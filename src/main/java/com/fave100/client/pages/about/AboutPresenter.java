package com.fave100.client.pages.about;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class AboutPresenter extends BasePresenter<AboutPresenter.MyView, AboutPresenter.MyProxy> {
	public interface MyView extends BaseView {
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
