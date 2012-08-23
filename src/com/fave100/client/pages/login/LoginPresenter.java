package com.fave100.client.pages.login;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.register.RegisterPresenter;
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

public class LoginPresenter extends
		BasePresenter<LoginPresenter.MyView, LoginPresenter.MyProxy>
		implements LoginUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<LoginUiHandlers> {	
		String getUsername();
		String getPassword();
		void clearUsername();
		void clearPassword();		
		void setError(String error);
		void setGoogleLoginUrl(String url);
		void setTwitterLoginUrl(String url);
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
		getView().setUiHandlers(this);
		//TODO: Should we have autocomplete for the username box?
		// See: http://code.google.com/p/google-web-toolkit-incubator/wiki/LoginSecurityFAQ
	}
	
	//TODO: Twitter login not working on appspot

	@Override
	protected void onBind() {
		super.onBind();
		
		// Get the login url for Google
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<String> loginUrlReq = appUserRequest.getGoogleLoginURL(Window.Location.getPath()+Window.Location.getQueryString()+"#"+
				NameTokens.register+";provider="+RegisterPresenter.PROVIDER_GOOGLE);
		loginUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(final String url) {
				getView().setGoogleLoginUrl(url);
			}
		});
		
		// Get the Twitter auth url
		final Request<String> authUrlReq = requestFactory.appUserRequest().getTwitterAuthUrl();
		authUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(final String url) {
				getView().setTwitterLoginUrl(url);
			}
		});
	}	

	@Override
	public void login() {
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<AppUserProxy> loginReq = appUserRequest.login(getView().getUsername(),
				getView().getPassword());

		// Clear the inputs immediately
		getView().clearPassword();
		loginReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				getView().clearUsername();
				placeManager.revealPlace(new PlaceRequest(NameTokens.myfave100));						
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				getView().setError("Username or password incorrect");
			}
		});
	}
}

interface LoginUiHandlers extends UiHandlers {
	void login();
}
