package com.fave100.client.pages.userlist;

import java.util.List;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHTML;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class UserlistPresenter extends
		Presenter<UserlistPresenter.MyView, UserlistPresenter.MyProxy> {

	public interface MyView extends View {
		InlineHTML getUserList();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.userlist)
	public interface MyProxy extends ProxyPlace<UserlistPresenter> {
	}
	
	@ContentSlot public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject TopBarPresenter topBar;
	@Inject ApplicationRequestFactory requestFactory;

	@Inject
	public UserlistPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);
	    
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
					output += "<img src='"+user.getAvatar()+"'/>";
					output += "<a href='"+Window.Location.getPath()+Window.Location.getQueryString()+"#"+NameTokens.users+";u="+user.getUsername()+"'>";
					output += "<div>"+user.getUsername()+"</div>";					
					output += "</a>";
					output += "</li>";
				}
				output += "</ul>";
				getView().getUserList().setHTML(output);				
			}			
		});
	}
}
