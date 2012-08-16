package com.fave100.client.pages.login;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class LoginPresenter extends
		Presenter<LoginPresenter.MyView, LoginPresenter.MyProxy> {

	public interface MyView extends View {		
		TextBox getUsernameInput();
		PasswordTextBox getPasswordInput();
		SpanElement getLoginStatusMessage();
		Button getLoginButton();
		Anchor getSignInWithGoogleButton();
		Anchor getSignInWithTwitterButton();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.login)
	public interface MyProxy extends ProxyPlace<LoginPresenter> {
	}
	
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	
	@Inject
	public LoginPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
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
		Request<String> loginUrlReq = appUserRequest.getGoogleLoginURL(Window.Location.getPath()+Window.Location.getQueryString()+"#"+
				NameTokens.register+";provider="+RegisterPresenter.PROVIDER_GOOGLE);
		loginUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(String url) {
				getView().getSignInWithGoogleButton().setHref(url);
			}
		});
		
		// Get the Twitter auth url
		Request<String> authUrlReq = requestFactory.appUserRequest().getTwitterAuthUrl();
		authUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(String url) {
				getView().getSignInWithTwitterButton().setHref(url);
			}
		});
		
		registerHandler(getView().getLoginButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				AppUserRequest appUserRequest = requestFactory.appUserRequest();
				Request<AppUserProxy> loginReq = appUserRequest.login(getView().getUsernameInput().getValue(),
						getView().getPasswordInput().getValue());

				// Clear the inputs immediately
				getView().getUsernameInput().setValue("");
				getView().getPasswordInput().setValue("");
				loginReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(AppUserProxy appUser) {						
						getView().getLoginStatusMessage().setInnerText("");
						placeManager.revealPlace(new PlaceRequest(NameTokens.myfave100));
					}
					@Override
					public void onFailure(ServerFailure failure) {
						getView().getLoginStatusMessage().setInnerText("Username or password incorrect.");
					}
				});
			}
		}));
	}
}
