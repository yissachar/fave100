package com.fave100.client.pages;

import com.fave100.client.events.LoginDialogRequestedEvent;
import com.fave100.client.events.LoginDialogRequestedHandler;
import com.fave100.client.pagefragments.playlist.PlaylistPresenter;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.topbar.TopBarPresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class MainPresenter extends Presenter<MainPresenter.MyView, MainPresenter.MyProxy> {
	interface MyView extends View {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> MAIN_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> PLAYLIST_SLOT = new Type<RevealContentHandler<?>>();

	@Inject protected TopBarPresenter topBar;
	@Inject private PlaylistPresenter playlist;
	@Inject private LoginPopupPresenter loginBox;

	@ProxyStandard
	public interface MyProxy extends Proxy<MainPresenter> {
	}

	@Inject
	public MainPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
		super(eventBus, view, proxy, RevealType.Root);
	}

	@Override
	protected void onBind() {
		setInSlot(TOP_BAR_SLOT, topBar);
		setInSlot(PLAYLIST_SLOT, playlist);

		addRegisteredHandler(LoginDialogRequestedEvent.getType(), new LoginDialogRequestedHandler() {

			@Override
			public void onLoginDialogRequested() {
				addToPopupSlot(loginBox);
			}
		});
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	public void showLoginDialog() {
		addToPopupSlot(loginBox);
	}

}
