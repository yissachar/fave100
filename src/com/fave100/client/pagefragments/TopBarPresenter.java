package com.fave100.client.pagefragments;

import java.util.List;

import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
		InlineHyperlink getLogInLogOutLink();
		SpanElement getGreeting();
		InlineHyperlink getMyFave100Link();
		InlineHyperlink getRegisterLink();
		InlineHTML getFaveFeed();
	}
	
	@Inject private ApplicationRequestFactory requestFactory;
	
	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}

	@Override
	protected void onBind() {
		super.onBind();	
	}
	
	@Override
	protected void onReveal() {
		super.onReveal();
		// TODO: Use manual reveal to avoid delay from AppUseRequest = but how can we use manual reveal on presenterwidget?
		
		// Whenever the page is refreshed, check to see if the user is logged in or not
		// and change the top bar links and elements appropriately.
				
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.getLoggedInAppUser();
		getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(AppUserProxy appUser) {						
				if(appUser != null) {					
					getView().getGreeting().setInnerHTML(appUser.getUsername());
					getView().getMyFave100Link().setVisible(true);
					getView().getRegisterLink().setVisible(false);
					getView().getLogInLogOutLink().setText("Log out");
					getView().getLogInLogOutLink().setTargetHistoryToken(NameTokens.logout);
				} else {
					getView().getGreeting().setInnerHTML("");
					getView().getMyFave100Link().setVisible(false);
					getView().getRegisterLink().setVisible(true);
					getView().getLogInLogOutLink().setText("Log in");
					getView().getLogInLogOutLink().setTargetHistoryToken(NameTokens.login);
				}				
			}
		});
		
		// Update the FaveFeed
		Request<List<String>> faveFeedReq = requestFactory.appUserRequest().getFaveFeedForCurrentUser();
		faveFeedReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(List<String> faveFeed) {
				SafeHtmlBuilder builder = new SafeHtmlBuilder();
				builder.appendHtmlConstant("<ul>");
				for(String notification : faveFeed) {
					builder.appendHtmlConstant("<li>");
					builder.appendEscaped(notification);
					builder.appendHtmlConstant("</li>");
				}
				builder.appendHtmlConstant("</ul>");
				getView().getFaveFeed().setVisible(true);
				getView().getFaveFeed().setHTML(builder.toSafeHtml());
			}
			@Override
			public void onFailure(ServerFailure failure) {
				getView().getFaveFeed().setVisible(false);
			}
		});
		
	}
}
