package com.fave100.client.pages.login;

import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pages.PagePresenter;
import com.fave100.client.place.NameTokens;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * A login page
 * 
 * @author yissachar.radcliffe
 * 
 */
public class LoginPresenter extends
		PagePresenter<LoginPresenter.MyView, LoginPresenter.MyProxy> {

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private LoginWidgetPresenter loginContainer;

	@ProxyCodeSplit
	@NameToken(NameTokens.login)
	@UseGatekeeper(NotLoggedInGatekeeper.class)
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
