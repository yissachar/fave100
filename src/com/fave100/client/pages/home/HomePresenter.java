package com.fave100.client.pages.home;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
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
		void updateMasterFaveList(List<SongProxy> faveList);
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> FAVE_FEED_SLOT = new Type<RevealContentHandler<?>>();

	//@Inject FaveFeedPresenter faveFeed;

	@ProxyCodeSplit
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<HomePresenter> {
	}

	@Inject private ApplicationRequestFactory requestFactory;

	@Inject
	public HomePresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void onReveal() {
	    super.onReveal();
	    //setInSlot(FAVE_FEED_SLOT, faveFeed);

	    // Get the master Fave list
	    final Request<List<SongProxy>> masterFaveListReq = requestFactory.faveListRequest().getMasterFaveList();
	    masterFaveListReq.fire(new Receiver<List<SongProxy>>() {
	    	@Override
	    	public void onSuccess(final List<SongProxy> masterFaveList) {
	    		getView().updateMasterFaveList(masterFaveList);
	    	}
	    });
	}
}