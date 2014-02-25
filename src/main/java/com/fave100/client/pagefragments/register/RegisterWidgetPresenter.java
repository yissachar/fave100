package com.fave100.client.pagefragments.register;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.RequestCache;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.LoginResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.UserRegistration;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.place.NameTokens;
import com.fave100.client.rest.RestSessionDispatch;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
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

public class RegisterWidgetPresenter extends PresenterWidget<RegisterWidgetPresenter.MyView>
		implements RegisterWidgetUiHandlers {

	public interface MyView extends View, HasUiHandlers<RegisterWidgetUiHandlers> {
		void setGoogleUrl(String url);

		void setFacebookUrl(String url);

		void clearFields();

		void setNativeUsernameError(String error);

		void setEmailError(String error);

		void setPasswordError(String error);

		void setPasswordRepeatError(String error);

		void clearNativeErrors();

		void setUsernameFocus();
	}

	private EventBus _eventBus;
	private PlaceManager _placeManager;
	private RequestCache _requestCache;
	private RestSessionDispatch _dispatcher;
	private RestServiceFactory _serviceFactory;
	private String redirect;

	@Inject
	public RegisterWidgetPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final RequestCache requestCache,
									final RestSessionDispatch dispatcher, final RestServiceFactory serviceFactory) {
		super(eventBus, view);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_requestCache = requestCache;
		_dispatcher = dispatcher;
		_serviceFactory = serviceFactory;
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
				getView().setGoogleUrl(url);
			}

			@Override
			public void onFailure(final Throwable caught) {

			}
		});

		// And for facebook
		_requestCache.getFacebookUrl(redirect, new AsyncCallback<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setFacebookUrl(url);
			}

			@Override
			public void onFailure(final Throwable caught) {

			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		getView().setUsernameFocus();
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().clearFields();
		getView().clearNativeErrors();
	}

	@Override
	public void register(String username, String email, final String password, final String passwordRepeat) {

		username = username.trim();
		email = email.trim();

		if (validateFields(username, email, password, passwordRepeat)) {

			// Create a new user with the username and password entered
			LoadingIndicator.show();
			UserRegistration registration = new UserRegistration();
			registration.setUsername(username);
			registration.setPassword(password);
			registration.setEmail(email);

			_dispatcher.execute(_serviceFactory.appuser().createAppUser(registration), new AsyncCallback<LoginResult>() {

				@Override
				public void onFailure(Throwable caught) {
					LoadingIndicator.hide();
					getView().setNativeUsernameError(caught.getMessage());
				}

				@Override
				public void onSuccess(LoginResult loginResult) {
					AppUser createdUser = loginResult.getAppUser();
					Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
					LoadingIndicator.hide();
					_eventBus.fireEvent(new CurrentUserChangedEvent(createdUser));
					if (createdUser != null) {
						appUserCreated();
					}
					else {
						getView().setPasswordError("An error occurred");
					}
				}
			});
		}
	}

	public void appUserCreated() {
		_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.lists).build());
		Notification.show("Thanks for registering!");
	}

	@Override
	public void goToTwitterAuth() {
		_dispatcher.execute(_serviceFactory.appuser().getTwitterAuthUrl(redirect), new AsyncCallback<StringResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(StringResult url) {
				Window.open(url.getValue(), "", "");
			}
		});
	}

	private boolean validateFields(final String username, final String email,
			final String password, final String passwordRepeat) {
		// Assume all valid
		getView().clearNativeErrors();
		boolean valid = true;

		// Check for validity

		final String usernameError = Validator.validateUsername(username);
		if (usernameError != null) {
			getView().setNativeUsernameError(usernameError);
			valid = false;
		}

		final String emailError = Validator.validateEmail(email);
		if (emailError != null) {
			getView().setEmailError(emailError);
			valid = false;
		}

		final String passwordError = Validator.validatePassword(password);
		if (passwordError != null) {
			getView().setPasswordError(passwordError);
			valid = false;
		}
		else if (!password.equals(passwordRepeat)) {
			getView().setPasswordRepeatError("Passwords must match");
			valid = false;
		}
		return valid;
	}
}

interface RegisterWidgetUiHandlers extends UiHandlers {
	void register(String username, String email, String password,
			String passwordRepeat);

	void goToTwitterAuth();

}