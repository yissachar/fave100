package com.fave100.client.pagefragments;

import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Top navigation bar that will be included on every page.
 * @author yissachar.radcliffe
 *
 */
public class TopBarPresenter extends PresenterWidget<TopBarPresenter.MyView> {

	public interface MyView extends View {		
		void setLoggedIn(String username);
		void setLoggedOut();
	}
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject private LoginWidgetPresenter loginBox;
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
		
		 setInSlot(LOGIN_SLOT, loginBox);
		// TODO: Use manual reveal to avoid delay from AppUseRequest = but how can we use manual reveal on a presenterwidget?
		
		// Whenever the page is refreshed, check to see if the user is logged in or not
		// and change the top bar links and elements appropriately.
				
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.getLoggedInAppUser();
		getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {						
				if(appUser != null) {					
					getView().setLoggedIn(appUser.getUsername());
				} else {
					getView().setLoggedOut();
				}				
			}
		});		
	}
}
