package com.fave100.client.pages;

import javax.inject.Inject;

import com.fave100.shared.Constants;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
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

		resize();
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});
	}

	private void resize() {
		main.setHeight((Window.getClientHeight() - Constants.TOP_BAR_HEIGHT) + "px");
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
