package com.fave100.client.pages.register;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.FacebookIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.GoogleIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.TwitterIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Registration page
 * 
 * @author yissachar.radcliffe
 * 
 */
public class RegisterPresenter extends
		BasePresenter<RegisterPresenter.MyView, RegisterPresenter.MyProxy>
		implements RegisterUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<RegisterUiHandlers> {
		void clearFields();

		void showThirdPartyUsernamePrompt();

		void hideThirdPartyUsernamePrompt();

		void setThirdPartyUsernameError(String error);

		void clearThirdPartyErrors();
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> REGISTER_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private RegisterWidgetPresenter registerContainer;

	@ProxyCodeSplit
	@NameToken(NameTokens.register)
	@UseGatekeeper(NotLoggedInGatekeeper.class)
	public interface MyProxy extends ProxyPlace<RegisterPresenter> {
	}

	public static final String PROVIDER_GOOGLE = "google";
	public static final String PROVIDER_TWITTER = "twitter";
	public static final String PROVIDER_FACEBOOK = "facebook";

	private EventBus eventBus;
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	private String provider;
	private String facebookRedirect;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view,
								final MyProxy proxy,
								final ApplicationRequestFactory requestFactory,
								final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		provider = placeRequest.getParameter("provider", "");
		if (provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
			// The user is being redirected back to the register page after
			// signing in to their 3rd party account - prompt them for a
			// username and create their account

			// Make sure that the user is actually logged into Google
			final AppUserRequest appUserRequest = requestFactory
					.appUserRequest();
			final Request<Boolean> checkGoogleUserLoggedIn = appUserRequest
					.isGoogleUserLoggedIn();
			checkGoogleUserLoggedIn.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean loggedIn) {
					if (loggedIn) {
						// If user is logged in to Google, log them in to
						// Fave100
						final Request<AppUserProxy> loginWithGoogle = requestFactory
								.appUserRequest().loginWithGoogle();
						loginWithGoogle.fire(new Receiver<AppUserProxy>() {
							@Override
							public void onSuccess(final AppUserProxy user) {
								eventBus.fireEvent(new CurrentUserChangedEvent(
										user));
								if (user != null) {
									getProxy().manualRevealFailed();
									final PlaceRequest place = new PlaceRequest(NameTokens.users).with(UsersPresenter.USER_PARAM, user.getUsername());
									placeManager.revealPlace(place);
								}
								else {
									getProxy().manualReveal(RegisterPresenter.this);
								}
							}
						});
						getView().showThirdPartyUsernamePrompt();
					}
					else {
						getView().hideThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}
			});

		}
		else if (provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {
			// The user is being redirected back to the register page after
			// signing in to their 3rd party account - prompt them for a username and create their account

			LoadingIndicator.show();
			// First see if they are already logged in
			final String oauth_verifier = Window.Location
					.getParameter("oauth_verifier");
			final Request<AppUserProxy> loginWithTwitter = requestFactory
					.appUserRequest().loginWithTwitter(oauth_verifier);
			loginWithTwitter.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy user) {
					LoadingIndicator.hide();
					eventBus.fireEvent(new CurrentUserChangedEvent(user));
					if (user != null) {
						registerContainer.goToMyFave100();
					}
					else {
						// If they are not logged in, prompt for a username
						getView().showThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}

				@Override
				public void onFailure(final ServerFailure failure) {
					LoadingIndicator.hide();
					getView().showThirdPartyUsernamePrompt();
					getProxy().manualReveal(RegisterPresenter.this);
				}
			});

		}
		else if (Window.Location.getParameter("code") != null) {
			// FaceBook login

			// Check if user alaready logged in through Facebook
			LoadingIndicator.show();
			final Request<AppUserProxy> loginWithFacebook = requestFactory
					.appUserRequest().loginWithFacebook(Window.Location.getParameter("code"));
			loginWithFacebook.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy user) {
					LoadingIndicator.hide();
					eventBus.fireEvent(new CurrentUserChangedEvent(user));
					if (user != null) {
						registerContainer.goToMyFave100();
					}
					else {
						// If they are not logged in, prompt for a username
						getView().showThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}

				@Override
				public void onFailure(final ServerFailure failure) {
					LoadingIndicator.hide();
					getView().showThirdPartyUsernamePrompt();
					getProxy().manualReveal(RegisterPresenter.this);
				}
			});
		}
		else {
			getView().hideThirdPartyUsernamePrompt();
			getProxy().manualReveal(RegisterPresenter.this);
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void onReveal() {
		super.onReveal();
		getView().clearFields();
		setInSlot(REGISTER_SLOT, registerContainer);
	}

	@Override
	public void registerThirdParty(final String username) {
		if (validateThirdPartyFields(username)) {
			final AppUserRequest appUserRequest = requestFactory
					.appUserRequest();
			if (provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
				// Create Google-linked account
				final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromGoogleAccount(username);
				createAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy createdUser) {
						eventBus.fireEvent(new CurrentUserChangedEvent(
								createdUser));
						registerContainer.appUserCreated();
					}

					@Override
					public void onFailure(final ServerFailure failure) {
						String errorMsg = "An error occurred";
						if (failure.getExceptionType().equals(
								UsernameAlreadyExistsException.class.getName())) {
							errorMsg = "A user with that name already exists";
						}
						else if (failure.getExceptionType().equals(
								GoogleIdAlreadyExistsException.class.getName())) {
							errorMsg = "A Fave100 account is already associated with that Google account";
						}
						getView().setThirdPartyUsernameError(errorMsg);
					}
				});
			}
			else if (provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {
				// Create Twitter-linked account
				final String oauth_verifier = Window.Location
						.getParameter("oauth_verifier");
				final Request<AppUserProxy> createAppUserReq = appUserRequest
						.createAppUserFromTwitterAccount(username,
								oauth_verifier);
				createAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy createdUser) {
						eventBus.fireEvent(new CurrentUserChangedEvent(
								createdUser));
						registerContainer.appUserCreated();
						registerContainer.goToMyFave100();
					}

					@Override
					public void onFailure(final ServerFailure failure) {
						String errorMsg = "An error occurred";
						if (failure.getExceptionType().equals(
								UsernameAlreadyExistsException.class.getName())) {
							errorMsg = "A user with that name already exists";
						}
						else if (failure.getExceptionType()
								.equals(TwitterIdAlreadyExistsException.class
										.getName())) {
							errorMsg = "A Fave100 account is already associated with that Twitter account";
						}
						getView().setThirdPartyUsernameError(errorMsg);
					}
				});
				// } else if
				// (provider.equals(RegisterPresenter.PROVIDER_FACEBOOK)) {
			}
			else if (Window.Location.getParameter("code") != null) {
				// Create Facebook linked account
				final String state = Window.Location.getParameter("state");
				final String code = Window.Location.getParameter("code");
				final Request<AppUserProxy> createAppUserReq = appUserRequest
						.createAppUserFromFacebookAccount(username, state,
								code, facebookRedirect);
				createAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy createdUser) {
						eventBus.fireEvent(new CurrentUserChangedEvent(
								createdUser));
						if (createdUser != null) {
							registerContainer.appUserCreated();
							registerContainer.goToMyFave100();
						}

					}

					@Override
					public void onFailure(final ServerFailure failure) {
						String errorMsg = "An error occurred";
						if (failure.getExceptionType().equals(
								UsernameAlreadyExistsException.class.getName())) {
							errorMsg = "A user with that name already exists";
						}
						else if (failure.getExceptionType().equals(
								FacebookIdAlreadyExistsException.class
										.getName())) {
							errorMsg = "A Fave100 account is already associated with that Facebook account";
						}
						getView().setThirdPartyUsernameError(errorMsg);
					}
				});
			}
		}
	}

	private boolean validateThirdPartyFields(final String username) {
		getView().clearThirdPartyErrors();
		final String usernameError = Validator.validateUsername(username);
		if (usernameError != null) {
			getView().setThirdPartyUsernameError(usernameError);
			return false;
		}
		return true;
	}
}

interface RegisterUiHandlers extends UiHandlers {
	void registerThirdParty(String username);
}
