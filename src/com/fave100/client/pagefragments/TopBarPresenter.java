package com.fave100.client.pagefragments;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHTML;
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
		InlineHTML getFaveFeed();
	}
	
	private ApplicationRequestFactory requestFactory;
	
	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view,
							final ApplicationRequestFactory requestFactory,
							final PlaceManager placeManager) {
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
		// TODO: Use manual reveal to avoid delay from AppUseRequest = how can we use manual reveal on presenterwidget?
		// Whenever the page is refreshed, check to see if the user is logged in or not
		// and change the top bar links and elements appropriately.
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		// We need the currentURL to redirect users back to this page
		// after a successful login
		String currentURL = Window.Location.getPath()+
				Window.Location.getQueryString()+Window.Location.getHash();
		Request<String> getLoginLogoutURLReq = appUserRequest.getGoogleLoginLogoutURL(currentURL);
		getLoginLogoutURLReq.fire(new Receiver<String>() {
			@Override public void onSuccess(final String url) {
				AppUserRequest appUserRequest = requestFactory.appUserRequest();
				Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.getLoggedInAppUser();
				getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(AppUserProxy appUser) {						
						if(appUser != null) {					
							getView().getGreeting().setInnerHTML(appUser.getUsername());
							getView().getMyFave100Link().setVisible(true);
							getView().getRegisterLink().setVisible(false);
							//getView().getLogInLogOutLink().setInnerHTML("<a href='"+url+"'>Log out</a>");
							getView().getLogInLogOutLink().setInnerHTML("<a href='"+Window.Location.getPath()
									+Window.Location.getQueryString()+"#"+NameTokens.logout+"'>Logout</a>");
						} else {
							getView().getGreeting().setInnerHTML("");
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
		
		// Update the FaveFeed
		Request<List<String>> faveFeedReq = requestFactory.appUserRequest().getFaveFeedForCurrentUser();
		faveFeedReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(List<String> faveFeed) {
				String output = "<ul>";
				for(String notification : faveFeed) {
					output += "<li>"+notification+"</li>";
				}
				output += "</ul>";
				getView().getFaveFeed().setVisible(true);
				getView().getFaveFeed().setHTML(output);
			}
			@Override
			public void onFailure(ServerFailure failure) {
				getView().getFaveFeed().setVisible(false);
			}
		});
		
	}
}
