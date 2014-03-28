package com.fave100.client.pages.lists.widgets.favelist.widgets;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.Notification;
import com.fave100.client.events.favelist.RankInputUnfocusEvent;
import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter.ItemAdded;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter.ItemDeleted;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter.RankChanged;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter.WhyLineChanged;
import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.resources.img.ImageResources;
import com.fave100.client.widgets.helpbubble.HelpBubble;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.query.client.Function;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.client.ClientUrlUtils;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class FavePickWidget extends Composite {

	private static final String CLICK_TO_ENTER_WHY_LINE = "Click to enter WhyLine";
	private static Binder uiBinder = GWT.create(Binder.class);

	public interface Binder extends UiBinder<Widget, FavePickWidget> {
	}

	public interface FavePickWidgetStyle extends GlobalStyle {
		String hoverPanel();

		String hoverPanelDeleteButton();

		String arrowPanel();

		String whyLinePanel();

		String whyLineTextBox();

		String whyLinePanelEmpty();

		String rank();

		String rankEditPanel();

		String rankEditTextBox();

		String rankThreeDigit();
	}

	@UiField HTMLPanel container;
	@UiField FavePickWidgetStyle style;
	@UiField Panel mainPanel;
	@UiField SimplePanel rankPanel;
	@UiField Anchor song;
	@UiField InlineLabel artist;
	@UiField SimplePanel whyLinePanel;
	@UiField Panel hoverPanel;

	private EventBus _eventBus;
	private String _song;
	private String _artist;
	private String _whyline;
	private int _rank;
	private String _songID;
	private String _username;
	private String _hashtag;
	private final boolean _editable;
	private WhyLineChanged _whyLineCallback;
	private RankChanged _rankCallback;
	private ItemDeleted _deletedCallback;
	private ItemAdded _addedCallback;
	private Label whyLineLabel;
	private HelpBubble whylineHelpBubble;
	private HelpBubble rankHelpBubble;
	private ImageResources resources = GWT.create(ImageResources.class);
	// Keep track of how many times user re-ranks with arrows and prompt them to use re-rank box instead
	private int arrowRankCount = 0;

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

	public FavePickWidget(final EventBus eventBus, final FaveItem item, final int rank, final boolean editable, final WhyLineChanged whyLineChanged, final RankChanged rankChanged,
							final ItemDeleted itemDeleted, final ItemAdded itemAdded, final String username, final String hashtag) {
		_eventBus = eventBus;
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
		_username = username;
		_hashtag = hashtag;

		initWidget(uiBinder.createAndBindUi(this));

		fillWidget();
	}

	private void fillWidget() {
		setupRankPanel();

		song.setText(getSong());
		if (_username.isEmpty()) {
			song.setHref("#" + new ParameterTokenFormatter(new ClientUrlUtils())
					.toPlaceToken(new PlaceRequest.Builder()
							.nameToken(NameTokens.song)
							.with(SongPresenter.ID_PARAM, getSongID())
							.with(SongPresenter.LIST_PARAM, getHashtag())
							.build()));
		}
		else {
			song.setHref("#" + new ParameterTokenFormatter(new ClientUrlUtils())
					.toPlaceToken(new PlaceRequest.Builder()
							.nameToken(NameTokens.song)
							.with(SongPresenter.ID_PARAM, getSongID())
							.with(SongPresenter.USER_PARAM, getUsername())
							.with(SongPresenter.LIST_PARAM, getHashtag())
							.build()));
		}

		artist.setText(getArtist());

		whyLineLabel = new Label(getWhyline());
		if (_editable) {
			if (whyLineLabel.getText().isEmpty()) {
				initEmptyWhyLine(whyLineLabel);
			}
			setupWhyLineEdit(whyLineLabel);
		}
		whyLinePanel.setWidget(whyLineLabel);

		setupHoverPanel();
		hoverPanel.addStyleName(style.hoverPanel());

	}

	private void setupRankPanel() {
		rankPanel.clear();
		rankPanel.addStyleName(style.rank());

		if (_rank >= 100) {
			rankPanel.addStyleName(style.rankThreeDigit());
		}
		else {
			rankPanel.removeStyleName(style.rankThreeDigit());
		}

		_songPick = new Label(Integer.toString(_rank));
		rankPanel.setWidget(_songPick);

		if (_editable)
			setupRankEdit();
	}

	private void setupRankEdit() {
		rankPanel.addStyleName(style.rankEditPanel());
		_songPick.setTitle("Click to change rank");
		_songPick.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				rankPanel.removeStyleName(style.rankEditPanel());
				final TextBox rankText = new TextBox();
				rankText.addStyleName(style.rankEditTextBox());
				rankText.addStyleName(style.rank());

				if (_rank >= 100) {
					rankText.addStyleName(style.rankThreeDigit());
				}
				else {
					rankText.removeStyleName(style.rankThreeDigit());
				}

				rankText.setText(_songPick.getText());
				rankText.addKeyDownHandler(new KeyDownHandler() {

					@Override
					public void onKeyDown(final KeyDownEvent event) {
						final int keyCode = event.getNativeKeyCode();
						// Only allow numbers and special keys
						if (event.getNativeEvent().getShiftKey() ||
								((!((keyCode >= 48 && keyCode <= 57)) && !((keyCode >= 96 && keyCode <= 105)))
										&& (keyCode != (char)KeyCodes.KEY_TAB)
										&& (keyCode != (char)KeyCodes.KEY_BACKSPACE)
										&& (keyCode != (char)KeyCodes.KEY_ESCAPE)
										&& (keyCode != (char)KeyCodes.KEY_DELETE)
										&& (keyCode != (char)KeyCodes.KEY_ENTER)
										&& (keyCode != (char)KeyCodes.KEY_HOME)
										&& (keyCode != (char)KeyCodes.KEY_END)
										&& (keyCode != (char)KeyCodes.KEY_UP)
										&& (keyCode != (char)KeyCodes.KEY_DOWN)
										&& (keyCode != (char)KeyCodes.KEY_LEFT)
										&& (keyCode != (char)KeyCodes.KEY_RIGHT))) {

							event.preventDefault();
							event.stopPropagation();
						}

						if (keyCode == KeyCodes.KEY_ENTER) {
							updateRank(rankText);
							_eventBus.fireEvent(new RankInputUnfocusEvent());
						}
						else if (keyCode == KeyCodes.KEY_ESCAPE) {
							_eventBus.fireEvent(new RankInputUnfocusEvent());
						}
					}
				});
				rankText.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(final BlurEvent event) {
						updateRank(rankText);
						_eventBus.fireEvent(new RankInputUnfocusEvent());
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
					arrowRankCount = 0;
					if (rankHelpBubble != null) {
						rankHelpBubble.setVisible(false);
					}
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
		whyLine.addStyleName(style.whyLinePanelEmpty());
		whyLine.setText(" "); //setting this so white-space: pre will always have spacing for why line
		_whyLineMouseHandlers = new ArrayList<HandlerRegistration>();
		_whyLineMouseHandlers.add(whyLine.addMouseOverHandler(_whyLineEmptyMouseOver));
		_whyLineMouseHandlers.add(whyLine.addMouseOutHandler(_whyLineEmptyMouseOut));
	}

	private void setupWhyLineEdit(final Label whyLine) {
		whyLinePanel.addStyleName(style.whyLinePanel());
		whyLine.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				removeMouseHandlers();

				whyLinePanel.removeStyleName(style.whyLinePanel());
				final TextBox txtBox = new TextBox();
				txtBox.setMaxLength(Constants.MAX_WHYLINE_LENGTH);
				txtBox.addStyleName(style.whyLineTextBox());
				if (whyLine.getText().equals(CLICK_TO_ENTER_WHY_LINE))
					txtBox.setValue("");
				else
					txtBox.setValue(whyLine.getText().trim());
				txtBox.addKeyDownHandler(new KeyDownHandler() {

					@Override
					public void onKeyDown(final KeyDownEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							if (whylineHelpBubble != null) {
								whylineHelpBubble.setVisible(false);
							}
							saveAndSwitchToLabel(txtBox);
						}
					}
				});
				txtBox.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(final BlurEvent event) {
						if (whylineHelpBubble != null) {
							whylineHelpBubble.setVisible(false);
						}
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
						whyLine.removeStyleName(style.whyLinePanelEmpty());
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
				whyLinePanel.addStyleName(style.whyLinePanel());
			}

		});
	}

	private void setupHoverPanel() {
		hoverPanel.clear();
		if (_editable) {
			final Panel arrowPanel = new FlowPanel();
			arrowPanel.addStyleName(style.arrowPanel());
			hoverPanel.add(arrowPanel);

			final Image upButton = new Image(resources.upArrow());
			upButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					arrowRankChange(_rank - 2);
				}
			});
			upButton.addStyleName("rankUpArrow");
			arrowPanel.add(upButton);

			final Image downButton = new Image(resources.downArrow());
			downButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					arrowRankChange(_rank);
				}
			});
			downButton.addStyleName("rankDownArrow");
			arrowPanel.add(downButton);

			final Panel deleteButtonPanel = new SimplePanel();
			final Image deleteButton = new Image(resources.delete());
			deleteButton.setTitle("Delete song");
			deleteButtonPanel.addStyleName(style.hoverPanelDeleteButton());
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
			deleteButtonPanel.add(deleteButton);
			hoverPanel.add(deleteButtonPanel);
		}
		else {
			final Image addButton = new Image(resources.add());
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

	private void arrowRankChange(int newIndex) {
		arrowRankCount++;
		if (arrowRankCount >= 3) {
			arrowRankCount = 0;
			showRankWhylineHelpBubble();
		}

		_rankCallback.onChange(getSongID(), _rank - 1, newIndex);
	}

	public void showWhylineHelpBubble() {
		final String whylineText = "You can add an 80 character Why-Line here, explaining why this song is in your Fave100";
		whylineHelpBubble = new HelpBubble("Why-Line", whylineText, 400, HelpBubble.Direction.UP);
		container.add(whylineHelpBubble);
	}

	public void showRankWhylineHelpBubble() {
		final String rankText = "You can change the rank of your songs here";
		rankHelpBubble = new HelpBubble("Rank", rankText, 300, HelpBubble.Direction.LEFT);
		container.add(rankHelpBubble);
		rankHelpBubble.setArrowPos(30);
	}

	public void focusWhyline() {
		whyLineLabel.fireEvent(new ClickEvent() {
		});
	}

	public void focusRank() {
		_songPick.fireEvent(new ClickEvent() {
		});
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

	public String getUsername() {
		return _username;
	}

	public void setUsername(final String _username) {
		this._username = _username;
	}

	public String getHashtag() {
		return _hashtag;
	}

	public void setHashtag(final String hashtag) {
		_hashtag = hashtag;
	}
}
