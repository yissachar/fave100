package com.fave100.client.pagefragments.popups.login;

import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class LoginPopupPresenter extends PresenterWidget<LoginPopupPresenter.MyView> {

	public interface MyView extends PopupView {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private LoginWidgetPresenter loginContainer;

	@Inject
	public LoginPopupPresenter(
								final EventBus eventBus,
								final MyView view) {
		super(eventBus, view);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(LOGIN_SLOT, loginContainer);
		loginContainer.setShortNames(true);
	}
}
