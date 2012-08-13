package com.fave100.client.pages.users;

import java.util.List;

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
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHTML;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class UsersPresenter extends
		Presenter<UsersPresenter.MyView, UsersPresenter.MyProxy> {

	public interface MyView extends View {
		InlineHTML getUserList();
		InlineHTML getUserProfile();
		InlineHTML getFollowButton();
		FaveDataGrid getUserFaveDataGrid();
	}
	
	private String requestedUser;
	private ApplicationRequestFactory requestFactory;
	private HandlerRegistration URLHandlerRegistration;
	
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject TopBarPresenter topBar;

	@ProxyCodeSplit
	@NameToken(NameTokens.users)
	public interface MyProxy extends ProxyPlace<UsersPresenter> {
	}

	@Inject
	public UsersPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}
	
	@Override
	public void prepareFromRequest(PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);
		requestedUser = placeRequest.getParameter("u", "");				
	}

	@Override
	protected void onBind() {
		super.onBind();
		
		// By default, just changing the parameters in the URL will not trigger onReveal,
		// Therefore we must listen for URL change and then trigger onReveal manually
		// TODO: This is not the most robust way of dealing with the problem; try to find
		// another way.
		ValueChangeHandler<String> URLHandler = new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String hash = event.getValue();
				if(hash.length() == 0) return;
				String[] historyTokens = hash.split("&",0);				
				if(historyTokens.length == 0 || !historyTokens[0].contains(";")) {
					refreshUserList();
				} else {
					requestedUser = historyTokens[0].split("=")[1];
					refreshUserFave();
				}
			}
		};
		
		URLHandlerRegistration = History.addValueChangeHandler(URLHandler);
		
		registerHandler(getView().getFollowButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if(!getView().getFollowButton().getStyleName().contains("alreadyFollowing")) {
					AppUserRequest appUserRequest = requestFactory.appUserRequest();
					Request<Void> followReq = appUserRequest.followUser(requestedUser);
					followReq.fire(new Receiver<Void>() {
						@Override
						public void onSuccess(Void response) {
							SideNotification.show("Following!");
							refreshFollowButton();
						}
						@Override
						public void onFailure(ServerFailure failure) {
							SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
						}
					});
				}
			}
		}));
	
		refreshUserList();
	}
	
	@Override
	protected void onUnbind() {
		super.onUnbind();
		
		URLHandlerRegistration.removeHandler();
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);
	    
	    if(requestedUser != "") {	 
	    	// See if the request User actually exists
	    	refreshUserFave();   
	    	refreshFollowButton();
	    } else {
	    	// No valid user requested, just show user list	    	
	    	getView().getUserList().setVisible(true);
	    	getView().getUserProfile().setVisible(false);
	    }
	}
	
	private void refreshUserList() {
		getView().getFollowButton().setVisible(false);
		getView().getUserList().setVisible(true);
    	getView().getUserProfile().setVisible(false);
    	getView().getUserFaveDataGrid().setVisible(false);
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<List<AppUserProxy>> userListReq = appUserRequest.getAppUsers();
		userListReq.fire(new Receiver<List<AppUserProxy>>() {
			@Override
			public void onSuccess(List<AppUserProxy> userList) {
				String output = "<ul>";				
				for(AppUserProxy user : userList) {
					output += "<li>";
					// TODO: profile image					
					//output += "<img src='http://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50' />";
					output += "<a href='"+Window.Location.getHref()+";u="+user.getUsername()+"'>";
					output += "<div>"+user.getUsername()+"</div>";					
					output += "</a>";
					output += "</li>";
				}
				output += "</ul>";
				getView().getUserList().setHTML(output);				
			}			
		});
	}
	
	private void refreshUserFave() {
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
	    Request<AppUserProxy> masterFaveListReq = appUserRequest.findAppUser(requestedUser).with("fave100Songs");
	    masterFaveListReq.fire(new Receiver<AppUserProxy>() {
	    	@Override
	    	public void onSuccess(AppUserProxy user) {
	    		if(user != null) {	    				
    				// Hide userlist, and show user profile
    				getView().getUserList().setVisible(false);
    				InlineHTML userProfile = getView().getUserProfile();
    				userProfile.setVisible(true);
    				String output = "";
    				// TODO: profile image
    				//output += "<img src='http://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50' />";
    				output += user.getUsername();    				
    				userProfile.setHTML(output);
    				getView().getFollowButton().setVisible(true);
    				getView().getUserFaveDataGrid().setVisible(true);
    	    		getView().getUserFaveDataGrid().setRowData(user.getFave100Songs());
    	    		getView().getUserFaveDataGrid().resizeFaveList();
    			}
	    	}
	    });
	}
	
	private void refreshFollowButton() {
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<Boolean> checkFollowing = appUserRequest.checkFollowing(requestedUser);
		checkFollowing.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(Boolean following) {
				InlineHTML followButton = getView().getFollowButton();
				if(following) {
					followButton.setHTML("Following");
					followButton.addStyleName("alreadyFollowing");
				} else {
					followButton.setHTML("Follow");
					followButton.removeStyleName("alreadyFollowing");
				}
			}
		});
	}
}
