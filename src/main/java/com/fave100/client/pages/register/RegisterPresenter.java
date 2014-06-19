package com.fave100.client.pages.register;

import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.gatekeepers.NotLoggedInGatekeeper;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FacebookRegistration;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.TwitterRegistration;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.fave100.client.pages.PagePresenter;
import com.fave100.shared.Validator;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * Registration page
 * 
 * @author yissachar.radcliffe
 * 
 */
public class RegisterPresenter extends
		PagePresenter<RegisterPresenter.MyView, RegisterPresenter.MyProxy>
		implements RegisterUiHandlers {

	public interface MyView extends View, HasUiHandlers<RegisterUiHandlers> {
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

	public static final String CODE_PARAM = "code";
	public static final String STATE_PARAM = "state";
	public static final String PROVIDER_PARAM = "provider";
	public static final String OAUTH_VERIFIER_PARAM = "oauth_verifier";

	public static final String PROVIDER_GOOGLE = "google";
	public static final String PROVIDER_TWITTER = "twitter";
	public static final String PROVIDER_FACEBOOK = "facebook";

	private EventBus eventBus;
	private PlaceManager placeManager;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private String _provider;
	private String _oauthVerifier;
	private String _state;
	private String _code;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final PlaceManager placeManager,
								final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
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

		_code = placeRequest.getParameter(CODE_PARAM, "");
		_state = placeRequest.getParameter(STATE_PARAM, "");
		_provider = placeRequest.getParameter(PROVIDER_PARAM, "");
		_oauthVerifier = placeRequest.getParameter(OAUTH_VERIFIER_PARAM, "");

		// Callback that attempts to login user, on failure prompts for username registration
		final AsyncCallback<AppUser> loginCallback = new AsyncCallback<AppUser>() {

			@Override
			public void onFailure(Throwable caught) {
				getView().showThirdPartyUsernamePrompt();
				getProxy().manualReveal(RegisterPresenter.this);
			}

			@Override
			public void onSuccess(AppUser user) {
				eventBus.fireEvent(new CurrentUserChangedEvent(user));
				goToUserPage(user.getUsername());
			}
		};

		// The user is being redirected back to the register page after signing in to their 3rd party account 
		// prompt them for a username and create their account
		if (_provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
			_dispatcher.execute(_restServiceFactory.auth().loginWithGoogle(), loginCallback);
		}
		else if (_provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {
			// First see if they are already logged in
			StringResult _oauthVerifierWrapper = new StringResult();
			_oauthVerifierWrapper.setValue(_oauthVerifier);

			_dispatcher.execute(_restServiceFactory.auth().loginWithTwitter(_oauthVerifierWrapper), loginCallback);
		}
		else if (_provider.equals(RegisterPresenter.PROVIDER_FACEBOOK)) {
			StringResult codeWrapper = new StringResult();
			codeWrapper.setValue(_code);

			_dispatcher.execute(_restServiceFactory.auth().loginWithFacebook(codeWrapper), loginCallback);
		}
		else {
			getView().hideThirdPartyUsernamePrompt();
			getProxy().manualReveal(RegisterPresenter.this);
		}
	}

	@Override
	public void onReveal() {
		super.onReveal();
		registerContainer.setShortNames(false);
		setInSlot(REGISTER_SLOT, registerContainer);
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().clearFields();
		getView().clearThirdPartyErrors();
	}

	@Override
	public void registerThirdParty(final String username) {
		if (validateThirdPartyFields(username)) {
			RestCallback<AppUser> registerCallback = new RestCallback<AppUser>() {

				@Override
				public void setResponse(Response response) {
					if (response.getStatusCode() >= 400) {
						getView().setThirdPartyUsernameError(response.getText());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// Already handled in setResponse
				}

				@Override
				public void onSuccess(AppUser appUser) {
					eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
					registerContainer.appUserCreated(appUser);
				}
			};

			if (_provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {

				StringResult usernameWrapper = new StringResult();
				usernameWrapper.setValue(username);

				_dispatcher.execute(_restServiceFactory.auth().createAppUserFromGoogleAccount(usernameWrapper), registerCallback);
			}
			else if (_provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {

				TwitterRegistration registration = new TwitterRegistration();
				registration.setUsername(username);
				registration.setOauthVerifier(_oauthVerifier);

				_dispatcher.execute(_restServiceFactory.auth().createAppUserFromTwitterAccount(registration), registerCallback);
			}
			else if (_provider.equals(RegisterPresenter.PROVIDER_FACEBOOK)) {

				FacebookRegistration registration = new FacebookRegistration();
				registration.setUsername(username);
				registration.setCode(_state);

				_dispatcher.execute(_restServiceFactory.auth().createAppUserFromFacebookAccount(registration), registerCallback);
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
		final PlaceRequest place = new PlaceRequest.Builder().nameToken(NameTokens.lists).with(PlaceParams.USER_PARAM, username).build();
		placeManager.revealPlace(place);
	}
}

interface RegisterUiHandlers extends UiHandlers {
	void registerThirdParty(String username);
}
