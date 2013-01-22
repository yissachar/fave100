package com.fave100.client.pages.login;

import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class LoginPresenter extends
		BasePresenter<LoginPresenter.MyView, LoginPresenter.MyProxy> {

	public interface MyView extends BaseView {
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	@Inject LoginWidgetPresenter loginContainer;

	@ProxyCodeSplit
	@NameToken(NameTokens.login)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {
	}

	@Inject
	public LoginPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(LOGIN_SLOT, loginContainer);
	}
}
