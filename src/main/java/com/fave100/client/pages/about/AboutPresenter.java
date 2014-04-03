package com.fave100.client.pages.about;

import com.fave100.client.pages.PagePresenter;
import com.fave100.shared.place.NameTokens;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class AboutPresenter extends PagePresenter<AboutPresenter.MyView, AboutPresenter.MyProxy> {

	public interface MyView extends View, HasUiHandlers<AboutUiHandlers> {
	}

	@NameToken(NameTokens.about)
	@ProxyCodeSplit
	public interface MyProxy extends ProxyPlace<AboutPresenter> {
	}

	@Inject
	public AboutPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
		super(eventBus, view, proxy);
	}

}
