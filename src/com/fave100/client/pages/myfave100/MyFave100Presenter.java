package com.fave100.client.pages.myfave100;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.Selector;
import com.google.gwt.query.client.Selectors;
import static com.google.gwt.query.client.GQuery.*;
import static com.google.gwt.query.client.css.CSS.*;


import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.requestfactory.FaveItemRequest;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.view.client.CellPreviewEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class MyFave100Presenter extends
		Presenter<MyFave100Presenter.MyView, MyFave100Presenter.MyProxy> {	
		
	private ApplicationRequestFactory requestFactory;
	private AppUserProxy appUser;	
	private EventBus eventBus;
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject TopBarPresenter topBar;

	public interface MyView extends View {
		SongSuggestBox getItemInputBox();
		FaveDataGrid getFaveList();
		Button getRankButton();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.myfave100)
	public interface MyProxy extends ProxyPlace<MyFave100Presenter> {
	}

	@Inject
	public MyFave100Presenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
		
		this.eventBus = eventBus;
		requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(eventBus);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();		
		
		// Add Fave Item on selection event
		registerHandler(getView().getItemInputBox().addSelectionHandler(new SelectionHandler<Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {				
				Suggestion selectedItem = event.getSelectedItem();
				
				FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
				
				// Must copy over properties individually, as cannot edit proxy created by different request context
				FaveItemProxy faveItemMap = getView().getItemInputBox().getFromSuggestionMap(selectedItem.getDisplayString());
				FaveItemProxy newFaveItem = faveItemRequest.create(FaveItemProxy.class);
				newFaveItem.setId(faveItemMap.getId());
				newFaveItem.setAppUser(appUser.getId());
				newFaveItem.setTitle(faveItemMap.getTitle());
				newFaveItem.setArtist(faveItemMap.getArtist());
				newFaveItem.setReleaseYear(faveItemMap.getReleaseYear());
				newFaveItem.setItemURL(faveItemMap.getItemURL());
				
				// persist
				Request<FaveItemProxy> createReq = faveItemRequest.persist().using(newFaveItem);
				createReq.fire(new Receiver<FaveItemProxy>() {
					@Override
					public void onSuccess(FaveItemProxy response) {
						getView().getFaveList().refreshFaveList();
					}					
				});
				
				//clear the itemInputBox
				getView().getItemInputBox().setValue("");
			}
		}));
		
		// On rank button click, allow items to be reranked
		registerHandler(getView().getRankButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getView().getFaveList().startRanking();
			}
		}));
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);
	    AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.findLoggedInAppUser();
		getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(AppUserProxy response) {
				appUser = response;
				getView().getFaveList().setAppUser(appUser);
				//getView().getFaveList().setRequestFactory(requestFactory);
				getView().getFaveList().refreshFaveList();
			}
		});		
	}
}