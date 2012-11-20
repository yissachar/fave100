package com.fave100.client.pages.passwordreset;

import com.fave100.client.pages.BasePresenter;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class PasswordResetView extends ViewWithUiHandlers<PasswordResetUiHandlers> implements
		PasswordResetPresenter.MyView {

	private final Widget widget;
	@UiField HTMLPanel topBar;
	@UiField FormPanel sendTokenForm;
	@UiField TextBox usernameInput;
	@UiField TextBox emailInput;
	@UiField Label tokenStatusMessage;
	@UiField SubmitButton sendTokenButton;
	@UiField FormPanel changePasswordForm;
	@UiField PasswordTextBox passwordInput;
	@UiField PasswordTextBox passwordRepeat;
	@UiField Label pwdStatusMessage;

	public interface Binder extends UiBinder<Widget, PasswordResetView> {
	}

	@Inject
	public PasswordResetView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		showSendTokenForm();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if(content != null) {
				topBar.add(content);
			}
		}
	}

	@UiHandler("sendTokenForm")
	public void onTokenSubmit(final SubmitEvent event) {
		// Clear error text
		tokenStatusMessage.setText("");
		getUiHandlers().sendEmail(usernameInput.getValue(), emailInput.getValue());
	}
// TODO: Validate password! Check that matches repeat... etc.
	@UiHandler("changePasswordForm")
	public void onPasswordSubmit(final SubmitEvent event) {
		getUiHandlers().changePassword(passwordInput.getValue());
	}

	@Override
	public void showPwdChangeForm() {
		changePasswordForm.reset();
		pwdStatusMessage.setText("");
		sendTokenForm.setVisible(false);
		changePasswordForm.setVisible(true);
	}

	@Override
	public void showSendTokenForm() {
		sendTokenForm.reset();
		tokenStatusMessage.setText("");
		sendTokenButton.setVisible(true);
		sendTokenForm.setVisible(true);
		changePasswordForm.setVisible(false);
	}

	@Override
	public void showTokenError() {
		tokenStatusMessage.setText("Incorrect username or email");
		tokenStatusMessage.addStyleName("error");
	}

	@Override
	public void showTokenSuccess() {
		String msg = "An email has been sent to your email address.";
		msg += "Please follow the instructions contained within.";
		tokenStatusMessage.setText(msg);
		tokenStatusMessage.removeStyleName("error");
		sendTokenButton.setVisible(false);
	}

	@Override
	public void showPwdError() {
		pwdStatusMessage.setText("Token expired or doesn't exist");
		pwdStatusMessage.addStyleName("error");
	}

}
