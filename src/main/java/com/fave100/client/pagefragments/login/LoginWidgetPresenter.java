package com.fave100.client.pagefragments.login;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.RequestCache;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.LoginCredentials;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

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

		void setShortNames(boolean yes);
	}

	private EventBus _eventBus;
	private PlaceManager _placeManager;
	private RequestCache _requestCache;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private String redirect;

	@Inject
	public LoginWidgetPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final RequestCache requestCache,
								final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_requestCache = requestCache;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
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

	public void setShortNames(boolean yes) {
		getView().setShortNames(yes);
	}

	// Native login
	@Override
	public void login() {
		if (getView().getUsername().isEmpty() || getView().getPassword().isEmpty()) {
			getView().setError("Fields must not be empty");
			return;
		}

		LoadingIndicator.show();

		LoginCredentials loginCredentials = new LoginCredentials();
		loginCredentials.setUsername(getView().getUsername().trim());
		loginCredentials.setPassword(getView().getPassword());

		_dispatcher.execute(_restServiceFactory.auth().login(loginCredentials), new AsyncCallback<AppUser>() {

			@Override
			public void onFailure(Throwable caught) {
				LoadingIndicator.hide();
				//				String errorMsg = "An error occurred";
				//				if (failure.getExceptionType().equals(
				//						IncorrectLoginException.class.getName())) {
				//					errorMsg = "Username or password incorrect";
				//				}
				getView().setError(caught.getMessage());
			}

			@Override
			public void onSuccess(AppUser appUser) {
				LoadingIndicator.hide();
				_eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
				Notification.show("Logged in successfully");
				_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.lists).with(ListPresenter.USER_PARAM, appUser.getUsername()).build());
			}
		});
	}

	// Twitter auth URL's expire relatively quickly, so instead of setting a link pointing to an auth URL we have to generate the auth URL whenever a
	// user clicks on the Twitter button.
	@Override
	public void goToTwitterAuth() {
		// Authenticate the user with Twitter
		_dispatcher.execute(_restServiceFactory.auth().getTwitterAuthUrl(redirect), new AsyncCallback<StringResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(StringResult url) {
				Window.open(url.getValue().replace("http", "https"), "", "");
			}
		});
	}
}

interface LoginUiHandlers extends UiHandlers {
	void login();

	void goToTwitterAuth();
}