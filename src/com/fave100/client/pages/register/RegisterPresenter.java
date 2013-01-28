package com.fave100.client.pages.register;

import com.fave100.client.CurrentUser;
import com.fave100.client.Notification;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.FacebookIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.GoogleIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.TwitterIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class RegisterPresenter extends
		BasePresenter<RegisterPresenter.MyView, RegisterPresenter.MyProxy>
		implements RegisterUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<RegisterUiHandlers> {
		void setGoogleUrl(String url);
		void setFacebookUrl(String url);
		void clearFields();
		void showThirdPartyUsernamePrompt();
		void hideThirdPartyUsernamePrompt();
		void setNativeUsernameError(String error);
		void setThirdPartyUsernameError(String error);
		void setEmailError(String error);
		void setPasswordError(String error);
		void setPasswordRepeatError(String error);
		void clearNativeErrors();
		void clearThirdPartyErrors();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.register)
	public interface MyProxy extends ProxyPlace<RegisterPresenter> {
	}

	public static final String PROVIDER_GOOGLE = "google";
	public static final String PROVIDER_TWITTER = "twitter";
	public static final String PROVIDER_FACEBOOK = "facebook";

	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	private CurrentUser currentUser;
	private String provider;
	private String facebookRedirect;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		final Request<AppUserProxy> getLoggedInUserReq =  requestFactory.appUserRequest().getLoggedInAppUser();
		getLoggedInUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy user) {
				if(user != null) {
					// TODO: Gatekeeper instead? (need CurrentUser class in order for that to work,
					// instead of RF every request, uses events)

					// Logged in user trying to register: redirect them to home
					placeManager.revealDefaultPlace();
				}
			}
		});
		// TODO: Captcha/recaptcha? Other spam filtering to prevent registration?

		provider = placeRequest.getParameter("provider", "");
		if(provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
			// The user is being redirected back to the register page after signing in to
			// their 3rd party account - prompt them for a username and create their account

			// Make sure that the user is actually logged into Google
			final AppUserRequest appUserRequest = requestFactory.appUserRequest();
			final Request<Boolean> checkGoogleUserLoggedIn = appUserRequest.isGoogleUserLoggedIn();
			checkGoogleUserLoggedIn.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean loggedIn) {
					if(loggedIn) {
						// If user is logged in to Google, log them in to Fave100
						final Request<AppUserProxy> loginWithGoogle = requestFactory.appUserRequest().loginWithGoogle();
						loginWithGoogle.fire(new Receiver<AppUserProxy>() {
							@Override
							public void onSuccess(final AppUserProxy user) {
								currentUser.setAppUser(user);
								if(user != null) {
									placeManager.revealDefaultPlace();
								}
								getProxy().manualReveal(RegisterPresenter.this);
							}
						});
						getView().showThirdPartyUsernamePrompt();
					} else {
						getView().hideThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}
			});

		} else if(provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {
			// The user is being redirected back to the register page after signing in to
			// their 3rd party account - prompt them for a username and create their account

			//TODO: Need to show some loading icon or something while waiting for the RF req
			getView().showThirdPartyUsernamePrompt();
			// Try to log the user in with Twitter
			final String oauth_verifier = Window.Location.getParameter("oauth_verifier");
			final Request<AppUserProxy> loginWithTwitter = requestFactory.appUserRequest().loginWithTwitter(oauth_verifier);
			loginWithTwitter.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy user) {
					currentUser.setAppUser(user);
					if(user != null) {
						goToMyFave100();
					}
					getProxy().manualReveal(RegisterPresenter.this);
				}
			});

		} else if(Window.Location.getParameter("code") != null){

			getView().showThirdPartyUsernamePrompt();
			getProxy().manualReveal(RegisterPresenter.this);

		} else {
			getView().hideThirdPartyUsernamePrompt();
			getProxy().manualReveal(RegisterPresenter.this);
		}
	}

	@Override
	protected void onBind() {
		super.onBind();

		// TODO: We should have 3rd party logins open in new window so as not to clutter up our URL

		// Get the login url for Google
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<String> loginUrlReq = appUserRequest.getGoogleLoginURL(Window.Location.getHref()+";provider="+RegisterPresenter.PROVIDER_GOOGLE);
		loginUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setGoogleUrl(url);
			}
		});

		facebookRedirect = Window.Location.getHref()+";provider="+RegisterPresenter.PROVIDER_FACEBOOK;
		final Request<String> fbAuthUrlReq = requestFactory.appUserRequest().getFacebookAuthUrl(facebookRedirect);
		fbAuthUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().setFacebookUrl(url);
			}
		});
	}

	@Override
	public void register(final String username, final String email, final String password,
			final String passwordRepeat) {

		if(validateFields(username, email, password, passwordRepeat)) {
			final AppUserRequest appUserRequest = requestFactory.appUserRequest();
			// Create a new user with the username and password entered
			final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUser(username,
					password, email);
			getView().clearFields();
			createAppUserReq.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy createdUser) {
					currentUser.setAppUser(createdUser);
					if(createdUser != null) {
						appUserCreated();
					} else {
						getView().setPasswordError("An error occurred");
					}
				}
				@Override
				public void onFailure(final ServerFailure failure) {
					String errorMsg = "An error occurred";
					if(failure.getExceptionType().equals(UsernameAlreadyExistsException.class.getName())) {
						errorMsg = "A user with that name already exists";
					}
					getView().setNativeUsernameError(errorMsg);
				}
			});
		}
	}

	@Override
	public void registerThirdParty(final String username) {
		if(validateThirdPartyFields(username)) {
			final AppUserRequest appUserRequest = requestFactory.appUserRequest();
			if(provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
				// Create Google-linked account
				final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromGoogleAccount(username);
				createAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy createdUser) {
						currentUser.setAppUser(createdUser);
						appUserCreated();
					}
					@Override
					public void onFailure(final ServerFailure failure) {
						String errorMsg = "An error occurred";
						if(failure.getExceptionType().equals(UsernameAlreadyExistsException.class.getName())) {
							errorMsg = "A user with that name already exists";
						} else if (failure.getExceptionType().equals(GoogleIdAlreadyExistsException.class.getName())) {
							errorMsg = "A Fave100 account is already associated with that Google account";
						}
						getView().setThirdPartyUsernameError(errorMsg);
					}
				});
			} else if(provider.equals(RegisterPresenter.PROVIDER_TWITTER)){
				// Create Twitter-linked account
				final String oauth_verifier = Window.Location.getParameter("oauth_verifier");
				final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromTwitterAccount(username, oauth_verifier);
				createAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy createdUser) {
						currentUser.setAppUser(createdUser);
						appUserCreated();
						goToMyFave100();
					}
					@Override
					public void onFailure(final ServerFailure failure) {
						String errorMsg = "An error occurred";
						if(failure.getExceptionType().equals(UsernameAlreadyExistsException.class.getName())) {
							errorMsg = "A user with that name already exists";
						} else if (failure.getExceptionType().equals(TwitterIdAlreadyExistsException.class.getName())) {
							errorMsg = "A Fave100 account is already associated with that Twitter account";
						}
						getView().setThirdPartyUsernameError(errorMsg);
					}
				});
			//} else if (provider.equals(RegisterPresenter.PROVIDER_FACEBOOK)) {
			} else if(Window.Location.getParameter("code") != null) {
				// Create Facebook linked account
				final String state = Window.Location.getParameter("state");
				final String code = Window.Location.getParameter("code");
				final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromFacebookAccount(username, state, code, facebookRedirect);
				createAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy createdUser) {
						currentUser.setAppUser(createdUser);
						if(createdUser != null) {
							appUserCreated();
							goToMyFave100();
						}

					}
					@Override
					public void onFailure(final ServerFailure failure) {
						String errorMsg = "An error occurred";
						if(failure.getExceptionType().equals(UsernameAlreadyExistsException.class.getName())) {
							errorMsg = "A user with that name already exists";
						} else if (failure.getExceptionType().equals(FacebookIdAlreadyExistsException.class.getName())) {
							errorMsg = "A Fave100 account is already associated with that Facebook account";
						}
						getView().setThirdPartyUsernameError(errorMsg);
					}
				});
			}
		}
	}

	private boolean validateFields(final String username, final String email,
			final String password, final String passwordRepeat) {
		// Assume all valid
		getView().clearNativeErrors();
		boolean valid = true;

		// Check for validity

		final String usernameError = Validator.validateUsername(username);
		if(usernameError != null) {
			getView().setNativeUsernameError(usernameError);
			valid = false;
		}

		final String emailError = Validator.validateEmail(email);
		if(emailError != null) {
			getView().setEmailError(emailError);
			valid = false;
		}

		final String passwordError = Validator.validatePassword(password);
		if(passwordError != null) {
			getView().setPasswordError(passwordError);
			valid = false;
		} else if(!password.equals(passwordRepeat)) {
			getView().setPasswordRepeatError("Passwords must match");
			valid = false;
		}
		return valid;
	}

	private boolean validateThirdPartyFields(final String username) {
		getView().clearThirdPartyErrors();
		final String usernameError = Validator.validateUsername(username);
		if(usernameError != null) {
			getView().setThirdPartyUsernameError(usernameError);
			return false;
		}
		return true;
	}

	private void appUserCreated() {
		placeManager.revealPlace(new PlaceRequest(NameTokens.home));
		Notification.show("Thanks for registering!");
	}

	private void goToMyFave100() {
		// Need to strip out query params or they will stick around in URL forever
		String url = Window.Location.getHref();
		if(Window.Location.getParameter("oauth_token") != null) {
			url = url.replace(Window.Location.getParameter("oauth_token"), "");
			url = url.replace("&oauth_token=", "");
		}
		if(Window.Location.getParameter("oauth_verifier") != null) {
			url = url.replace(Window.Location.getParameter("oauth_verifier"), "");
			url = url.replace("&oauth_verifier=", "");
		}
		if(Window.Location.getParameter("code") != null) {
			url = url.replace(Window.Location.getParameter("code"), "");
			url = url.replace("&code=", "");
		}
		url = url.replace(Window.Location.getHash(), "#myfave100");
		Window.Location.assign(url);
	}

	@Override
	public void goToTwitterAuth() {
		final Request<String> authUrlReq = requestFactory.appUserRequest().getTwitterAuthUrl(Window.Location.getHref()+";provider="+RegisterPresenter.PROVIDER_TWITTER);
		authUrlReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				Window.Location.assign(url);
			}
		});
	}

}

interface RegisterUiHandlers extends UiHandlers {
	void register(String username, String email, String password, String passwordRepeat);
	void registerThirdParty(String username);
	void goToTwitterAuth();

}
