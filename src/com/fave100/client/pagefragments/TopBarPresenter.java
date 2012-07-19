package com.fave100.client.pagefragments;

import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHyperlink;

public class TopBarPresenter extends PresenterWidget<TopBarPresenter.MyView> {

	public interface MyView extends View {
		SpanElement getLogInLogOutLink();
		SpanElement getGreeting();
		InlineHyperlink getMyFave100Link();
	}
	
	private ApplicationRequestFactory requestFactory;
	private Boolean appUserLoggedIn = false; 
	
	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(eventBus);		
	}

	@Override
	protected void onBind() {
		super.onBind();	
	}
	
	@Override
	protected void onReveal() {
		super.onReveal();
		
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.findLoggedInAppUser();
		getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(AppUserProxy appUser) {
				if(appUser != null) {
					appUserLoggedIn = true;
					getView().getGreeting().setInnerHTML("Welcome "+appUser.getName());
					getView().getMyFave100Link().setVisible(true);
				} else {
					appUserLoggedIn = false;
					getView().getMyFave100Link().setVisible(false);					
				}
				// Create the login/logout URL as appropriate
				AppUserRequest appUserRequest = requestFactory.appUserRequest();				
				Request<String> loginURLReq = appUserRequest.getLoginLogoutURL(Window.Location.getPath()+Window.Location.getQueryString());
				loginURLReq.fire(new Receiver<String>() {
					@Override
					public void onSuccess(String response) {
						String loginLogoutString = (appUserLoggedIn) ? "Log out" : "Log in";
						getView().getLogInLogOutLink().setInnerHTML("<a href="+response+">"+loginLogoutString+"</a>");
					}			
				});
			}
		});
		/*
		Request<String> loginURLReq = appUserRequest.getLoginURL();
		loginURLReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(String response) {
				getView().getLogInLink().setInnerHTML("<a href="+response+">Log in</a>");
			}			
		});
		
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<String> idReq = appUserRequest.getGoogleIdForCurrentUser();
		idReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(String response) {				
				getView().getGreeting().setInnerHTML("Hi "+response);
			}			
		});*/
	}
}
