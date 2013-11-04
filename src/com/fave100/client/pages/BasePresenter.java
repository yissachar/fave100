package com.fave100.client.pages;

import com.fave100.client.pagefragments.topbar.TopBarPresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailHandler;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public abstract class BasePresenter<V extends BaseView, Proxy_ extends Proxy<?>>
		extends Presenter<V, Proxy_>
		implements AsyncCallFailHandler {

	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();

	@Inject protected TopBarPresenter topBar;

	protected BasePresenter(final EventBus eventBus, final V view, final Proxy_ proxy) {
		super(eventBus, view, proxy);

	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addRegisteredHandler(new Type<AsyncCallFailHandler>(), this);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(TOP_BAR_SLOT, topBar);
	}

	// When a new version of the app is deployed, old async JS fragments will be lost but the app will still attempt to load them
	// This handler will automatically reload the page when an async JS fragment is not loaded properly
	@Override
	public void onAsyncCallFail(final AsyncCallFailEvent asyncCallFailEvent) {
		Window.Location.reload();
	}

}