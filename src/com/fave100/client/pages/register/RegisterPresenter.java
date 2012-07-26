package com.fave100.client.pages.register;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class RegisterPresenter extends
		Presenter<RegisterPresenter.MyView, RegisterPresenter.MyProxy> {

	public interface MyView extends View {
		HTMLPanel getSignInWithGoogleSpan();
		HTMLPanel getRegisterContainer();
	}
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject TopBarPresenter topBar;

	@ProxyCodeSplit
	@NameToken(NameTokens.register)
	public interface MyProxy extends ProxyPlace<RegisterPresenter> {
	}
	
	private ApplicationRequestFactory requestFactory;

	@Inject
	public RegisterPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
	
	@Override
	protected void onReveal() {
		//TODO: If the user has already registered, redirect/otherwise handle
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);  
		
	    
	    // Check whether the user is signed in to their Google account and set links
	    AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<Boolean> checkGoogleUserLoggedIn = appUserRequest.isGoogleUserLoggedIn();
		checkGoogleUserLoggedIn.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(Boolean loggedIn) {
				// We need the currentURL to redirect users back to this page after a successful login 
				String currentURL = Window.Location.getPath()+
						Window.Location.getQueryString()+Window.Location.getHash();
				if(loggedIn) {		
					// User signed in with Google account, allow them to create Fave100 account
					getView().getSignInWithGoogleSpan().setVisible(false);
					getView().getRegisterContainer().setVisible(true);
					//getView().getGreeting().setInnerHTML("Welcome "+appUser.getUsername());
					//getView().getMyFave100Link().setVisible(true);
					//getView().getRegisterLink().setVisible(false);
					//getView().getLogInLogOutLink().setInnerHTML("<a href='/_ah/logout?continue="+currentURL+"'>Log out</a>");
				} else {
					// User is not signed in with Google account, ask them to sign in
					getView().getSignInWithGoogleSpan().setVisible(true);
					getView().getRegisterContainer().setVisible(false);	
//					getView().getSignInWithGoogleSpan().set("Please <a href='/_ah/login?continue="+currentURL+"'>sign in</a>"+
//							"with your Google account in order to create a Fave100 account.");
					//getView().getMyFave100Link().setVisible(false);
					//getView().getRegisterLink().setVisible(true);				
					//getView().getLogInLogOutLink().setInnerHTML("<a href='/_ah/login?continue="+currentURL+"'>Log in</a>");
				}				
			}
		});
	}
}
