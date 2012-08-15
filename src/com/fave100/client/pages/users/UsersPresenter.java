package com.fave100.client.pages.users;

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
import com.fave100.client.widgets.FaveDataGrid;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class UsersPresenter extends
		Presenter<UsersPresenter.MyView, UsersPresenter.MyProxy> {

	public interface MyView extends View {		
		HTMLPanel getUserProfile();
		Image getAvatar();
		SpanElement getUsernameSpan();		
		Button getFollowButton();
		FaveDataGrid getUserFaveDataGrid();
	}
	
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject TopBarPresenter topBar;
	
	private String requestedUsername;
	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;

	@ProxyCodeSplit
	@NameToken(NameTokens.users)
	public interface MyProxy extends ProxyPlace<UsersPresenter> {
	}

	@Inject
	public UsersPresenter(final EventBus eventBus, final MyView view,
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
	public boolean useManualReveal() {
		return true;
	}
	
	@Override
	public void prepareFromRequest(PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);
		requestedUsername = placeRequest.getParameter("u", "");	
		if(requestedUsername.equals("")) {
			placeManager.revealDefaultPlace();
		} else {
			// Update follow button
			Request<Boolean> checkFollowing = requestFactory.appUserRequest().checkFollowing(requestedUsername);
			checkFollowing.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(Boolean following) {
					Button followButton = getView().getFollowButton();
					if(following) {
						followButton.setHTML("Following");
						followButton.setEnabled(false);
					} else {
						followButton.setHTML("Follow");
						followButton.setEnabled(true);
					}
				}
			});
			
			// Update user profile
		    Request<AppUserProxy> masterFaveListReq = requestFactory.appUserRequest().findAppUser(requestedUsername).with("fave100Songs");
		    masterFaveListReq.fire(new Receiver<AppUserProxy>() {
		    	@Override
		    	public void onSuccess(AppUserProxy user) {
		    		if(user != null) {	    				
		    			// Upate user profile
	    				getView().getAvatar().setUrl(user.getAvatar());
	    				getView().getUsernameSpan().setInnerText(user.getUsername());
	    	    		getView().getUserFaveDataGrid().setRowData(user.getFave100Songs());
	    	    		getView().getUserFaveDataGrid().resizeFaveList();
	    			} else {
	    				placeManager.revealDefaultPlace();
	    			}
		    		getProxy().manualReveal(UsersPresenter.this);
		    	}
		    });			
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		
		registerHandler(getView().getFollowButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if(!getView().getFollowButton().getStyleName().contains("alreadyFollowing")) {
					AppUserRequest appUserRequest = requestFactory.appUserRequest();
					Request<Void> followReq = appUserRequest.followUser(requestedUsername);
					followReq.fire(new Receiver<Void>() {
						@Override
						public void onSuccess(Void response) {
							SideNotification.show("Following!");
							getView().getFollowButton().setHTML("Following");
							getView().getFollowButton().setEnabled(false);
						}
						@Override
						public void onFailure(ServerFailure failure) {
							SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
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
	    // TODO: handle user visiting own page	    
	}
}
