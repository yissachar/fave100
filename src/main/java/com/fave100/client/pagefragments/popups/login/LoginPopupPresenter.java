package com.fave100.client.pagefragments.popups.login;

import com.fave100.client.CurrentUser;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class LoginPopupPresenter extends PresenterWidget<LoginPopupPresenter.MyView> {

	public interface MyView extends PopupView {

		void showRegister();

		void showLogin();
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> REGISTER_SLOT = new Type<RevealContentHandler<?>>();

	private CurrentUser _currentUser;
	@Inject private LoginWidgetPresenter loginContainer;
	@Inject private RegisterWidgetPresenter registerContainer;

	@Inject
	public LoginPopupPresenter(final EventBus eventBus, final MyView view, CurrentUser currentUser) {
		super(eventBus, view);
		_currentUser = currentUser;
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(LOGIN_SLOT, loginContainer);
		setInSlot(REGISTER_SLOT, registerContainer);
	}

	@Override
	protected void onHide() {
		_currentUser.setAfterLoginAction(null);
	}

	public void showLogin() {
		getView().showLogin();
		loginContainer.focus();
	}

	public void showRegister() {
		getView().showRegister();
		registerContainer.focus();
	}

}
