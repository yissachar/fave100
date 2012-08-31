package com.fave100.client.pages.profile;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class ProfilePresenter extends
		BasePresenter<ProfilePresenter.MyView, ProfilePresenter.MyProxy> {

	public interface MyView extends BaseView {
		void createActionUrl(String url);
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
			requestFactory.appUserRequest().setAvatarForCurrentUser(blobKey).fire();
		}
	}

}
