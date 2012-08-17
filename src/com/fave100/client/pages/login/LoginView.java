package com.fave100.client.pages.login;

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

public class LoginView extends ViewImpl implements LoginPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, LoginView> {
	}

	@Inject
	public LoginView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == LoginPresenter.TOP_BAR_SLOT) {
			topBar.clear();			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@UiField Button loginButton;

	@Override
	public Button getLoginButton() {
		return loginButton;
	}
	
	@UiField TextBox usernameInput;

	@Override
	public TextBox getUsernameInput() {
		return usernameInput;
	}
	
	@UiField PasswordTextBox passwordInput;

	@Override
	public PasswordTextBox getPasswordInput() {
		return passwordInput;
	}

	@UiField SpanElement loginStatusMessage;
	
	@Override
	public SpanElement getLoginStatusMessage() {
		return loginStatusMessage;
	}
	
	@UiField Anchor signInWithGoogleButton;

	@Override
	public Anchor getSignInWithGoogleButton() {
		return signInWithGoogleButton;
	}
	
	@UiField Anchor signInWithTwitterButton;
	
	@Override
	public Anchor getSignInWithTwitterButton() {
		return signInWithTwitterButton;
	}
}
