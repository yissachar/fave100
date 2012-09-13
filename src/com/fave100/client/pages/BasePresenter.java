package com.fave100.client.pages;

import com.fave100.client.pagefragments.topbar.TopBarPresenter;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public abstract class BasePresenter<V extends BaseView, Proxy_ extends Proxy<?>>
		extends Presenter<V, Proxy_> {
		
	@ContentSlot
	public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject protected TopBarPresenter topBar;
	
	protected BasePresenter(final EventBus eventBus, final V view, final Proxy_ proxy) {
		super(eventBus, view, proxy);
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

}