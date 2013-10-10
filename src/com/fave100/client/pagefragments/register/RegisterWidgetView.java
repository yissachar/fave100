package com.fave100.client.pagefragments.register;

import com.fave100.shared.Constants;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RegisterWidgetView extends ViewWithUiHandlers<RegisterWidgetUiHandlers> implements RegisterWidgetPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, RegisterWidgetView> {
	}

	@UiField FormPanel registerForm;
	@UiField SubmitButton registerButton;
	@UiField TextBox usernameField;
	@UiField PasswordTextBox passwordField;
	@UiField PasswordTextBox passwordRepeatField;
	@UiField Anchor registerWithGoogleButton;
	@UiField SpanElement usernameStatusMessage;
	@UiField SpanElement passwordStatusMessage;
	@UiField TextBox emailField;
	@UiField SpanElement emailStatusMessage;
	@UiField Anchor registerWithTwitterButton;
	@UiField Anchor registerWithFacebookButton;

	@Inject
	public RegisterWidgetView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		usernameField.getElement().setAttribute("maxlength", Integer.toString(Constants.MAX_USERNAME_LENGTH));
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("registerForm")
	void onRegisterFormSubmit(final SubmitEvent event) {
		event.cancel();
		getUiHandlers().register(usernameField.getValue(),
				emailField.getValue(), passwordField.getValue(),
				passwordRepeatField.getValue());
	}

	@UiHandler("registerWithTwitterButton")
	void onRegisterWithTwitterButtonClick(final ClickEvent event) {
		getUiHandlers().goToTwitterAuth();
	}

	@Override
	public void clearFields() {
		usernameField.setValue("");
		emailField.setValue("");
		passwordField.setValue("");
		passwordRepeatField.setValue("");
	}

	@Override
	public void setNativeUsernameError(final String error) {
		usernameStatusMessage.setInnerText(error);
		usernameField.addStyleName("errorInput");
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

	@Override
	public void setUsernameFocus() {
		usernameField.setFocus(true);
	}
}
