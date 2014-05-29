package com.fave100.client.pages;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailHandler;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public abstract class PagePresenter<V extends View, Proxy_ extends Proxy<?>>
		extends Presenter<V, Proxy_>
		implements AsyncCallFailHandler {

	protected PagePresenter(final EventBus eventBus, final V view, final Proxy_ proxy) {
		super(eventBus, view, proxy);

	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addRegisteredHandler(new Type<AsyncCallFailHandler>(), this);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
	}

	// When a new version of the app is deployed, old async JS fragments will be lost but the app will still attempt to load them
	// This handler will automatically reload the page when an async JS fragment is not loaded properly
	@Override
	public void onAsyncCallFail(final AsyncCallFailEvent asyncCallFailEvent) {
		Window.Location.reload();
	}

}