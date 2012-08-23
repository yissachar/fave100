package com.fave100.client.pages.userlist;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class UserlistPresenter extends
		BasePresenter<UserlistPresenter.MyView, UserlistPresenter.MyProxy> {

	public interface MyView extends BaseView {
		InlineHTML getUserList();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.userlist)
	public interface MyProxy extends ProxyPlace<UserlistPresenter> {
	}
	
	@Inject ApplicationRequestFactory requestFactory;

	@Inject
	public UserlistPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    
	    final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<List<AppUserProxy>> userListReq = appUserRequest.getAppUsers();
		userListReq.fire(new Receiver<List<AppUserProxy>>() {
			@Override
			public void onSuccess(final List<AppUserProxy> userList) {
				// TODO: This needs to be safeHTML				
				String output = "<ul>";				
				for(final AppUserProxy user : userList) {
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
