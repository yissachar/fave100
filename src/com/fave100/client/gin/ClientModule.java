package com.fave100.client.gin;

import com.google.gwt.core.client.GWT;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.fave100.client.pages.home.HomePresenter;
import com.fave100.client.pages.home.HomeView;
import com.fave100.client.place.ClientPlaceManager;
import com.fave100.client.place.DefaultPlace;
import com.fave100.client.place.NameTokens;
import com.fave100.client.pages.myfave100.MyFave100Presenter;
import com.fave100.client.pages.myfave100.MyFave100View;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.pagefragments.TopBarView;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.register.RegisterView;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.pages.users.UsersView;
import com.fave100.client.pages.login.LoginPresenter;
import com.fave100.client.pages.login.LoginView;
import com.fave100.client.pages.logout.LogoutPresenter;
import com.fave100.client.pages.logout.LogoutView;
import com.fave100.client.pages.userlist.UserlistPresenter;
import com.fave100.client.pages.userlist.UserlistView;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.home);
		
		bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
				HomeView.class, HomePresenter.MyProxy.class);

		bindPresenter(MyFave100Presenter.class,
				MyFave100Presenter.MyView.class, MyFave100View.class,
				MyFave100Presenter.MyProxy.class);

		bindPresenterWidget(TopBarPresenter.class,
				TopBarPresenter.MyView.class, TopBarView.class);	

		bindPresenter(RegisterPresenter.class, RegisterPresenter.MyView.class,
				RegisterView.class, RegisterPresenter.MyProxy.class);

		bindPresenter(UsersPresenter.class, UsersPresenter.MyView.class,
				UsersView.class, UsersPresenter.MyProxy.class);

		bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class,
				LoginView.class, LoginPresenter.MyProxy.class);

		bindPresenter(LogoutPresenter.class, LogoutPresenter.MyView.class,
				LogoutView.class, LogoutPresenter.MyProxy.class);

		bindPresenter(UserlistPresenter.class, UserlistPresenter.MyView.class,
				UserlistView.class, UserlistPresenter.MyProxy.class);
	}
	
	@Provides
	@Singleton
	public ApplicationRequestFactory createApplicationRequestFactory(EventBus eventBus) {
		ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(eventBus);
		return requestFactory;
	}
}
