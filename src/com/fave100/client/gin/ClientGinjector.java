package com.fave100.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.fave100.client.gin.ClientModule;
import com.fave100.client.pages.home.HomePresenter;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.google.gwt.inject.client.AsyncProvider;
import com.fave100.client.pages.myfave100.MyFave100Presenter;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.pages.login.LoginPresenter;
import com.fave100.client.pages.logout.LogoutPresenter;
import com.fave100.client.pages.userlist.UserlistPresenter;

@GinModules({ DispatchAsyncModule.class, ClientModule.class })
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	AsyncProvider<HomePresenter> getHomePresenter();

	AsyncProvider<MyFave100Presenter> getMyFave100Presenter();

	AsyncProvider<RegisterPresenter> getRegisterPresenter();

	AsyncProvider<UsersPresenter> getUsersPresenter();

	AsyncProvider<LoginPresenter> getLoginPresenter();

	AsyncProvider<LogoutPresenter> getLogoutPresenter();

	AsyncProvider<UserlistPresenter> getUserlistPresenter();
}
