package com.fave100.client.pages.home;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * Default page that the user will see.
 * @author yissachar.radcliffe
 *
 */
public class HomePresenter extends
		BasePresenter<HomePresenter.MyView, HomePresenter.MyProxy> {

	public interface MyView extends BaseView {
		//void updateMasterFaveList(List<SongProxy> faveList);
		void addUserThumb(AppUserProxy appUser);
	}

	//@ContentSlot
	//public static final Type<RevealContentHandler<?>> FAVE_FEED_SLOT = new Type<RevealContentHandler<?>>();

	//@Inject FaveFeedPresenter faveFeed;

	@ProxyCodeSplit
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<HomePresenter> {
	}

	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;

	@Inject
	public HomePresenter(final ApplicationRequestFactory requestFactory,
			final EventBus eventBus, final MyView view,
			final MyProxy proxy, final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
	}

	@Override
	protected void onBind() {
		super.onBind();

		// Get a list of 4 random users
	    final Request<List<AppUserProxy>> randomUsers = requestFactory.appUserRequest().getRandomUsers(4);
	    randomUsers.fire(new Receiver<List<AppUserProxy>>() {
	    	@Override
	    	public void onSuccess(final List<AppUserProxy> userList) {
	    		for(final AppUserProxy appUser : userList) {
	    			// Create thumbs to display songs from the user's lists
	    			getView().addUserThumb(appUser);
	    		}
	    	}
		});
	}

	@Override
	protected void onReveal() {
	    super.onReveal();
	    //setInSlot(FAVE_FEED_SLOT, faveFeed);

	    // TODO: Can see the redirect - either use GateKeepers or manual reveal
	    // If a logged in user accesses #Home redirect to their own page
	    final Request<AppUserProxy> getLoggedInUserReq =  requestFactory.appUserRequest().getLoggedInAppUser();
		getLoggedInUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				if(appUser != null) {
					final PlaceRequest placeRequest = new PlaceRequest(NameTokens.users)
													.with("u", appUser.getUsername());
					placeManager.revealPlace(placeRequest);
				}
			}
		});

	    // Get the master Fave list
	    /*final Request<List<SongProxy>> masterFaveListReq = requestFactory.faveListRequest().getMasterFaveList();
	    masterFaveListReq.fire(new Receiver<List<SongProxy>>() {
	    	@Override
	    	public void onSuccess(final List<SongProxy> masterFaveList) {
	    		getView().updateMasterFaveList(masterFaveList);
	    	}
	    });*/
	}
}