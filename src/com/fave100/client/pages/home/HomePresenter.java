package com.fave100.client.pages.home;

import java.util.ArrayList;
import java.util.List;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.pagefragments.TopBarPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListItem;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.widgets.FaveDataGrid;
import com.fave100.client.widgets.FaveItemCell;
import com.fave100.client.widgets.FaveListBase;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

/**
 * Default page that the user will see.
 * @author yissachar.radcliffe
 *
 */
public class HomePresenter extends
		Presenter<HomePresenter.MyView, HomePresenter.MyProxy> {

	public interface MyView extends View {
		FaveDataGrid getMasterFaveDataGrid();
		FaveListBase getMasterFaveList();
	}
	
	@ContentSlot
	public static final Type<RevealContentHandler<?>> TOP_BAR_SLOT = new Type<RevealContentHandler<?>>();
	
	@ProxyCodeSplit
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<HomePresenter> {
	}
	
	@Inject private TopBarPresenter topBar;	
	@Inject private ApplicationRequestFactory requestFactory;

	@Inject
	public HomePresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}
	
	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(TOP_BAR_SLOT, topBar);
	    
	    // Get the master Fave list
	    AppUserRequest appUserRequest = requestFactory.appUserRequest();
	    Request<List<SongProxy>> masterFaveListReq = appUserRequest.getMasterFaveList();
	    masterFaveListReq.fire(new Receiver<List<SongProxy>>() {
	    	@Override
	    	public void onSuccess(List<SongProxy> masterFaveList) {
	    		getView().getMasterFaveDataGrid().setRowData(masterFaveList);
	    		getView().getMasterFaveDataGrid().resizeFaveList();
	    		// Test
	    		CompositeCell<FaveListItem> cell = new CompositeCell<FaveListItem>(cells);
	    		getView().getMasterFaveList().setRowData(masterFaveList);
	    	}
	    });	    
	}
}