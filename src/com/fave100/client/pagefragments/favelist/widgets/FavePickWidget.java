package com.fave100.client.pagefragments.favelist.widgets;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.pagefragments.favelist.FavelistPresenter.WhyLineChanged;
import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.FaveItemProxy;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FavePickWidget extends Composite {

	private static final String RANK_EDIT_PANEL = "rankEditPanel";
	private static final String RANK_EDIT_TEXT_BOX = "rankEditTextBox";
	private static final String CLICK_TO_ENTER_WHY_LINE = "Click to enter WhyLine";
	private static final String WHY_LINE_EDIT_HOVER = "whyLinePanel";
	private static Binder uiBinder = GWT.create(Binder.class);

	public interface Binder extends UiBinder<Widget, FavePickWidget> {
	}

	@UiField HorizontalPanel mainPanel;
	@UiField SimplePanel rankPanel;
	@UiField Anchor song;
	@UiField InlineLabel artist;
	@UiField SimplePanel whyLinePanel;
	@UiField SimplePanel hoverPanel;

	private final FaveItemProxy _item;
	private int _rank;
	//TODO
	private final boolean _editable;
	private WhyLineChanged _whyLineCallback;

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

	public FavePickWidget(final FaveItemProxy item, final int rank, final boolean editable, final WhyLineChanged whyLineChanged) {
		_item = item;
		_rank = rank;
		_editable = editable;
		_whyLineCallback = whyLineChanged;

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

		song.setText(_item.getSong());
		song.setHref("#" + new UrlBuilder(NameTokens.song).with(SongPresenter.ID_PARAM, _item.getSongID()).getPlaceToken());

		artist.setText(_item.getArtist());

		final Label whyLine = new Label(_item.getWhyline());
		if (_editable) {
			if (whyLine.getText().isEmpty()) {
				initEmptyWhyLine(whyLine);
			}
			setupWhyLineEdit(whyLine);
		}
		whyLinePanel.setWidget(whyLine);

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
				rankPanel.clear();
				rankPanel.setWidget(rankText);
				rankText.setFocus(true);
				rankText.selectAll();
			}

			private void updateRank(final TextBox rankText) {
				//TODO: save and move
				try {
					_rank = Integer.parseInt(rankText.getText());
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
							saveAndSwithToLabel(txtBox);
						}
					}
				});
				txtBox.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(final BlurEvent event) {
						saveAndSwithToLabel(txtBox);
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

			private void saveAndSwithToLabel(final TextBox txtBox) {
				if (!txtBox.getValue().trim().equals(_item.getWhyline())) {
					_whyLineCallback.onChange(_item.getSongID(), txtBox.getValue());
				}

				if (txtBox.getValue().isEmpty()) {
					initEmptyWhyLine(whyLine);
				}
				else {
					whyLine.setText(txtBox.getValue());
				}
				whyLinePanel.clear();
				whyLinePanel.setWidget(whyLine);
				whyLinePanel.addStyleName(WHY_LINE_EDIT_HOVER);
			}

		});
	}
}