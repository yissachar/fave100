package com.fave100.client.pages.register;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class RegisterPresenter extends
		Presenter<RegisterPresenter.MyView, RegisterPresenter.MyProxy> {

	public interface MyView extends View {
		SpanElement getUsernameStatusMessage();
		SpanElement getThirdPartyUsernameStatusMessage();
		SpanElement getEmailStatusMessage();
		TextBox getUsernameField();
		TextBox getEmailField();
		TextBox getThirdPartyUsernameField();
		PasswordTextBox getPasswordField();
		PasswordTextBox getPasswordRepeatField();
		SpanElement getPasswordStatusMessage();
		HTMLPanel getRegisterContainer();
		HTMLPanel getThirdPartyUsernamePrompt();
		Button getRegisterButton();
		Button getThirdPartyUsernameSubmitButton();
		Anchor getRegisterWithGoogleButton();
		Anchor getRegisterWithTwitterButton();
	}
	
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	public static final String PROVIDER_GOOGLE = "google";
	public static final String PROVIDER_TWITTER = "twitter";
	
	@Inject TopBarPresenter topBar;

	@ProxyCodeSplit
	@NameToken(NameTokens.register)
	public interface MyProxy extends ProxyPlace<RegisterPresenter> {
	}
	
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	private String provider;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
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
					// instead of RF every request, uses evebts)
					
					// Logged in user trying to register: redirect them to home
					placeManager.revealDefaultPlace();
				}
			}
		});		
		// TODO: Captcha
		
		provider = placeRequest.getParameter("provider", "");
		if(provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
			// The user is being redirected back to the register page after signing in to 
			// their 3rd party account - prompt them for a username and create their account
			
			// TODO: Can we do this in one request?
			// Make sure that the user is actually logged into Google
			final AppUserRequest appUserRequest = requestFactory.appUserRequest();
			final Request<Boolean> checkGoogleUserLoggedIn = appUserRequest.isGoogleUserLoggedIn();
			checkGoogleUserLoggedIn.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean loggedIn) {
					if(loggedIn) {
						final Request<AppUserProxy> loginWithGoogle = requestFactory.appUserRequest().loginWithGoogle();
						loginWithGoogle.fire(new Receiver<AppUserProxy>() {
							@Override
							public void onSuccess(final AppUserProxy user) {	
								if(user != null) {
									placeManager.revealDefaultPlace();
								}
								getProxy().manualReveal(RegisterPresenter.this);
							}
						});
						showThirdPartyUsernamePrompt();
					} else {						
						hideThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}
			});
		// TODO: Duplicate code with above, merge where possible	
		} else if(provider.equals(RegisterPresenter.PROVIDER_TWITTER)) {
			// The user is being redirected back to the register page after signing in to 
			// their 3rd party account - prompt them for a username and create their account
			
			//TODO: Need to show some loading icon or something while waiting for the RF req
			getView().getRegisterContainer().setVisible(false);
			// Try to log the user in with Twitter
			final String oauth_verifier = Window.Location.getParameter("oauth_verifier");
			final Request<AppUserProxy> loginWithTwitter = requestFactory.appUserRequest().loginWithTwitter(oauth_verifier);
			loginWithTwitter.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy user) {	
					if(user != null) {						
						goToMyFave100();
					} else {
						showThirdPartyUsernamePrompt();
					}
					getProxy().manualReveal(RegisterPresenter.this);
				}
			});			
		} else {
			hideThirdPartyUsernamePrompt();
			getProxy().manualReveal(RegisterPresenter.this);
		}
	}
	
	private void showThirdPartyUsernamePrompt() {
		getView().getThirdPartyUsernamePrompt().setVisible(true);
		getView().getRegisterContainer().setVisible(false);
	}
	
	private void hideThirdPartyUsernamePrompt() {
		getView().getThirdPartyUsernamePrompt().setVisible(false);
		getView().getRegisterContainer().setVisible(true);
	}
	

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		
		// Get the login url for Google
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<String> loginUrlReq = appUserRequest.getGoogleLoginURL(Window.Location.getHref()+";provider="+RegisterPresenter.PROVIDER_GOOGLE);
		loginUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(final String url) {
				getView().getRegisterWithGoogleButton().setHref(url);
			}
		});
		
		// TODO: Auth url will expire - need to regenerate on click, not on page refresh
		// Get the auth url for Twitter
		final Request<String> authUrlReq = requestFactory.appUserRequest().getTwitterAuthUrl();
		authUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(final String url) {
				getView().getRegisterWithTwitterButton().setHref(url);
			}
		});
		
		registerHandler(getView().getRegisterButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if(validateFields()) {
					final AppUserRequest appUserRequest = requestFactory.appUserRequest();
					// Create a new user with the username and password entered
					final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUser(getView().getUsernameField().getValue(),
							getView().getPasswordField().getValue(), getView().getEmailField().getValue());
					clearFields();				
					createAppUserReq.fire(new Receiver<AppUserProxy>() {
						@Override
						public void onSuccess(final AppUserProxy createdUser) {
							appUserCreated();
						}
						@Override
						public void onFailure(final ServerFailure failure) {
							getView().getUsernameStatusMessage().setInnerText(failure.getMessage().replace("Server Error:", ""));
							getView().getThirdPartyUsernameField().addStyleName("errorInput");
						}
					});
				}
			}
		}));
		
		registerHandler(getView().getThirdPartyUsernameSubmitButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(final ClickEvent event) {
				if(validateThirdPartyFields()) {
					final AppUserRequest appUserRequest = requestFactory.appUserRequest();
					if(provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {	
						// Create Google-linked account
						final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromGoogleAccount(getView().getThirdPartyUsernameField().getValue());
						createAppUserReq.fire(new Receiver<AppUserProxy>() {
							@Override
							public void onSuccess(final AppUserProxy createdUser) {
								appUserCreated();
							}
							@Override
							public void onFailure(final ServerFailure failure) {
								getView().getThirdPartyUsernameStatusMessage().setInnerText(failure.getMessage().replace("Server Error:", ""));
								getView().getThirdPartyUsernameField().addStyleName("errorInput");
							}
						});
					} else if(provider.equals(RegisterPresenter.PROVIDER_TWITTER)){
						// Create Twitter-linked account
						final String username = getView().getThirdPartyUsernameField().getValue();
						final String oauth_verifier = Window.Location.getParameter("oauth_verifier");
						final Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromTwitterAccount(username, oauth_verifier);
						createAppUserReq.fire(new Receiver<AppUserProxy>() {
							@Override
							public void onSuccess(final AppUserProxy createdUser) {
								//TODO: Not working if user already logged into twitter
								appUserCreated();
								// TODO: clean url from twitter on success
								goToMyFave100();
							}
							@Override
							public void onFailure(final ServerFailure failure) {
								getView().getThirdPartyUsernameStatusMessage().setInnerText(failure.getMessage().replace("Server Error:", ""));
								getView().getThirdPartyUsernameField().addStyleName("errorInput");
							}
						});
					}
					
				}
			}
		}));		
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);  
	}
	
	private boolean validateFields() {		
		// Assume all valid
		getView().getUsernameField().removeStyleName("errorInput");
		getView().getPasswordField().removeStyleName("errorInput");
		getView().getEmailField().removeStyleName("errorInput");
		getView().getPasswordRepeatField().removeStyleName("errorInput");
		getView().getUsernameStatusMessage().setInnerText("");
		getView().getPasswordStatusMessage().setInnerText("");
		getView().getEmailStatusMessage().setInnerText("");
		
		// TODO: Must duplicate all validation on server...
		
		// Check for validity
		final String username = getView().getUsernameField().getValue();
		final String email = getView().getEmailField().getValue();
		final String password = getView().getPasswordField().getValue();
		final String passwordConfirm = getView().getPasswordRepeatField().getValue();
		if(username.equals("")) {
			getView().getUsernameField().addStyleName("errorInput");
			getView().getUsernameStatusMessage().setInnerText("You must enter a username");
			return false;
		}
		final String usernamePattern = "^[a-zA-Z0-9]+$";
		if(!username.matches(usernamePattern)) {
			getView().getUsernameField().addStyleName("errorInput");
			getView().getUsernameStatusMessage().setInnerText("Username must only consist of letters and numbers");
			return false;
		}
		final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		if(!email.matches(emailPattern)) {
			getView().getEmailStatusMessage().setInnerText("Not a valid email address");
			getView().getEmailField().addStyleName("errorInput");
			return false;
		}
		if(password.equals("")) {
			getView().getPasswordStatusMessage().setInnerText("You must enter a password");
			getView().getPasswordField().addStyleName("errorInput");
			return false;
		}
		if(!password.equals(passwordConfirm)) {
			getView().getPasswordStatusMessage().setInnerText("Passwords must match");
			getView().getPasswordField().addStyleName("errorInput");
			getView().getPasswordRepeatField().addStyleName("errorInput");
			return false;
		}		
		return true;
	}
	
	private boolean validateThirdPartyFields() {
		getView().getThirdPartyUsernameField().removeStyleName("errorInput");				
		final String username = getView().getThirdPartyUsernameField().getValue();
		if(username.equals("")) {
			getView().getThirdPartyUsernameStatusMessage().setInnerText("You must enter a username");	
			getView().getThirdPartyUsernameField().addStyleName("errorInput");
			return false;
		}		
		return true;
	}
	
	private void clearFields() {
		getView().getUsernameField().setValue("");
		getView().getEmailField().setValue("");
		getView().getPasswordField().setValue("");
		getView().getPasswordRepeatField().setValue("");
		getView().getThirdPartyUsernameField().setValue("");
	}	
	
	private void appUserCreated() {
		placeManager.revealPlace(new PlaceRequest(NameTokens.myfave100));
		SideNotification.show("Thanks for registering!", false, 1500);
	}
	
	private void goToMyFave100() {
		// TODO: Any better method than this?
		String url = Window.Location.getHref();
		url = url.replace(Window.Location.getParameter("oauth_token"), "");
		url = url.replace("&oauth_token=", "");
		url = url.replace(Window.Location.getParameter("oauth_verifier"), "");
		url = url.replace("&oauth_verifier=", "");
		url = url.replace(Window.Location.getHash(), "#myfave100");
		Window.Location.assign(url);
		
	}
}
