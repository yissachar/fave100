package com.fave100.client.pagefragments.register;

import com.fave100.client.CurrentUser;
import com.fave100.client.LoadingIndicator;
import com.fave100.client.Notification;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
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

	private EventBus eventBus;
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	private CurrentUser currentUser;
	private String facebookRedirect;

	@Inject
	public RegisterWidgetPresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory, final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		// Get the login url for Google
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<String> loginUrlReq = appUserRequest
				.getGoogleLoginURL(Window.Location.getProtocol() + "//" + Window.Location.getHost() + Window.Location.getPath() + Window.Location.getQueryString()
						+ "#" + NameTokens.register + ";provider=" + RegisterPresenter.PROVIDER_GOOGLE);
		loginUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setGoogleUrl(url);
			}
		});

		facebookRedirect = Window.Location.getProtocol() + "//" + Window.Location.getHost() + Window.Location.getPath() + Window.Location.getQueryString()
				+ "#" + NameTokens.register + ";provider=" + RegisterPresenter.PROVIDER_FACEBOOK;
		final Request<String> fbAuthUrlReq = requestFactory.appUserRequest()
				.getFacebookAuthUrl(facebookRedirect);
		fbAuthUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setFacebookUrl(url);
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
	}

	@Override
	public void register(final String username, final String email,
			final String password, final String passwordRepeat) {

		if (validateFields(username, email, password, passwordRepeat)) {
			final AppUserRequest appUserRequest = requestFactory.appUserRequest();
			// Create a new user with the username and password entered
			final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUser(username, password, email);

			LoadingIndicator.show();
			createAppUserReq.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy createdUser) {
					LoadingIndicator.hide();
					eventBus.fireEvent(new CurrentUserChangedEvent(createdUser));
					if (createdUser != null) {
						appUserCreated();
					}
					else {
						getView().setPasswordError("An error occurred");
					}
				}

				@Override
				public void onFailure(final ServerFailure failure) {
					LoadingIndicator.hide();
					String errorMsg = "An error occurred";
					if (failure.getExceptionType().equals(
							UsernameAlreadyExistsException.class.getName())) {
						errorMsg = "A user with that name already exists";
						getView().setNativeUsernameError(errorMsg);
					}
					else if (failure.getExceptionType().equals(
							EmailIDAlreadyExistsException.class.getName())) {
						errorMsg = "A user with that email address is already registered";
						getView().setEmailError(errorMsg);
					}
					else {
						getView().setNativeUsernameError(errorMsg);
					}
				}
			});
		}
	}

	public void appUserCreated() {
		placeManager.revealPlace(new PlaceRequest(NameTokens.home));
		Notification.show("Thanks for registering!");
	}

	public void goToMyFave100() {
		// Need to strip out query params or they will stick around in URL
		// forever
		String url = Window.Location.getHref();
		if (Window.Location.getParameter("oauth_token") != null) {
			url = url.replace(Window.Location.getParameter("oauth_token"), "");
			url = url.replace("&oauth_token=", "");
		}
		if (Window.Location.getParameter("oauth_verifier") != null) {
			url = url.replace(Window.Location.getParameter("oauth_verifier"),
					"");
			url = url.replace("&oauth_verifier=", "");
		}
		if (Window.Location.getParameter("code") != null) {
			url = url.replace(Window.Location.getParameter("code"), "");
			url = url.replace("&code=", "");
		}
		final String currentUserPlace = new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, currentUser.getUsername()).getPlaceToken();
		url = url.replace(Window.Location.getHash(), currentUserPlace);
		Window.Location.assign(url);
	}

	@Override
	public void goToTwitterAuth() {
		final Request<String> authUrlReq = requestFactory.appUserRequest()
				.getTwitterAuthUrl(
						Window.Location.getProtocol() + "//" + Window.Location.getHost() + Window.Location.getPath() + Window.Location.getQueryString()
								+ "#" + NameTokens.register + ";provider=" + RegisterPresenter.PROVIDER_TWITTER);
		authUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				Window.Location.assign(url);
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
