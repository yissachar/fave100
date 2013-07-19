package com.fave100.client.pagefragments.login;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.RequestCache;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.exceptions.user.IncorrectLoginException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * A {@link PresenterWidget} that displays native and third party login options.
 * The majority of the styling is left to the parent class.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class LoginWidgetPresenter extends
		PresenterWidget<LoginWidgetPresenter.MyView> implements LoginUiHandlers {

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {
		String getUsername();

		String getPassword();

		void clearUsername();

		void clearPassword();

		void setError(String error);

		void setGoogleLoginUrl(String url);

		void setFacebookLoginUrl(String url);

		void setUsernameFocus();
	}

	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private PlaceManager _placeManager;
	private RequestCache _requestCache;
	private String redirect;

	@Inject
	public LoginWidgetPresenter(final EventBus eventBus, final MyView view,
								final ApplicationRequestFactory requestFactory,
								final PlaceManager placeManager, final RequestCache requestCache) {
		super(eventBus, view);
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_placeManager = placeManager;
		_requestCache = requestCache;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		redirect = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/oauthcallback.html";

		// Get the login url for Google
		_requestCache.getGoogleUrl(redirect, new AsyncCallback<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setGoogleLoginUrl(url);
			}

			@Override
			public void onFailure(final Throwable caught) {

			}
		});

		// Get the login url for Facebook
		_requestCache.getFacebookUrl(redirect, new AsyncCallback<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setFacebookLoginUrl(url);
			}

			@Override
			public void onFailure(final Throwable caught) {

			}
		});
		/*final Request<String> facebookLoginUrlReq = requestFactory
				.appUserRequest().getFacebookAuthUrl(redirect);
		facebookLoginUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setFacebookLoginUrl(url);
			}
		});*/

	}

	@Override
	public void onReveal() {
		super.onReveal();
		getView().setUsernameFocus();
	}

	@Override
	protected void onHide() {
		super.onHide();
		clearLoginDetails();
	}

	public void clearLoginDetails() {
		getView().clearUsername();
		getView().clearPassword();
	}

	// Native login
	@Override
	public void login() {
		LoadingIndicator.show();
		final AppUserRequest appUserRequest = _requestFactory.appUserRequest();
		final Request<AppUserProxy> loginReq = appUserRequest.login(getView()
				.getUsername().trim(), getView().getPassword());

		// Attempt to log the user in
		loginReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				LoadingIndicator.hide();
				_eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
				Notification.show("Logged in successfully");
				_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.users).with(UsersPresenter.USER_PARAM, appUser.getUsername()).build());
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				LoadingIndicator.hide();
				String errorMsg = "An error occurred";
				if (failure.getExceptionType().equals(
						IncorrectLoginException.class.getName())) {
					errorMsg = "Username or password incorrect";
				}
				getView().setError(errorMsg);
			}
		});
	}

	// Twitter auth URL's expire relatively quickly, so instead of setting a
	// link pointing to an auth URL we have to generate the auth URL whenever a
	// user clicks on the Twitter button.
	@Override
	public void goToTwitterAuth() {
		// Authenticate the user with Twitter
		final Request<String> authUrlReq = _requestFactory.appUserRequest()
				.getTwitterAuthUrl(redirect);
		authUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				Window.open(url, "", "");
			}
		});
	}
}

interface LoginUiHandlers extends UiHandlers {
	void login();

	void goToTwitterAuth();
}