package com.fave100.client.pages;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public abstract class PagePresenter<V extends View, Proxy_ extends Proxy<?>> extends Presenter<V, Proxy_> {

	protected PagePresenter(final EventBus eventBus, final V view, final Proxy_ proxy) {
		super(eventBus, view, proxy);

	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
	}

}