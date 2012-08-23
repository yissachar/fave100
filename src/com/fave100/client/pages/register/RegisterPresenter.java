package com.fave100.client.pages.register;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
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
		void setTwitterUrl(String url);
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
	
	public static final String PROVIDER_GOOGLE = "google";
	public static final String PROVIDER_TWITTER = "twitter";

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
						getView().showThirdPartyUsernamePrompt();
					} else {						
						getView().hideThirdPartyUsernamePrompt();
						getProxy().manualReveal(RegisterPresenter.this);
					}
				}
			});
		// TODO: Duplicate code with above, merge where possible	
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
					if(user != null) {						
						goToMyFave100();
					}
					getProxy().manualReveal(RegisterPresenter.this);
				}
			});			
		} else {
			getView().hideThirdPartyUsernamePrompt();
			getProxy().manualReveal(RegisterPresenter.this);
		}
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
				getView().setGoogleUrl(url);
			}
		});
		
		// TODO: Auth url will expire - need to regenerate on click, not on page refresh
		// Get the auth url for Twitter
		final Request<String> authUrlReq = requestFactory.appUserRequest().getTwitterAuthUrl();
		authUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(final String url) {
				getView().setTwitterUrl(url);
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
					appUserCreated();
				}
				@Override
				public void onFailure(final ServerFailure failure) {							
					getView().setNativeUsernameError(failure.getMessage().replace("Server Error:", ""));
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
						appUserCreated();
					}
					@Override
					public void onFailure(final ServerFailure failure) {
						getView().setThirdPartyUsernameError(failure.getMessage().replace("Server Error:", ""));
					}
				});
			} else if(provider.equals(RegisterPresenter.PROVIDER_TWITTER)){
				// Create Twitter-linked account
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
						getView().setThirdPartyUsernameError(failure.getMessage().replace("Server Error:", ""));
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
		
		// TODO: Must duplicate all validation on server...
		
		// Check for validity
		final String usernamePattern = "^[a-zA-Z0-9]+$";
		final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		
		if(username.equals("")) {
			getView().setNativeUsernameError("You must enter a username");
			valid = false;
		} else if(!username.matches(usernamePattern)) {
			getView().setNativeUsernameError("Username must only consist of letters and numbers");
			valid = false;
		}
		
		if(!email.matches(emailPattern)) {
			getView().setEmailError("Not a valid email address");
			valid = false;
		}
		
		if(password.equals("")) {
			getView().setPasswordError("You must enter a password");
			valid = false;
		} else if(!password.equals(passwordRepeat)) {
			getView().setPasswordRepeatError("Passwords must match");
			valid = false;
		}		
		return valid;
	}
	
	private boolean validateThirdPartyFields(final String username) {
		getView().clearThirdPartyErrors();
		if(username.equals("")) {
			getView().setThirdPartyUsernameError("You must enter a username");
			return false;
		}		
		return true;
	}
	
	private void appUserCreated() {
		placeManager.revealPlace(new PlaceRequest(NameTokens.myfave100));
		SideNotification.show("Thanks for registering!", false, 1500);
	}
	
	private void goToMyFave100() {
		// TODO: Any better method than this?
		// TODO: Instead - as soon as page loads, clean url and store vars 
		String url = Window.Location.getHref();
		url = url.replace(Window.Location.getParameter("oauth_token"), "");
		url = url.replace("&oauth_token=", "");
		url = url.replace(Window.Location.getParameter("oauth_verifier"), "");
		url = url.replace("&oauth_verifier=", "");
		url = url.replace(Window.Location.getHash(), "#myfave100");
		Window.Location.assign(url);		
	}
	
}

interface RegisterUiHandlers extends UiHandlers {
	void register(String username, String email, String password, String passwordRepeat);
	void registerThirdParty(String username);
}
