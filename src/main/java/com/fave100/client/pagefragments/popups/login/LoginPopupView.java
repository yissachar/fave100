package com.fave100.client.pagefragments.popups.login;

import com.fave100.client.widgets.Icon;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;

public class LoginPopupView extends PopupViewImpl implements LoginPopupPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, LoginPopupView> {
	}

	@UiField HTMLPanel loginContainer;
	@UiField HTMLPanel registerContainer;
	@UiField Icon closeButton;
	@UiField Label viewToggleLink;
	private boolean loginView = true;

	@Inject
	public LoginPopupView(final EventBus eventBus, final Binder binder) {
		super(eventBus);
		widget = binder.createAndBindUi(this);
		setAutoHideOnNavigationEventEnabled(true);
		registerContainer.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("viewToggleLink")
	public void onViewToggleLinkClick(ClickEvent event) {
		event.preventDefault();
		if (loginView) {
			showRegister();
		}
		else {
			showLogin();
		}
	}

	@UiHandler("closeButton")
	void onCloseButtonClick(ClickEvent event) {
		hide();
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		if (slot == LoginPopupPresenter.LOGIN_SLOT) {
			loginContainer.clear();
			if (content != null) {
				loginContainer.add(content);
				loginContainer.addStyleName("fullLoginPage");
			}
		}
		else if (slot == LoginPopupPresenter.REGISTER_SLOT) {
			registerContainer.clear();
			if (content != null) {
				registerContainer.add(content);
				registerContainer.addStyleName("fullLoginPage");
			}
		}
	}

	@Override
	public void showRegister() {
		loginContainer.setVisible(false);
		registerContainer.setVisible(true);
		viewToggleLink.setText("Already have an account? Log in");
		loginView = false;
	}

	@Override
	public void showLogin() {
		loginContainer.setVisible(true);
		registerContainer.setVisible(false);
		viewToggleLink.setText("Don't have an account? Register");
		loginView = true;
	}
}
