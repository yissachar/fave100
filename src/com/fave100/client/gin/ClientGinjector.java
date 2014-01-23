package com.fave100.client.gin;

import com.fave100.client.CurrentUser;
import com.fave100.client.RequestCache;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.pages.login.LoginPresenter;
import com.fave100.client.pages.passwordreset.PasswordResetPresenter;
import com.fave100.client.pages.profile.ProfilePresenter;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.song.SongPresenter;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

@GinModules({ClientModule.class})
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	CurrentUser getCurrentUser();

	RequestCache getRequestCache();

	LoggedInGatekeeper getLoggedInGatekeeper();

	NotLoggedInGatekeeper getNotLoggedInGatekeeper();

	AsyncProvider<RegisterPresenter> getRegisterPresenter();

	AsyncProvider<ListPresenter> getListPresenter();

	AsyncProvider<LoginPresenter> getLoginPresenter();

	AsyncProvider<ProfilePresenter> getProfilePresenter();

	AsyncProvider<SongPresenter> getSongPresenter();

	AsyncProvider<PasswordResetPresenter> getPasswordResetPresenter();
}
