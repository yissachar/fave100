package com.fave100.client.pages.profile;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class ProfilePresenter extends
		BasePresenter<ProfilePresenter.MyView, ProfilePresenter.MyProxy>
		implements ProfileUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<ProfileUiHandlers> {
		void createActionUrl(String url);
		void setEmailValue(String val);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.profile)
	public interface MyProxy extends ProxyPlace<ProfilePresenter> {
	}
	
	private ApplicationRequestFactory requestFactory;

	@Inject
	public ProfilePresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		getView().setUiHandlers(this);
	}
	
	@Override
	public void onBind() {
		super.onBind();
		final Request<String> blobRequest = requestFactory.appUserRequest().createBlobstoreUrl("/avatarUpload");
		blobRequest.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().createActionUrl(url);
			}
		});
	}
	
	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);
		
		final String blobKey = placeRequest.getParameter("blob-key","");
		if(!blobKey.isEmpty()) {
			final Request<Void> avatarReq = requestFactory.appUserRequest().setAvatarForCurrentUser(blobKey);
			avatarReq.fire();
		}
	}
	
	@Override
	public void onReveal() {
		super.onReveal();
		final Request<AppUserProxy> loggedInUserReq = requestFactory.appUserRequest().getLoggedInAppUser();
		loggedInUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy user) {
				getView().setEmailValue(user.getEmail());
			}			
		});		
	}

	@Override
	public void setUserAvatarBlobKey(final String blobKey) {
		requestFactory.appUserRequest().setAvatarForCurrentUser(blobKey).fire();
	}
	
	@Override
	public void setProfileData(final String email) {
		final Request<Void> setProfileDataReq = requestFactory.appUserRequest().setProfileData(email);
		setProfileDataReq.fire();
	}

}

interface ProfileUiHandlers extends UiHandlers {
	void setUserAvatarBlobKey(String blobKey);
	void setProfileData(String email);
}
