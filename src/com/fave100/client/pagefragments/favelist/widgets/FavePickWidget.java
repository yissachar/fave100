package com.fave100.client.pagefragments.favelist.widgets;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.Notification;
import com.fave100.client.pagefragments.favelist.FavelistPresenter.ItemAdded;
import com.fave100.client.pagefragments.favelist.FavelistPresenter.ItemDeleted;
import com.fave100.client.pagefragments.favelist.FavelistPresenter.RankChanged;
import com.fave100.client.pagefragments.favelist.FavelistPresenter.WhyLineChanged;
import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.Validator;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.query.client.Function;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FavePickWidget extends Composite {

	private static final String RANK_EDIT_PANEL = "rankEditPanel";
	private static final String RANK_EDIT_TEXT_BOX = "rankEditTextBox";
	private static final String CLICK_TO_ENTER_WHY_LINE = "Click to enter WhyLine";
	private static final String WHY_LINE_EDIT_HOVER = "whyLinePanel";
	private static Binder uiBinder = GWT.create(Binder.class);

	public interface Binder extends UiBinder<Widget, FavePickWidget> {
	}

	public interface FavePickWidgetStyle extends CssResource {
		String hoverPanel();
	}

	@UiField FavePickWidgetStyle style;
	@UiField HorizontalPanel mainPanel;
	@UiField SimplePanel rankPanel;
	@UiField Anchor song;
	@UiField InlineLabel artist;
	@UiField SimplePanel whyLinePanel;
	@UiField HorizontalPanel hoverPanel;

	private String _song;
	private String _artist;
	private String _whyline;
	private int _rank;
	private String _songID;
	private final boolean _editable;
	private WhyLineChanged _whyLineCallback;
	private RankChanged _rankCallback;
	private ItemDeleted _deletedCallback;
	private ItemAdded _addedCallback;

	private final MouseOverHandler _whyLineEmptyMouseOver = new MouseOverHandler() {

		@Override
		public void onMouseOver(final MouseOverEvent event) {
			((HasText)event.getSource()).setText(CLICK_TO_ENTER_WHY_LINE);
		}
	};
	private final MouseOutHandler _whyLineEmptyMouseOut = new MouseOutHandler() {

		@Override
		public void onMouseOut(final MouseOutEvent event) {
			((HasText)event.getSource()).setText(" ");
		}
	};

	private List<HandlerRegistration> _whyLineMouseHandlers;
	private Label _songPick;

	public FavePickWidget(final FaveItemProxy item, final int rank, final boolean editable, final WhyLineChanged whyLineChanged, final RankChanged rankChanged, final ItemDeleted itemDeleted,
							final ItemAdded itemAdded) {
		_song = item.getSong();
		_artist = item.getArtist();
		_whyline = item.getWhyline();
		_rank = rank;
		_songID = item.getSongID();
		_editable = editable;
		_whyLineCallback = whyLineChanged;
		_rankCallback = rankChanged;
		_deletedCallback = itemDeleted;
		_addedCallback = itemAdded;

		initWidget(uiBinder.createAndBindUi(this));

		fillWidget();

		mainPanel.addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(final MouseOverEvent event) {
				hoverPanel.removeStyleName("hide");
			}
		}, MouseOverEvent.getType());

		mainPanel.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(final MouseOutEvent event) {
				hoverPanel.addStyleName("hide");
			}
		}, MouseOutEvent.getType());
	}

	private void fillWidget() {
		setupRankPanel();

		song.setText(getSong());
		song.setHref("#" + new UrlBuilder(NameTokens.song).with(SongPresenter.ID_PARAM, getSongID()).getPlaceToken());

		artist.setText(getArtist());

		final Label whyLine = new Label(getWhyline());
		if (_editable) {
			if (whyLine.getText().isEmpty()) {
				initEmptyWhyLine(whyLine);
			}
			setupWhyLineEdit(whyLine);
		}
		whyLinePanel.setWidget(whyLine);

		setupHoverPanel();
		hoverPanel.addStyleName(style.hoverPanel());

	}

	private void setupRankPanel() {
		rankPanel.clear();
		_songPick = new Label(Integer.toString(_rank));
		rankPanel.setWidget(_songPick);
		if (_editable)
			setupRankEdit();
	}

	private void setupRankEdit() {
		rankPanel.addStyleName(RANK_EDIT_PANEL);
		_songPick.setTitle("Click to change rank");
		_songPick.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				rankPanel.removeStyleName(RANK_EDIT_PANEL);
				final TextBox rankText = new TextBox();
				rankText.addStyleName(RANK_EDIT_TEXT_BOX);
				rankText.setText(_songPick.getText());
				rankText.addKeyDownHandler(new KeyDownHandler() {

					@Override
					public void onKeyDown(final KeyDownEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
							updateRank(rankText);
					}
				});
				rankText.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(final BlurEvent event) {
						updateRank(rankText);
					}
				});
				// Only allow numbers in rankText
				rankText.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(final KeyPressEvent event) {
						final TextBox sender = (TextBox)event.getSource();

						if (sender.isReadOnly() || !sender.isEnabled()) {
							return;
						}

						final Character charCode = event.getCharCode();

						// allow digits
						if (!Character.isDigit(charCode)) {
							sender.cancelKey();
						}
					}
				});
				rankPanel.clear();
				rankPanel.setWidget(rankText);
				rankText.setFocus(true);
				rankText.selectAll();
			}

			private void updateRank(final TextBox rankText) {
				try {
					final int _currentRank = _rank;
					_rank = Integer.parseInt(rankText.getText());
					_rankCallback.onChange(getSongID(), _currentRank - 1, _rank - 1);
				}
				catch (final NumberFormatException ex) {

				}
				finally {
					setupRankPanel();
				}
			}

		});
	}

	private void initEmptyWhyLine(final Label whyLine) {
		whyLine.setText(" "); //setting this so white-space: pre will always have spacing for why line
		_whyLineMouseHandlers = new ArrayList<HandlerRegistration>();
		_whyLineMouseHandlers.add(whyLine.addMouseOverHandler(_whyLineEmptyMouseOver));
		_whyLineMouseHandlers.add(whyLine.addMouseOutHandler(_whyLineEmptyMouseOut));
	}

	private void setupWhyLineEdit(final Label whyLine) {
		whyLinePanel.addStyleName(WHY_LINE_EDIT_HOVER);
		whyLine.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				removeMouseHandlers();

				whyLinePanel.removeStyleName(WHY_LINE_EDIT_HOVER);
				final TextBox txtBox = new TextBox();
				txtBox.addStyleName("whyLineTextBox");
				if (whyLine.getText().equals(CLICK_TO_ENTER_WHY_LINE))
					txtBox.setValue("");
				else
					txtBox.setValue(whyLine.getText().trim());
				txtBox.setWidth("500px");
				txtBox.addKeyDownHandler(new KeyDownHandler() {

					@Override
					public void onKeyDown(final KeyDownEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							saveAndSwitchToLabel(txtBox);
						}
					}
				});
				txtBox.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(final BlurEvent event) {
						saveAndSwitchToLabel(txtBox);
					}
				});

				whyLinePanel.clear();
				whyLinePanel.setWidget(txtBox);
				txtBox.selectAll();
				txtBox.setFocus(true);
			}

			private void removeMouseHandlers() {
				if (_whyLineMouseHandlers != null) {
					for (final HandlerRegistration handler : _whyLineMouseHandlers)
						handler.removeHandler();

					_whyLineMouseHandlers.clear();
				}

			}

			private void saveAndSwitchToLabel(final TextBox txtBox) {
				// Errors with whyline, show them
				final String error = Validator.validateWhyline(txtBox.getValue());
				if (error != null) {
					Notification.show(error, true);
				}
				else {
					if (!txtBox.getValue().trim().equals(getWhyline())) {
						_whyLineCallback.onChange(getSongID(), txtBox.getValue());
						setWhyline(txtBox.getValue());
					}

					if (txtBox.getValue().isEmpty()) {
						initEmptyWhyLine(whyLine);
					}
					else {
						whyLine.setText(txtBox.getValue());
					}
				}
				whyLinePanel.clear();
				whyLinePanel.setWidget(whyLine);
				whyLinePanel.addStyleName(WHY_LINE_EDIT_HOVER);
			}

		});
	}

	private void setupHoverPanel() {
		hoverPanel.clear();
		if (_editable) {
			final VerticalPanel arrowPanel = new VerticalPanel();
			hoverPanel.add(arrowPanel);

			final Image upButton = new Image("img/up-arrow.png");
			upButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					_rankCallback.onChange(getSongID(), _rank - 1, _rank - 2);
				}
			});
			arrowPanel.add(upButton);

			final Image downButton = new Image("img/down-arrow.png");
			downButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					_rankCallback.onChange(getSongID(), _rank - 1, _rank);
				}
			});
			arrowPanel.add(downButton);

			final Image deleteButton = new Image("img/delete.png");
			deleteButton.setTitle("Delete song");
			final FavePickWidget _this = this;
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					$(_this).slideUp(new Function() {
						@Override
						public void f() {
							removeFromParent();
						}
					});
					_deletedCallback.onDeleted(getSongID(), _rank - 1);
				}
			});
			hoverPanel.add(deleteButton);
		}
		else {
			final Image addButton = new Image("img/add.png");
			addButton.setTitle("Add to your Fave100");
			addButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					_addedCallback.onAdded(getSongID(), getSong(), getArtist());
				}
			});
			hoverPanel.add(addButton);
		}
	}

	/* Getters and Setters */

	public void setRank(final int rank) {
		_rank = rank;
		setupRankPanel();
	}

	public String getSong() {
		return _song;
	}

	public String getArtist() {
		return _artist;
	}

	public String getWhyline() {
		return _whyline;
	}

	public void setWhyline(final String _whyline) {
		this._whyline = _whyline;
	}

	public String getSongID() {
		return _songID;
	}

	public void set_songID(final String _songID) {
		this._songID = _songID;
	}
}
