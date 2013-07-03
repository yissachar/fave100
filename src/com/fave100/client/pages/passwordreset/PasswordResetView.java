package com.fave100.client.pages.passwordreset;

import com.fave100.client.pages.BasePresenter;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class PasswordResetView extends
		ViewWithUiHandlers<PasswordResetUiHandlers> implements
		PasswordResetPresenter.MyView {

	private final Widget widget;
	@UiField HTMLPanel topBar;
	@UiField FormPanel sendTokenForm;
	@UiField TextBox usernameInput;
	@UiField TextBox emailInput;
	@UiField Label tokenStatusMessage;
	@UiField SubmitButton sendTokenButton;
	@UiField FormPanel changePasswordForm;
	@UiField Label currPasswordLabel;
	@UiField PasswordTextBox currPasswordInput;
	@UiField Label currPwdStatusMsg;
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
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if (content != null) {
				topBar.add(content);
			}
		}
	}

	@UiHandler("sendTokenForm")
	public void onTokenSubmit(final SubmitEvent event) {
		// Clear error text
		event.cancel();
		tokenStatusMessage.setText("");
		getUiHandlers().sendEmail(usernameInput.getValue(),
				emailInput.getValue());
	}

	@UiHandler("changePasswordForm")
	public void onPasswordSubmit(final SubmitEvent event) {
		event.cancel();
		getUiHandlers().changePassword(passwordInput.getValue(),
				passwordRepeat.getValue(), currPasswordInput.getValue());
	}

	@Override
	public void showPwdChangeForm(final Boolean requireOldPwd) {
		changePasswordForm.reset();
		pwdStatusMessage.setText("");
		pwdStatusMessage.removeStyleName("errorInput");
		currPwdStatusMsg.setText("");
		currPasswordInput.removeStyleName("errorInput");
		passwordInput.removeStyleName("errorInput");
		sendTokenForm.setVisible(false);
		if (requireOldPwd) {
			currPasswordLabel.setVisible(true);
			currPasswordInput.setVisible(true);
			currPwdStatusMsg.setVisible(false);
		}
		else {
			currPasswordLabel.setVisible(false);
			currPasswordInput.setVisible(false);
			currPwdStatusMsg.setVisible(false);
		}
		changePasswordForm.setVisible(true);
	}

	@Override
	public void showSendTokenForm() {
		sendTokenForm.reset();
		tokenStatusMessage.setText("");
		tokenStatusMessage.removeStyleName("error");
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
		String msg = "An email has been sent to your email address. ";
		msg += "Please follow the instructions contained within. ";
		msg += "If you do not receive the email after several minutes ";
		msg += "please check your spam folder.";
		tokenStatusMessage.setText(msg);
		tokenStatusMessage.removeStyleName("error");
		sendTokenButton.setVisible(false);
	}

	@Override
	public void showPwdError(final String errorMsg, final Boolean inputError) {
		pwdStatusMessage.setText(errorMsg);
		pwdStatusMessage.addStyleName("error");
		if (inputError) {
			passwordInput.addStyleName("errorInput");
		}
		else {
			passwordInput.removeStyleName("errorInput");
		}
	}

	@Override
	public void showCurrPwdError(final String errorMsg) {
		currPasswordInput.addStyleName("errorInput");
		currPwdStatusMsg.setText(errorMsg);
	}

}
