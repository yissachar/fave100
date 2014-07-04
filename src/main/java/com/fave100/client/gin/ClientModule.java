package com.fave100.client.gin;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.RequestCache;
import com.fave100.client.StorageManager;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pagefragments.login.LoginWidgetView;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.popups.login.LoginPopupView;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.fave100.client.pagefragments.register.RegisterWidgetView;
import com.fave100.client.pagefragments.topbar.TopBarModule;
import com.fave100.client.pages.MainModule;
import com.fave100.client.pages.about.AboutModule;
import com.fave100.client.pages.listbrowser.ListBrowserModule;
import com.fave100.client.pages.lists.ListModule;
import com.fave100.client.pages.passwordreset.PasswordResetPresenter;
import com.fave100.client.pages.passwordreset.PasswordResetView;
import com.fave100.client.pages.profile.ProfilePresenter;
import com.fave100.client.pages.profile.ProfileView;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.register.RegisterView;
import com.fave100.client.pages.song.SongModule;
import com.fave100.client.pages.tour.TourModule;
import com.fave100.client.place.ClientPlaceManager;
import com.fave100.client.place.DefaultPlace;
import com.fave100.client.widgets.alert.AlertModule;
import com.fave100.client.widgets.autocomplete.AutocompleteModule;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.gwtplatform.dispatch.rest.client.RestApplicationPath;
import com.gwtplatform.dispatch.rest.client.gin.RestDispatchAsyncModule;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.GaAccount;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsNavigationTracker;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new MainModule());
		// Page and widget modules
		install(new SongModule());
		install(new TopBarModule());
		install(new ListModule());
		install(new TourModule());
		install(new AboutModule());
		install(new AlertModule());
		install(new ListBrowserModule());
		install(new AutocompleteModule());

		install(new DefaultModule(ClientPlaceManager.class));
		install(new RestDispatchAsyncModule.Builder().build());

		bindConstant().annotatedWith(RestApplicationPath.class).to(Constants.API_PATH);

		// Google Analytics
		bindConstant().annotatedWith(GaAccount.class).to("UA-39911495-1");
		bind(GoogleAnalyticsNavigationTracker.class).asEagerSingleton();

		bind(RequestCache.class).asEagerSingleton();
		bind(CurrentUser.class).asEagerSingleton();
		bind(LoggedInGatekeeper.class).asEagerSingleton();
		bind(NotLoggedInGatekeeper.class).asEagerSingleton();
		bind(StorageManager.class).asEagerSingleton();
		bind(FaveApi.class).asEagerSingleton();

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.lists);
		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.lists);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.lists);

		bindPresenter(RegisterPresenter.class, RegisterPresenter.MyView.class, RegisterView.class, RegisterPresenter.MyProxy.class);
		bindPresenter(ProfilePresenter.class, ProfilePresenter.MyView.class, ProfileView.class, ProfilePresenter.MyProxy.class);
		bindPresenterWidget(LoginWidgetPresenter.class, LoginWidgetPresenter.MyView.class, LoginWidgetView.class);
		bindPresenter(PasswordResetPresenter.class, PasswordResetPresenter.MyView.class, PasswordResetView.class, PasswordResetPresenter.MyProxy.class);
		bindSingletonPresenterWidget(LoginPopupPresenter.class, LoginPopupPresenter.MyView.class, LoginPopupView.class);
		bindSingletonPresenterWidget(RegisterWidgetPresenter.class, RegisterWidgetPresenter.MyView.class, RegisterWidgetView.class);
	}
}
