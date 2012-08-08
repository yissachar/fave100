package com.fave100.client.pagefragments;

import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHyperlink;

/**
 * Top navigation bar that will be included on every page.
 * @author yissachar.radcliffe
 *
 */
public class TopBarPresenter extends PresenterWidget<TopBarPresenter.MyView> {

	public interface MyView extends View {
		SpanElement getLogInLogOutLink();
		SpanElement getGreeting();
		InlineHyperlink getMyFave100Link();
		InlineHyperlink getRegisterLink();
	}
	
	private ApplicationRequestFactory requestFactory;
	
	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view,
							final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		this.requestFactory = requestFactory;
	}

	@Override
	protected void onBind() {
		super.onBind();	
	}
	
	@Override
	protected void onReveal() {
		super.onReveal();
		// TODO: Use manual reveal to avoid delay from AppUseRequest
		
		// Whenever the page is refreshed, check to see if the user is logged in or not
		// and change the top bar links and elements appropriately.
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		// We need the currentURL to redirect users back to this page
		// after a successful login
		String currentURL = Window.Location.getPath()+
				Window.Location.getQueryString()+Window.Location.getHash();
		Request<String> getLoginLogoutURLReq = appUserRequest.getLoginLogoutURL(currentURL);
		getLoginLogoutURLReq.fire(new Receiver<String>() {
			@Override public void onSuccess(final String url) {
				AppUserRequest appUserRequest = requestFactory.appUserRequest();
				Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.getLoggedInAppUser();
				getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(AppUserProxy appUser) {						
						if(appUser != null) {					
							getView().getGreeting().setInnerHTML("Welcome "+appUser.getUsername());
							getView().getMyFave100Link().setVisible(true);
							getView().getRegisterLink().setVisible(false);
							//getView().getLogInLogOutLink().setInnerHTML("<a href='"+url+"'>Log out</a>");
							getView().getLogInLogOutLink().setInnerHTML("<a href='"+Window.Location.getPath()
									+Window.Location.getQueryString()+"#"+NameTokens.logout+"'>Logout</a>");
						} else {
							getView().getMyFave100Link().setVisible(false);
							getView().getRegisterLink().setVisible(true);				
							//getView().getLogInLogOutLink().setInnerHTML("<a href='"+url+"'>Log in</a>");
							getView().getLogInLogOutLink().setInnerHTML("<a href='"+Window.Location.getPath()
									+Window.Location.getQueryString()+"#"+NameTokens.login+"'>Login</a>");
						}				
					}
				});
			}
		});
	}
}
