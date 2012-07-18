package com.fave100.client.pages.myfave100;

import java.util.HashMap;
import java.util.List;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.requestfactory.FaveItemRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class MyFave100Presenter extends
		Presenter<MyFave100Presenter.MyView, MyFave100Presenter.MyProxy> {
	
	private HashMap<String, FaveItemProxy> itemSuggestionMap;
	private Timer suggestionsTimer;
	private ApplicationRequestFactory requestFactory;

	public interface MyView extends View {
		SuggestBox getItemInputBox();
		MusicSuggestionOracle getSuggestions();
		DataGrid<FaveItemProxy> getFaveList();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.myfave100)
	public interface MyProxy extends ProxyPlace<MyFave100Presenter> {
	}

	@Inject
	public MyFave100Presenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
		
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
			
		this.getView().getItemInputBox().setLimit(4);
		itemSuggestionMap = new HashMap<String, FaveItemProxy>();		
		
		suggestionsTimer = new Timer() {
			public void run() {
				getAutocompleteList();
			}
		};
		
		DataGrid<FaveItemProxy> faveList = getView().getFaveList();		
		
		SafeHtmlCell linkCell = new SafeHtmlCell();
		Column<FaveItemProxy, SafeHtml> titleColumn = new Column<FaveItemProxy, SafeHtml>(linkCell) {
			@Override
			public SafeHtml getValue(FaveItemProxy faveItem) {
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				if(faveItem.getItemURL() != null && faveItem.getItemURL() != "") {
					sb.appendHtmlConstant("<a href='"+faveItem.getItemURL()+"'>"+faveItem.getTitle()+"</a>");
				} else {
					sb.appendHtmlConstant(faveItem.getTitle());
				}
				return sb.toSafeHtml();
			}
		};
		titleColumn.setCellStyleNames("titleColumn");
		faveList.addColumn(titleColumn, "Title");
		
		TextColumn<FaveItemProxy> artistColumn = new TextColumn<FaveItemProxy>() {
			@Override
			public String getValue(FaveItemProxy object) {
				return object.getArtist();
			}
		};
		artistColumn.setCellStyleNames("artistColumn");
		faveList.addColumn(artistColumn, "Artist");
		
		TextColumn<FaveItemProxy> yearColumn = new TextColumn<FaveItemProxy>() {
			@Override
			public String getValue(FaveItemProxy object) {
				return object.getReleaseYear().toString();
			}
		};
		yearColumn.setCellStyleNames("yearColumn");
		faveList.addColumn(yearColumn, "Year");
		
		ActionCell<FaveItemProxy> deleteButton = new ActionCell<FaveItemProxy>("Delete", new ActionCell.Delegate<FaveItemProxy>() {
		      @Override
		      public void execute(FaveItemProxy contact) {
		    	  //find the item in the data store
		    	  FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
		    	  Request<Void> deleteReq = faveItemRequest.removeFaveItem(contact.getId());
		    	  deleteReq.fire(new Receiver<Void>() {
		    		  @Override
		    		  public void onSuccess(Void response) {
		    			  refreshFaveList();									
		    		  }								
		    	  });
		      }
	    });
		Column<FaveItemProxy, FaveItemProxy> deleteColumn = new Column<FaveItemProxy, FaveItemProxy>(deleteButton) {
			@Override
			public FaveItemProxy getValue(FaveItemProxy object) {
				return object;
			}
		};
		deleteColumn.setCellStyleNames("deleteColumn");
		faveList.addColumn(deleteColumn);
		
		refreshFaveList();
		
		this.getView().getItemInputBox().addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {	
				//To restrict amount of queries, don't bother searching unless more than 200ms have passed
				//since the last keystroke.		
				suggestionsTimer.cancel();
				// don't search if it was just an arrow key being pressed
				if(!KeyCodeEvent.isArrow(event.getNativeKeyCode()) && event.getNativeKeyCode() != KeyCodes.KEY_ENTER)
				{
					suggestionsTimer.schedule(200);
				}
			}
		});
		
		this.getView().getItemInputBox().addSelectionHandler(new SelectionHandler<Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {				
				Suggestion selectedItem = event.getSelectedItem();
				
				FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
				
				// Must copy over properties individually, as cannot edit proxy created by different request context
				FaveItemProxy faveItemMap = itemSuggestionMap.get(selectedItem.getDisplayString());
				FaveItemProxy newFaveItem = faveItemRequest.create(FaveItemProxy.class);
				newFaveItem.setId(faveItemMap.getId());
				newFaveItem.setTitle(faveItemMap.getTitle());
				newFaveItem.setArtist(faveItemMap.getArtist());
				newFaveItem.setReleaseYear(faveItemMap.getReleaseYear());
				newFaveItem.setItemURL(faveItemMap.getItemURL());
				
				// persist
				Request<FaveItemProxy> createReq = faveItemRequest.persist().using(newFaveItem);
				createReq.fire(new Receiver<FaveItemProxy>() {
					@Override
					public void onSuccess(FaveItemProxy response) {
						refreshFaveList();
					}					
				});
				
				//clear the itemInputBox
				getView().getItemInputBox().setValue("");
			}
		});
	}
	
	private void getAutocompleteList() {
		
		SuggestBox itemInputBox = this.getView().getItemInputBox();
		
		String url = "http://itunes.apple.com/search?"+
						"term="+itemInputBox.getValue()+
						"&media=music"+
						"&entity=song"+
						"&attribute=songTerm"+
						"&limit=5";
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, new AsyncCallback<Result>() {	
			
	       	public void onSuccess(Result result) {
	       		//clear the current suggestions)
	       		getView().getSuggestions().clear();
	       		itemSuggestionMap.clear();
	       		
	    	    JsArray<Entry> entries = result.getResults();
	         
	    	    for (int i = 0; i < entries.length(); i++) {
	    	    	Entry entry = entries.get(i);
	    	    	String suggestionEntry = entry.trackName()+"<span class='artistName'>"+entry.artistName()+"</span>";
	    	    	getView().getSuggestions().add(suggestionEntry);
	    		   	//itemSuggestionMap.put(suggestionEntry, new ItemSuggestionObject(entry.trackName(), 
	    		   	//		entry.artistName(), Integer.parseInt(entry.releaseYear()), entry.itemURL()));
	    	    	FaveItemRequest faveRequest = requestFactory.faveItemRequest();
	    	    	
	    	    	FaveItemProxy faveItem = faveRequest.create(FaveItemProxy.class);
	    	    	faveItem.setId(Long.parseLong(entry.id()));
	    	    	faveItem.setTitle(entry.trackName());
	    	    	faveItem.setArtist(entry.artistName());
	    	    	faveItem.setReleaseYear(Integer.parseInt(entry.releaseYear()));
	    	    	faveItem.setItemURL(entry.itemURL());
	    	    	itemSuggestionMap.put(suggestionEntry, faveItem);
	    	    	
	    		   	getView().getItemInputBox().showSuggestionList();		    		   	
	    	    }
	       	}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}
		});		
	}
	
	private void refreshFaveList() {
		//get the data from the datastore
		FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
		
		Request<List<FaveItemProxy>> allFaveItemsReq = faveItemRequest.getAllFaveItemsForUser();
		allFaveItemsReq.fire(new Receiver<List<FaveItemProxy>>() {	
			@Override
			public void onSuccess(List<FaveItemProxy> response) {
				getView().getFaveList().setRowData(response);
			}
		});		
	}
}

/**
 * 
 * @author yissachar.radcliffe
 * Classes to convert JSON return into Java parseable object.
 */
class Entry extends JavaScriptObject {
	protected Entry() {}
	
	public final native String id() /*-{
		return String(this.trackId);
	}-*/;
	
	public final native String itemURL() /*-{
		return this.trackViewUrl;
	}-*/;
	
	public final native String trackName() /*-{
     	return this.trackName;
   	}-*/;
   
	public final native String artistName() /*-{
	   return this.artistName;
	}-*/;
	
	public final native String releaseYear() /*-{
		return this.releaseDate.substring(0, 4);
	}-*/;
 }

class Result extends JavaScriptObject {
   protected Result() {}

   public final native JsArray<Entry> getResults() /*-{
     return this.results;
   }-*/;
 }
