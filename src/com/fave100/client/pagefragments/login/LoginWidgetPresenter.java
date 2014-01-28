package com.fave100.client.pagefragments.login;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.RequestCache;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUserDto;
import com.fave100.client.generated.entities.LoginResultDto;
import com.fave100.client.generated.entities.StringResultDto;
import com.fave100.client.generated.services.AppUserService;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.rest.RestSessionDispatch;
import com.fave100.shared.Constants;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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

		void setShortNames(boolean yes);
	}

	private EventBus _eventBus;
	private PlaceManager _placeManager;
	private RequestCache _requestCache;
	private RestSessionDispatch _dispatcher;
	private AppUserService _appUserService;
	private String redirect;

	@Inject
	public LoginWidgetPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final RequestCache requestCache,
								final RestSessionDispatch dispatcher, final AppUserService appUserService) {
		super(eventBus, view);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_requestCache = requestCache;
		_dispatcher = dispatcher;
		_appUserService = appUserService;
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
		_dispatcher.execute(_appUserService.login(getView().getUsername().trim(), getView().getPassword()), new AsyncCallback<LoginResultDto>() {

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
			public void onSuccess(LoginResultDto loginResult) {
				AppUserDto appUser = loginResult.getAppUser();
				Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
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
		_dispatcher.execute(_appUserService.getTwitterAuthUrl(redirect), new AsyncCallback<StringResultDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(StringResultDto url) {
				Window.open(url.getValue(), "", "");
			}
		});
	}
}

interface LoginUiHandlers extends UiHandlers {
	void login();

	void goToTwitterAuth();
}