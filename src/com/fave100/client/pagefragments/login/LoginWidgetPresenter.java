package com.fave100.client.pagefragments.login;

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
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class LoginWidgetPresenter extends
		PresenterWidget<LoginWidgetPresenter.MyView>
		implements LoginUiHandlers{

	public interface MyView extends View, HasUiHandlers<LoginUiHandlers> {
		String getUsername();
		String getPassword();
		void clearUsername();
		void clearPassword();		
		void setError(String error);
		void setGoogleLoginUrl(String url);
	}
	
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;

	@Inject
	public LoginWidgetPresenter(final EventBus eventBus, final MyView view,
			final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);
	}

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
				placeManager.revealPlace(new PlaceRequest(NameTokens.home));						
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				getView().setError("Username or password incorrect");
			}
		});
	}
	
	@Override
	public void goToTwitterAuth() {
		String redirect = "http://"+Window.Location.getHost()+Window.Location.getPath();
		redirect += Window.Location.getQueryString()+"#"+NameTokens.register+";provider="+RegisterPresenter.PROVIDER_TWITTER;
		final Request<String> authUrlReq = requestFactory.appUserRequest().getTwitterAuthUrl(redirect);
		authUrlReq.fire(new Receiver<String>() {
			@Override 
			public void onSuccess(final String url) {
				Window.Location.assign(url);
			}
		});
	}
}


interface LoginUiHandlers extends UiHandlers {
	void login();
	void goToTwitterAuth();
}