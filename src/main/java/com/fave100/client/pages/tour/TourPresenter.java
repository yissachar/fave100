package com.fave100.client.pages.tour;

import com.fave100.client.pages.PagePresenter;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class TourPresenter extends PagePresenter<TourPresenter.MyView, TourPresenter.MyProxy> implements TourUiHandlers {

	public interface MyView extends View, HasUiHandlers<TourUiHandlers> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> SLOT_Tour = new Type<RevealContentHandler<?>>();

	@NameToken(NameTokens.tour)
	@ProxyCodeSplit
	public interface MyProxy extends ProxyPlace<TourPresenter> {
	}

	@Inject
	public TourPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
		super(eventBus, view, proxy);

		getView().setUiHandlers(this);
	}

}
