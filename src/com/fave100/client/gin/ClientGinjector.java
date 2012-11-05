package com.fave100.client.gin;

import com.fave100.client.pages.home.HomePresenter;
import com.fave100.client.pages.login.LoginPresenter;
import com.fave100.client.pages.logout.LogoutPresenter;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.search.SearchPresenter;
import com.fave100.client.pages.userlist.UserlistPresenter;
import com.fave100.client.pages.users.UsersPresenter;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.fave100.client.pages.profile.ProfilePresenter;
import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.pages.passwordreset.PasswordResetPresenter;

@GinModules({ DispatchAsyncModule.class, ClientModule.class })
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	AsyncProvider<HomePresenter> getHomePresenter();

	AsyncProvider<RegisterPresenter> getRegisterPresenter();

	AsyncProvider<UsersPresenter> getUsersPresenter();

	AsyncProvider<LoginPresenter> getLoginPresenter();

	AsyncProvider<LogoutPresenter> getLogoutPresenter();

	AsyncProvider<UserlistPresenter> getUserlistPresenter();

	AsyncProvider<SearchPresenter> getSearchPresenter();

	AsyncProvider<ProfilePresenter> getProfilePresenter();

	AsyncProvider<SongPresenter> getSongPresenter();

	AsyncProvider<PasswordResetPresenter> getPasswordResetPresenter();
}
