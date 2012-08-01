package com.fave100.client.pages.myfave100;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.requestfactory.SongRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

/**
 * Page for user to modify their own Fave100 list.
 * @author yissachar.radcliffe
 *
 */
public class MyFave100Presenter extends
		Presenter<MyFave100Presenter.MyView, MyFave100Presenter.MyProxy> {	
		
	private ApplicationRequestFactory requestFactory;
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject TopBarPresenter topBar;

	public interface MyView extends View {
		SongSuggestBox getSongSuggestBox();
		UserFaveDataGrid getFaveList();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.myfave100)
	public interface MyProxy extends ProxyPlace<MyFave100Presenter> {
	}

	@Inject
	public MyFave100Presenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		
		this.requestFactory = requestFactory;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		// Add Fave Item on selection event
		registerHandler(getView().getSongSuggestBox().addSelectionHandler(new SelectionHandler<Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {				
				Suggestion selectedItem = event.getSelectedItem();
				
				AppUserRequest appUserRequest = requestFactory.appUserRequest();
				SongRequest songRequest = appUserRequest.append(requestFactory.songRequest());
				
				// Lookup the SuggestionResult corresponding to the selected String 
				SuggestionResult faveItemMap = getView().getSongSuggestBox().getFromSuggestionMap(selectedItem.getDisplayString());				
				// and turn it into an SongProxy
				SongProxy songProxy = songRequest.create(SongProxy.class);
	       		AutoBean<SuggestionResult> autoBean = AutoBeanUtils.getAutoBean(faveItemMap);
				AutoBean<SongProxy> newBean = AutoBeanUtils.getAutoBean(songProxy);
				AutoBeanCodex.decodeInto(AutoBeanCodex.encode(autoBean), newBean);				
				songProxy = newBean.as();
				// Add the SongProxy as a new FaveItem for the AppUser
				Request<Void> createReq = appUserRequest.addFaveItemForCurrentUser(Long.valueOf(faveItemMap.getTrackId()), songProxy);
				createReq.fire(new Receiver<Void>() {
					@Override
					public void onSuccess(Void response) {
						getView().getFaveList().refreshFaveList();
					}
				});
				
				//clear the SuggestBox for the next entry
				getView().getSongSuggestBox().setValue("");
			}
		}));		
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);
	    getView().getFaveList().refreshFaveList();	
	}
}