package com.fave100.client.gin;

import com.fave100.client.CurrentUser;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.pagefragments.autocomplete.SongAutocompletePresenter;
import com.fave100.client.pagefragments.autocomplete.SongAutocompleteView;
import com.fave100.client.pagefragments.favelist.FavelistPresenter;
import com.fave100.client.pagefragments.favelist.FavelistView;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pagefragments.login.LoginWidgetView;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.popups.login.LoginPopupView;
import com.fave100.client.pagefragments.popups.register.RegisterPopupPresenter;
import com.fave100.client.pagefragments.popups.register.RegisterPopupView;
import com.fave100.client.pagefragments.topbar.TopBarPresenter;
import com.fave100.client.pagefragments.topbar.TopBarView;
import com.fave100.client.pages.explore.ExplorePresenter;
import com.fave100.client.pages.explore.ExploreView;
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
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.pages.users.UsersView;
import com.fave100.client.place.ClientPlaceManager;
import com.fave100.client.place.DefaultPlace;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.annotations.GaAccount;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsNavigationTracker;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.fave100.client.pagefragments.register.RegisterWidgetView;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		// Google Analytics
		bindConstant().annotatedWith(GaAccount.class).to("UA-39911495-1");
		bind(GoogleAnalyticsNavigationTracker.class).asEagerSingleton();

		bind(CurrentUser.class).asEagerSingleton();

		bind(LoggedInGatekeeper.class).asEagerSingleton();

		bind(NotLoggedInGatekeeper.class).asEagerSingleton();

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.home);

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

		bindPresenter(ExplorePresenter.class, ExplorePresenter.MyView.class, ExploreView.class, ExplorePresenter.MyProxy.class);
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
