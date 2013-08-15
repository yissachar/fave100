package com.fave100.client.gin;

import com.fave100.client.CurrentUser;
import com.fave100.client.RequestCache;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pagefragments.login.LoginWidgetView;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.popups.login.LoginPopupView;
import com.fave100.client.pagefragments.popups.register.RegisterPopupPresenter;
import com.fave100.client.pagefragments.popups.register.RegisterPopupView;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.fave100.client.pagefragments.register.RegisterWidgetView;
import com.fave100.client.pagefragments.topbar.TopBarPresenter;
import com.fave100.client.pagefragments.topbar.TopBarView;
import com.fave100.client.pages.home.HomePresenter;
import com.fave100.client.pages.home.HomeView;
import com.fave100.client.pages.login.LoginPresenter;
import com.fave100.client.pages.login.LoginView;
import com.fave100.client.pages.logout.LogoutPresenter;
import com.fave100.client.pages.logout.LogoutView;
import com.fave100.client.pages.passwordreset.PasswordResetPresenter;
import com.fave100.client.pages.passwordreset.PasswordResetView;
import com.fave100.client.pages.profile.ProfilePresenter;
import com.fave100.client.pages.profile.ProfileView;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.register.RegisterView;
import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.pages.song.SongView;
import com.fave100.client.pages.song.widgets.playlist.PlaylistPresenter;
import com.fave100.client.pages.song.widgets.playlist.PlaylistView;
import com.fave100.client.pages.song.widgets.youtube.YouTubePresenter;
import com.fave100.client.pages.song.widgets.youtube.YouTubeView;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.pages.users.UsersView;
import com.fave100.client.place.ClientPlaceManager;
import com.fave100.client.place.DefaultPlace;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.GaAccount;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsNavigationTracker;
import com.fave100.client.pages.users.widgets.autocomplete.SongAutocompletePresenter;
import com.fave100.client.pages.users.widgets.autocomplete.SongAutocompleteView;
import com.fave100.client.pages.users.widgets.favelist.FavelistPresenter;
import com.fave100.client.pages.users.widgets.favelist.FavelistView;
import com.fave100.client.pages.users.widgets.listmanager.ListManagerPresenter;
import com.fave100.client.pages.users.widgets.listmanager.ListManagerView;
import com.fave100.client.pages.users.widgets.usersfollowing.UsersFollowingPresenter;
import com.fave100.client.pages.users.widgets.usersfollowing.UsersFollowingView;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		// Google Analytics
		bindConstant().annotatedWith(GaAccount.class).to("UA-39911495-1");
		bind(GoogleAnalyticsNavigationTracker.class).asEagerSingleton();

		bind(RequestCache.class).asEagerSingleton();

		bind(CurrentUser.class).asEagerSingleton();

		bind(LoggedInGatekeeper.class).asEagerSingleton();

		bind(NotLoggedInGatekeeper.class).asEagerSingleton();

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.home);
		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.home);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.home);

		bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
				HomeView.class, HomePresenter.MyProxy.class);

		bindSingletonPresenterWidget(TopBarPresenter.class,
				TopBarPresenter.MyView.class, TopBarView.class);

		bindPresenter(RegisterPresenter.class, RegisterPresenter.MyView.class,
				RegisterView.class, RegisterPresenter.MyProxy.class);

		bindPresenter(UsersPresenter.class, UsersPresenter.MyView.class,
				UsersView.class, UsersPresenter.MyProxy.class);

		bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class,
				LoginView.class, LoginPresenter.MyProxy.class);

		bindPresenter(LogoutPresenter.class, LogoutPresenter.MyView.class,
				LogoutView.class, LogoutPresenter.MyProxy.class);

		bindPresenter(ProfilePresenter.class, ProfilePresenter.MyView.class,
				ProfileView.class, ProfilePresenter.MyProxy.class);

		bindPresenterWidget(LoginWidgetPresenter.class,
				LoginWidgetPresenter.MyView.class, LoginWidgetView.class);

		bindPresenter(SongPresenter.class, SongPresenter.MyView.class,
				SongView.class, SongPresenter.MyProxy.class);

		bindPresenter(PasswordResetPresenter.class,
				PasswordResetPresenter.MyView.class, PasswordResetView.class,
				PasswordResetPresenter.MyProxy.class);

		bindSingletonPresenterWidget(SongAutocompletePresenter.class,
				SongAutocompletePresenter.MyView.class,
				SongAutocompleteView.class);

		bindSingletonPresenterWidget(FavelistPresenter.class,
				FavelistPresenter.MyView.class, FavelistView.class);

		bindSingletonPresenterWidget(RegisterPopupPresenter.class, RegisterPopupPresenter.MyView.class, RegisterPopupView.class);

		bindSingletonPresenterWidget(LoginPopupPresenter.class, LoginPopupPresenter.MyView.class, LoginPopupView.class);

		bindSingletonPresenterWidget(RegisterWidgetPresenter.class, RegisterWidgetPresenter.MyView.class, RegisterWidgetView.class);

		bindSingletonPresenterWidget(PlaylistPresenter.class, PlaylistPresenter.MyView.class, PlaylistView.class);

		bindSingletonPresenterWidget(YouTubePresenter.class, YouTubePresenter.MyView.class, YouTubeView.class);

		bindPresenterWidget(UsersFollowingPresenter.class, UsersFollowingPresenter.MyView.class, UsersFollowingView.class);

		bindSingletonPresenterWidget(ListManagerPresenter.class, ListManagerPresenter.MyView.class, ListManagerView.class);
	}

	@Provides
	@Singleton
	public ApplicationRequestFactory createApplicationRequestFactory(
			final EventBus eventBus) {
		final ApplicationRequestFactory requestFactory = GWT
				.create(ApplicationRequestFactory.class);
		requestFactory.initialize(eventBus);
		return requestFactory;
	}
}
