package com.fave100.client.pages.lists.widgets.favelist.widgets;

import static com.google.gwt.query.client.GQuery.$;

import com.fave100.client.pages.lists.widgets.favelist.FavelistUiHandlers;
import com.fave100.client.resources.css.GlobalStyle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.query.client.Function;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FavePickRerankPanel extends Composite {

	private static FavePickRerankPanelUiBinder uiBinder = GWT.create(FavePickRerankPanelUiBinder.class);

	interface FavePickRerankPanelUiBinder extends UiBinder<Widget, FavePickRerankPanel> {
	}

	interface FavePickRerankStyle extends GlobalStyle {
		String arrowPanel();
	}

	private FavePickWidget _favePickWidget;
	private FavelistUiHandlers _uiHandlers;

	public FavePickRerankPanel(FavePickWidget favePickWidget, FavelistUiHandlers uiHandlers) {
		_favePickWidget = favePickWidget;
		_uiHandlers = uiHandlers;

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("upArrow")
	void onUpArrowClick(ClickEvent event) {
		_uiHandlers.changeSongPosition(_favePickWidget.getSongID(), _favePickWidget.getRank() - 1, _favePickWidget.getRank() - 2);
	}

	@UiHandler("downArrow")
	void onDownArrowClick(ClickEvent event) {
		_uiHandlers.changeSongPosition(_favePickWidget.getSongID(), _favePickWidget.getRank() - 1, _favePickWidget.getRank());
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		$(_favePickWidget).slideUp(new Function() {
			@Override
			public void f() {
				_favePickWidget.removeFromParent();
			}
		});
		_uiHandlers.removeSong(_favePickWidget.getSongID(), _favePickWidget.getRank() - 1);
	}

}
