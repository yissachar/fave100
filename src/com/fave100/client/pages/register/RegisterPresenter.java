package com.fave100.client.pages.register;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
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
	}
	
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	public static final String PROVIDER_GOOGLE = "google";	
	
	@Inject TopBarPresenter topBar;

	@ProxyCodeSplit
	@NameToken(NameTokens.register)
	public interface MyProxy extends ProxyPlace<RegisterPresenter> {
	}
	
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
	}
	
	@Override
	public void prepareFromRequest(PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);
		
		Request<AppUserProxy> getLoggedInUserReq =  requestFactory.appUserRequest().getLoggedInAppUser();
		getLoggedInUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(AppUserProxy user) {
				if(user != null) {
					// TODO: Gatekeeper instead?
					// Logged in user trying to register: redirect them to home
					placeManager.revealDefaultPlace();
				}
			}
		});		
		
		
		String provider = placeRequest.getParameter("provider", "");
		if(provider.equals(RegisterPresenter.PROVIDER_GOOGLE)) {
			// The user is being redirected back to the register page after signing in to 
			// their 3rd party account - prompt them for a username and create their account
			
			// TODO: Can we do this in one request?
			// Make sure that the user is actually logged into Google
			AppUserRequest appUserRequest = requestFactory.appUserRequest();
			Request<Boolean> checkGoogleUserLoggedIn = appUserRequest.isGoogleUserLoggedIn();
			checkGoogleUserLoggedIn.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(Boolean loggedIn) {
					if(loggedIn) {
						Request<AppUserProxy> loginWithGoogle = requestFactory.appUserRequest().loginWithGoogle();
						loginWithGoogle.fire(new Receiver<AppUserProxy>() {
							@Override
							public void onSuccess(AppUserProxy user) {
								placeManager.revealDefaultPlace();
							}
						});
						showThirdPartyUsernamePrompt();
					} else {
						hideThirdPartyUsernamePrompt();
					}				
				}
			});			
		} else {
			hideThirdPartyUsernamePrompt();
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
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<String> loginUrlReq = appUserRequest.getGoogleLoginURL(Window.Location.getHref()+";provider="+RegisterPresenter.PROVIDER_GOOGLE);
		loginUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(String url) {
				getView().getRegisterWithGoogleButton().setHref(url);
			}
		});
		
		registerHandler(getView().getRegisterButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(validateFields()) {
					AppUserRequest appUserRequest = requestFactory.appUserRequest();
					// TODO: password error message if don't match
					// Create a new user with the username and password entered
					Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUser(getView().getUsernameField().getValue(),
							getView().getPasswordField().getValue(), getView().getEmailField().getValue());
					clearFields();				
					createAppUserReq.fire(new Receiver<AppUserProxy>() {
						@Override
						public void onSuccess(AppUserProxy createdUser) {
							appUserCreated();
						}
						@Override
						public void onFailure(ServerFailure failure) {
							getView().getUsernameStatusMessage().setInnerText(failure.getMessage().replace("Server Error:", ""));
							getView().getThirdPartyUsernameField().addStyleName("errorInput");
						}
					});
				}
			}
		}));
		
		registerHandler(getView().getThirdPartyUsernameSubmitButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if(validateThirdPartyFields()) {
					AppUserRequest appUserRequest = requestFactory.appUserRequest();
					Request<AppUserProxy> createAppUserReq = appUserRequest.createAppUserFromGoogleAccount(getView().getThirdPartyUsernameField().getValue());
					createAppUserReq.fire(new Receiver<AppUserProxy>() {
						@Override
						public void onSuccess(AppUserProxy createdUser) {
							appUserCreated();
						}
						@Override
						public void onFailure(ServerFailure failure) {
							getView().getThirdPartyUsernameStatusMessage().setInnerText(failure.getMessage().replace("Server Error:", ""));
							getView().getThirdPartyUsernameField().addStyleName("errorInput");
						}
					});
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
		
		// Check for validity
		String username = getView().getUsernameField().getValue();
		String email = getView().getEmailField().getValue();
		String password = getView().getPasswordField().getValue();
		String passwordConfirm = getView().getPasswordRepeatField().getValue();
		if(username.equals("")) {
			getView().getUsernameField().addStyleName("errorInput");
			getView().getUsernameStatusMessage().setInnerText("You must enter a username");
			return false;
		}
		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
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
		String username = getView().getThirdPartyUsernameField().getValue();
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
}
