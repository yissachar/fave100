package com.fave100.client.pages.home;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class HomeView extends ViewWithUiHandlers<HomeUiHandlers> implements HomePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, HomeView> {
	}

	@UiField Button registerButton;
	@UiField Button loginButton;
	@UiField Button exploreButton;

	@Inject
	public HomeView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		super.setInSlot(slot, content);
	}

	@UiHandler("registerButton")
	void onLoginClick(final ClickEvent event) {
		getUiHandlers().showRegister();
	}

	@UiHandler("loginButton")
	void onRegisterClick(final ClickEvent event) {
		getUiHandlers().showLogin();
	}

	@UiHandler("exploreButton")
	void onExploreClick(final ClickEvent event) {
		getUiHandlers().explore();
	}
}
