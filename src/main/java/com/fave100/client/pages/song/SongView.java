package com.fave100.client.pages.song;

import com.fave100.client.pages.PageView;
import com.fave100.client.widgets.Icon;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class SongView extends PageView<SongUiHandlers> implements SongPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, SongView> {
	}

	@UiField Icon backButton;
	@UiField Label backText;
	private PlaceManager _placeManager;

	@Inject
	public SongView(final Binder binder, PlaceManager placeManager) {
		widget = binder.createAndBindUi(this);
		_placeManager = placeManager;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("backButton")
	void onBackButtonClick(ClickEvent event) {
		_placeManager.navigateBack();
	}

	@UiHandler("backText")
	void onBackTextClick(ClickEvent event) {
		_placeManager.navigateBack();
	}

}
