package com.fave100.client.pages.register;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RegisterView extends ViewWithUiHandlers<RegisterUiHandlers>
		implements RegisterPresenter.MyView {

	private final Widget	widget;

	public interface Binder extends UiBinder<Widget, RegisterView> {
	}

	@UiField
	HTMLPanel		registerContainer;
	@UiField
	FormPanel		registerForm;
	@UiField
	Button			registerButton;
	@UiField
	TextBox			usernameField;
	@UiField
	PasswordTextBox	passwordField;
	@UiField
	PasswordTextBox	passwordRepeatField;
	@UiField
	Anchor			registerWithGoogleButton;
	@UiField
	SpanElement		usernameStatusMessage;
	@UiField
	SpanElement		thirdPartyUsernameStatusMessage;
	@UiField
	TextBox			thirdPartyUsernameField;
	@UiField
	SpanElement		passwordStatusMessage;
	@UiField
	TextBox			emailField;
	@UiField
	SpanElement		emailStatusMessage;
	@UiField
	HTMLPanel		thirdPartyUsernamePrompt;
	@UiField
	Button			thirdPartyUsernameSubmitButton;
	@UiField
	Anchor			registerWithTwitterButton;
	@UiField
	Anchor			registerWithFacebookButton;

	@Inject
	public RegisterView(final Binder binder) {
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

	@UiHandler("registerForm")
	void onRegisterFormSubmit(final SubmitEvent event) {
		getUiHandlers().register(usernameField.getValue(),
				emailField.getValue(), passwordField.getValue(),
				passwordRepeatField.getValue());
	}

	@UiHandler("registerWithTwitterButton")
	void onRegisterWithTwitterButtonClick(final ClickEvent event) {
		getUiHandlers().goToTwitterAuth();
	}

	@UiHandler("thirdPartyUsernameSubmitButton")
	void onThirdPartyRegisterClick(final ClickEvent event) {
		getUiHandlers().registerThirdParty(thirdPartyUsernameField.getValue());
	}

	@Override
	public void clearFields() {
		usernameField.setValue("");
		emailField.setValue("");
		passwordField.setValue("");
		passwordRepeatField.setValue("");
		thirdPartyUsernameField.setValue("");

	}

	@Override
	public void showThirdPartyUsernamePrompt() {
		thirdPartyUsernamePrompt.setVisible(true);
		registerContainer.setVisible(false);
	}

	@Override
	public void hideThirdPartyUsernamePrompt() {
		thirdPartyUsernamePrompt.setVisible(false);
		registerContainer.setVisible(true);
	}

	@Override
	public void setNativeUsernameError(final String error) {
		usernameStatusMessage.setInnerText(error);
		usernameField.addStyleName("errorInput");
	}

	@Override
	public void setThirdPartyUsernameError(final String error) {
		thirdPartyUsernameStatusMessage.setInnerText(error);
		thirdPartyUsernameField.addStyleName("errorInput");
	}

	@Override
	public void setEmailError(final String error) {
		emailStatusMessage.setInnerText(error);
		emailField.addStyleName("errorInput");

	}

	@Override
	public void setPasswordError(final String error) {
		passwordStatusMessage.setInnerText(error);
		passwordField.addStyleName("errorInput");
	}

	@Override
	public void setPasswordRepeatError(final String error) {
		passwordStatusMessage.setInnerText(error);
		passwordField.addStyleName("errorInput");
		passwordRepeatField.addStyleName("errorInput");

	}

	@Override
	public void clearThirdPartyErrors() {
		thirdPartyUsernameField.removeStyleName("errorInput");
	}

	@Override
	public void clearNativeErrors() {
		usernameField.removeStyleName("errorInput");
		passwordField.removeStyleName("errorInput");
		emailField.removeStyleName("errorInput");
		passwordRepeatField.removeStyleName("errorInput");
		usernameStatusMessage.setInnerText("");
		passwordStatusMessage.setInnerText("");
		emailStatusMessage.setInnerText("");
	}

	@Override
	public void setGoogleUrl(final String url) {
		registerWithGoogleButton.setHref(url);
	}

	@Override
	public void setFacebookUrl(final String url) {
		registerWithFacebookButton.setHref(url);
	}
}
