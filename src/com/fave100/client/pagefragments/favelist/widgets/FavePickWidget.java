package com.fave100.client.pagefragments.favelist.widgets;

import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FavePickWidget extends Composite {

	private static final String WHY_LINE_EDIT_HOVER = "whyLinePanel";
	private static Binder uiBinder = GWT.create(Binder.class);

	public interface Binder extends UiBinder<Widget, FavePickWidget> {
	}

	@UiField SimplePanel rankPanel;
	@UiField Anchor song;
	@UiField InlineLabel artist;
	@UiField SimplePanel whyLinePanel;

	private final FaveItemProxy _item;
	private final int _rank;
	//TODO
	private final boolean _editable;

	public FavePickWidget(final FaveItemProxy item, final int rank, final boolean editable) {
		_item = item;
		_rank = rank;
		_editable = editable;

		initWidget(uiBinder.createAndBindUi(this));

		fillWidget();
	}

	private void fillWidget() {
		//TODO: do editable
		final InlineLabel songPick = new InlineLabel(Integer.toString(_rank));
		rankPanel.setWidget(songPick);

		song.setText(_item.getSong());
		song.setHref("#" + new UrlBuilder(NameTokens.song).with(SongPresenter.ID_PARAM, _item.getSongID()).getPlaceToken());

		artist.setText(_item.getArtist());

		//TODO: editable
		//		whyLinePanel.setWidget(new Label(_item.getWhyline()));

		final Label whyLine = new Label("my why line goes here....");
		if (_editable) {
			setupWhyLineEdit(whyLine);
		}
		whyLinePanel.setWidget(whyLine);

	}

	private void setupWhyLineEdit(final Label whyLine) {
		//		whyLinePanel.addStyleName(WHY_LINE_EDIT_HOVER);
		whyLinePanel.addStyleName(WHY_LINE_EDIT_HOVER);
		whyLine.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				whyLinePanel.removeStyleName(WHY_LINE_EDIT_HOVER);
				final TextBox txtBox = new TextBox();
				txtBox.setValue(whyLine.getText());
				txtBox.setWidth("500px");
				txtBox.addKeyDownHandler(new KeyDownHandler() {

					@Override
					public void onKeyDown(final KeyDownEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							saveAndSwithToLabel(txtBox);
						}
					}
				});
				//				txtBox.addBlurHandler(new BlurHandler() {
				//
				//					@Override
				//					public void onBlur(final BlurEvent event) {
				//						saveAndSwithToLabel(txtBox);
				//					}
				//				});

				whyLinePanel.clear();
				whyLinePanel.setWidget(txtBox);
				txtBox.selectAll();
				txtBox.setFocus(true);
			}

			private void saveAndSwithToLabel(final TextBox txtBox) {
				//TODO: save
				whyLine.setText(txtBox.getValue());
				whyLinePanel.clear();
				whyLinePanel.setWidget(whyLine);
				whyLinePanel.addStyleName(WHY_LINE_EDIT_HOVER);
			}

		});
	}
}
