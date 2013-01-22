package com.fave100.client.pages.home;

import java.util.List;

import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

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

	@ContentSlot
	public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	//@Inject FaveFeedPresenter faveFeed;
	@Inject LoginWidgetPresenter loginWidget;

	@ProxyCodeSplit
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<HomePresenter> {
	}

	private ApplicationRequestFactory requestFactory;

	@Inject
	public HomePresenter(final ApplicationRequestFactory requestFactory,
			final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
	}

	@Override
	protected void onBind() {
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
	    setInSlot(LOGIN_SLOT, loginWidget);

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