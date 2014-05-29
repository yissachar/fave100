package com.fave100.client.pages;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class MainView extends ViewImpl implements MainPresenter.MyView {
	interface Binder extends UiBinder<Widget, MainView> {
	}

	@UiField SimplePanel main;
	@UiField SimplePanel topBar;
	@UiField HTMLPanel playlist;

	@Inject
	MainView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(Object slot, IsWidget content) {
		if (slot == MainPresenter.MAIN_SLOT) {
			main.setWidget(content);
		}
		else if (slot == MainPresenter.TOP_BAR_SLOT) {
			topBar.setWidget(content);
		}
		else if (slot == MainPresenter.PLAYLIST_SLOT) {
			playlist.clear();
			playlist.add(content);
		}
		else {
			super.setInSlot(slot, content);
		}
	}
}
