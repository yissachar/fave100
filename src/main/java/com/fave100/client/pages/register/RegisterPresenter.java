package com.fave100.client.pages.register;

import com.fave100.client.LoadingIndicator;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.generated.entities.LoginResult;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.rest.RestSessionDispatch;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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
	private PlaceManager placeManager;
	private RestSessionDispatch _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private String provider;
	private String facebookRedirect;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final PlaceManager placeManager,
								final RestSessionDispatch dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view, proxy);
		this.eventBus = eventBus;
		this.placeManager = placeManager;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
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
			_dispatcher.execute(_restServiceFactory.user().isGoogleUserLoggedIn(), new AsyncCallback<BooleanResult>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSuccess(BooleanResult loggedIn) {
					if (loggedIn.getValue()) {
						// If user is logged in to Google, log them in to Fave100
						_dispatcher.execute(_restServiceFactory.user().loginWithGoogle(), new AsyncCallback<LoginResult>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(LoginResult loginResult) {
								AppUser user = loginResult.getAppUser();
								Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
								eventBus.fireEvent(new CurrentUserChangedEvent(user));
								if (user != null) {
									goToUserPage(user.getUsername());
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
			final String oauth_verifier = placeRequest.getParameter("oauth_verifier", "");//Window.Location
			//					.getParameter("oauth_verifier");

			_dispatcher.execute(_restServiceFactory.user().loginWithTwitter(oauth_verifier), new AsyncCallback<LoginResult>() {

				@Override
				public void onFailure(Throwable caught) {
					LoadingIndicator.hide();
					getView().showThirdPartyUsernamePrompt();
					getProxy().manualReveal(RegisterPresenter.this);
				}

				@Override
				public void onSuccess(LoginResult loginResult) {
					AppUser user = loginResult.getAppUser();
					Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
					LoadingIndicator.hide();
					eventBus.fireEvent(new CurrentUserChangedEvent(user));
					if (user != null) {
						goToUserPage(user.getUsername());
					}
					else {
						// If they are not logged in, prompt for a username
						getView().showThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}
			});

		}
		else if (provider.equals(RegisterPresenter.PROVIDER_FACEBOOK)) {
			// Check if user alaready logged in through Facebook
			LoadingIndicator.show();
			_dispatcher.execute(_restServiceFactory.user().loginWithFacebook(placeRequest.getParameter("code", "")), new AsyncCallback<LoginResult>() {

				@Override
				public void onFailure(Throwable caught) {
					LoadingIndicator.hide();
					getView().showThirdPartyUsernamePrompt();
					getProxy().manualReveal(RegisterPresenter.this);
				}

				@Override
				public void onSuccess(LoginResult loginResult) {
					AppUser user = loginResult.getAppUser();
					Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
					LoadingIndicator.hide();
					eventBus.fireEvent(new CurrentUserChangedEvent(user));
					if (user != null) {
						goToUserPage(user.getUsername());
					}
					else {
						// If they are not logged in, prompt for a username
						getView().showThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}

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
			if (provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
				// Create Google-linked account
				_dispatcher.execute(_restServiceFactory.user().createAppUserFromGoogleAccount(username), new AsyncCallback<LoginResult>() {

					@Override
					public void onFailure(Throwable caught) {
						getView().setThirdPartyUsernameError(caught.getMessage());
					}

					@Override
					public void onSuccess(LoginResult loginResult) {
						eventBus.fireEvent(new CurrentUserChangedEvent(loginResult.getAppUser()));
						registerContainer.appUserCreated();
					}
				});
			}
			else if (provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {
				// Create Twitter-linked account
				final String oauth_verifier = Window.Location.getParameter("oauth_verifier");

				_dispatcher.execute(_restServiceFactory.user().createAppUserFromTwitterAccount(username, oauth_verifier), new AsyncCallback<LoginResult>() {

					@Override
					public void onFailure(Throwable caught) {
						getView().setThirdPartyUsernameError(caught.getMessage());
					}

					@Override
					public void onSuccess(LoginResult loginResult) {
						AppUser createdUser = loginResult.getAppUser();
						Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
						eventBus.fireEvent(new CurrentUserChangedEvent(createdUser));
						registerContainer.appUserCreated();
						goToUserPage(createdUser.getUsername());
					}
				});
			}
			else if (provider.equals(RegisterPresenter.PROVIDER_FACEBOOK)) {
				// Create Facebook linked account
				final String state = Window.Location.getParameter("state");
				final String code = Window.Location.getParameter("code");
				_dispatcher.execute(_restServiceFactory.user().createAppUserFromFacebookAccount(username, state, code, facebookRedirect), new AsyncCallback<LoginResult>() {

					@Override
					public void onFailure(Throwable caught) {
						getView().setThirdPartyUsernameError(caught.getMessage());
					}

					@Override
					public void onSuccess(LoginResult loginResult) {
						AppUser createdUser = loginResult.getAppUser();
						Cookies.setCookie(Constants.SESSION_HEADER, loginResult.getSessionId());
						eventBus.fireEvent(new CurrentUserChangedEvent(createdUser));
						if (createdUser != null) {
							registerContainer.appUserCreated();
							goToUserPage(createdUser.getUsername());
						}
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

	private void goToUserPage(final String username) {
		getProxy().manualRevealFailed();
		final PlaceRequest place = new PlaceRequest.Builder().nameToken(NameTokens.lists).with(ListPresenter.USER_PARAM, username).build();
		placeManager.revealPlace(place);
	}
}

interface RegisterUiHandlers extends UiHandlers {
	void registerThirdParty(String username);
}
