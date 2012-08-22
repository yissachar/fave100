package com.fave100.client.pages.users;

import java.util.List;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.widgets.NonpersonalFaveList;
import com.fave100.server.domain.FaveList;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class UsersPresenter extends
		Presenter<UsersPresenter.MyView, UsersPresenter.MyProxy> {

	public interface MyView extends View {		
		HTMLPanel getUserProfile();
		Image getAvatar();
		SpanElement getUsernameSpan();		
		Button getFollowButton();
		NonpersonalFaveList getUserFaveList();
		Anchor getFave100TabLink();
		Anchor getActivityTabLink();
		InlineHTML getActivityTab();
	}
		
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	public static final String FAVE_100_TAB = "fave100";
	public static final String ACTIVITY_TAB = "activity";
	
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
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);
		requestedUsername = placeRequest.getParameter("u", "");	
		if(requestedUsername.equals("")) {
			placeManager.revealDefaultPlace();
		} else {
			// Update follow button
			final Request<Boolean> checkFollowing = requestFactory.appUserRequest().checkFollowing(requestedUsername);
			checkFollowing.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean following) {
					final Button followButton = getView().getFollowButton();
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
		    final Request<AppUserProxy> userReq = requestFactory.appUserRequest().findAppUser(requestedUsername);			
		    userReq.fire(new Receiver<AppUserProxy>() {
		    	@Override
		    	public void onSuccess(final AppUserProxy user) {
		    		if(user != null) {	    				
		    			// Upate user profile
	    				getView().getAvatar().setUrl(user.getAvatar());
	    				getView().getUsernameSpan().setInnerText(user.getUsername());
	    			} else {
	    				placeManager.revealDefaultPlace();
	    			}
		    		getProxy().manualReveal(UsersPresenter.this);
		    	}
		    });		
		    
		    // Update fave list
			final Request<List<FaveItemProxy>> userFaveListReq = requestFactory.faveListRequest().getFaveList(requestedUsername, FaveList.DEFAULT_HASHTAG);
		    userFaveListReq.fire(new Receiver<List<FaveItemProxy>>() {
		    	@Override
		    	public void onSuccess(final List<FaveItemProxy> faveList) {
		    		if(faveList != null) {	    	
	    	    		getView().getUserFaveList().setRowData(faveList);
	    			} else {
	    				placeManager.revealDefaultPlace();
	    			}
		    		getProxy().manualReveal(UsersPresenter.this);
		    	}
		    });	
		    
		    final String tab = placeRequest.getParameter("tab", UsersPresenter.FAVE_100_TAB);
		    if(tab.equals(UsersPresenter.ACTIVITY_TAB)) {
		    	getView().getActivityTab().setVisible(true);
		    	getView().getUserFaveList().setVisible(false);
		    	getView().getFave100TabLink().removeStyleName("selected");
				getView().getActivityTabLink().addStyleName("selected");
				final Request<List<String>> getActivityReq = requestFactory.appUserRequest().getActivityForUser(requestedUsername);
				getActivityReq.fire(new Receiver<List<String>>() {
					@Override
					public void onSuccess(final List<String> activityList) {
						final SafeHtmlBuilder builder = new SafeHtmlBuilder();
						builder.appendHtmlConstant("<ul>");
						for(final String activity : activityList) {
							builder.appendHtmlConstant("<li>");
							builder.appendEscaped(activity);
							builder.appendHtmlConstant("</li>");
						}
						builder.appendHtmlConstant("</ul>");
						getView().getActivityTab().setHTML(builder.toSafeHtml());
					}
				});
		    } else if(tab.equals(UsersPresenter.FAVE_100_TAB)) {
		    	getView().getUserFaveList().setVisible(true);
		    	getView().getActivityTab().setVisible(false);
		    	getView().getActivityTabLink().removeStyleName("selected");
				getView().getFave100TabLink().addStyleName("selected");
		    }
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		
		// Follow button
		registerHandler(getView().getFollowButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(final ClickEvent event) {
				if(!getView().getFollowButton().getStyleName().contains("alreadyFollowing")) {
					final AppUserRequest appUserRequest = requestFactory.appUserRequest();
					final Request<Void> followReq = appUserRequest.followUser(requestedUsername);
					followReq.fire(new Receiver<Void>() {
						@Override
						public void onSuccess(final Void response) {
							SideNotification.show("Following!");
							getView().getFollowButton().setHTML("Following");
							getView().getFollowButton().setEnabled(false);
						}
						@Override
						public void onFailure(final ServerFailure failure) {
							SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
						}
					});
				}
			}
		}));
		
		// Fave100 tab link
		registerHandler(getView().getFave100TabLink().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(final ClickEvent event) {				
				placeManager.revealPlace(new PlaceRequest(NameTokens.users).with("u", requestedUsername).with("tab", UsersPresenter.FAVE_100_TAB));				
			}
		}));
		
		// Activity tab link
		registerHandler(getView().getActivityTabLink().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(final ClickEvent event) {				
				placeManager.revealPlace(new PlaceRequest(NameTokens.users).with("u", requestedUsername).with("tab", UsersPresenter.ACTIVITY_TAB));
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
