package com.fave100.client.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.fave100.client.pages.home.HomePresenter;
import com.fave100.client.pages.home.HomeView;
import com.fave100.client.place.ClientPlaceManager;
import com.fave100.client.place.DefaultPlace;
import com.fave100.client.place.NameTokens;
import com.fave100.client.pages.about.AboutPresenter;
import com.fave100.client.pages.about.AboutView;
import com.fave100.client.pages.myfave100.MyFave100Presenter;
import com.fave100.client.pages.myfave100.MyFave100View;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.home);
		
		bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
				HomeView.class, HomePresenter.MyProxy.class);

		bindPresenter(AboutPresenter.class, AboutPresenter.MyView.class,
				AboutView.class, AboutPresenter.MyProxy.class);

		bindPresenter(MyFave100Presenter.class,
				MyFave100Presenter.MyView.class, MyFave100View.class,
				MyFave100Presenter.MyProxy.class);
	}
}
