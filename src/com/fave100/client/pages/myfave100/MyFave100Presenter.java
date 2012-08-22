package com.fave100.client.pages.myfave100;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.requestfactory.SongRequest;
import com.fave100.server.domain.FaveList;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
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
		PersonalFaveList getPersonalFaveList();
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
			@Override
			public void onSelection(final SelectionEvent<Suggestion> event) {				
				final Suggestion selectedItem = event.getSelectedItem();
				
				final FaveListRequest faveListRequest = requestFactory.faveListRequest();
				final SongRequest songRequest = faveListRequest.append(requestFactory.songRequest());
				
				// Lookup the SuggestionResult corresponding to the selected String 
				final SuggestionResult faveItemMap = getView().getSongSuggestBox().getFromSuggestionMap(selectedItem.getReplacementString());				
				// and turn it into an SongProxy
				SongProxy songProxy = songRequest.create(SongProxy.class);
				// Need to use AutoBeans to copy, as Request Factory won't allow reuse
	       		final AutoBean<SuggestionResult> autoBean = AutoBeanUtils.getAutoBean(faveItemMap);
				final AutoBean<SongProxy> newBean = AutoBeanUtils.getAutoBean(songProxy);
				AutoBeanCodex.decodeInto(AutoBeanCodex.encode(autoBean), newBean);				
				songProxy = newBean.as();
				// Add the SongProxy as a new FaveItem for the AppUser
				final Request<Void> createReq = faveListRequest.addFaveItemForCurrentUser(FaveList.DEFAULT_HASHTAG, Long.valueOf(faveItemMap.getTrackId()), songProxy);
				createReq.fire(new Receiver<Void>() {
					@Override
					public void onSuccess(final Void response) {
						getView().getPersonalFaveList().refreshList();
					}
					@Override
					public void onFailure(final ServerFailure failure) {
						// TODO: This shouldn't just spit out any error
						SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
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
	    getView().getPersonalFaveList().refreshList();
	}
}