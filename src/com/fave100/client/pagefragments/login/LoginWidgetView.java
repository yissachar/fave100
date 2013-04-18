package com.fave100.client.pagefragments.login;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class LoginWidgetView extends ViewWithUiHandlers<LoginUiHandlers>
		implements LoginWidgetPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, LoginWidgetView> {
	}

	@UiField FormPanel loginForm;
	@UiField TextBox usernameInput;
	@UiField PasswordTextBox passwordInput;
	@UiField Label loginStatusMessage;
	@UiField Anchor signInWithGoogleButton;
	@UiField Anchor signInWithTwitterButton;
	@UiField Anchor signInWithFacebookButton;
	@UiField Button loginButton;

	@Inject
	public LoginWidgetView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("loginForm")
	public void onSubmit(final SubmitEvent event) {
		getUiHandlers().login();
	}

	@UiHandler("signInWithTwitterButton")
	public void onClick(final ClickEvent event) {
		getUiHandlers().goToTwitterAuth();
	}

	@Override
	public void setError(final String error) {
		loginStatusMessage.setText(error);
	}

	@Override
	public void clearUsername() {
		usernameInput.setValue("");
	}

	@Override
	public void clearPassword() {
		passwordInput.setValue("");
		loginStatusMessage.setText("");
	}

	@Override
	public String getUsername() {
		return usernameInput.getValue();
	}

	@Override
	public String getPassword() {
		return passwordInput.getValue();
	}

	@Override
	public void setGoogleLoginUrl(final String url) {
		signInWithGoogleButton.setHref(url);
	}

	@Override
	public void setFacebookLoginUrl(final String url) {
		signInWithFacebookButton.setHref(url);

	}

}
