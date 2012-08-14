package com.fave100.client.pages.register;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class RegisterView extends ViewImpl implements RegisterPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, RegisterView> {
	}

	@Inject
	public RegisterView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == RegisterPresenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiField HTMLPanel registerContainer;
	
	@Override
	public HTMLPanel getRegisterContainer() {
		return registerContainer;
	}

	@UiField Button registerButton;
	
	@Override
	public Button getRegisterButton() {
		return registerButton;
	}

	@UiField TextBox usernameField;
	
	@Override
	public TextBox getUsernameField() {
		return usernameField;
	}
	
	@UiField PasswordTextBox passwordField;

	@Override
	public PasswordTextBox getPasswordField() {
		return passwordField;
	}
	
	@UiField PasswordTextBox passwordRepeatField;

	@Override
	public PasswordTextBox getPasswordRepeatField() {
		return passwordRepeatField;
	}
	
	@UiField Anchor registerWithGoogleButton;

	@Override
	public Anchor getRegisterWithGoogleButton() {
		return registerWithGoogleButton;
	}
	
	@UiField SpanElement usernameStatusMessage;

	@Override
	public SpanElement getUsernameStatusMessage() {
		return usernameStatusMessage;
	}
	
	@UiField SpanElement thirdPartyUsernameStatusMessage;

	@Override
	public SpanElement getThirdPartyUsernameStatusMessage() {
		return thirdPartyUsernameStatusMessage;
	}
	
	@UiField TextBox thirdPartyUsernameField;

	@Override
	public TextBox getThirdPartyUsernameField() {
		return thirdPartyUsernameField;
	}
	
	@UiField SpanElement passwordStatusMessage;

	@Override
	public SpanElement getPasswordStatusMessage() {
		return passwordStatusMessage;
	}

	@UiField TextBox emailField;
	
	@Override
	public TextBox getEmailField() {	
		return emailField;
	}
	
	@UiField SpanElement emailStatusMessage;

	@Override
	public SpanElement getEmailStatusMessage() {
		return emailStatusMessage;
	}
	
	@UiField HTMLPanel thirdPartyUsernamePrompt;

	@Override
	public HTMLPanel getThirdPartyUsernamePrompt() {
		return thirdPartyUsernamePrompt;
	}

	@UiField Button thirdPartyUsernameSubmitButton;
	
	@Override
	public Button getThirdPartyUsernameSubmitButton() {		
		return thirdPartyUsernameSubmitButton;
	}
}
