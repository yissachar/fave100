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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class MyFave100Presenter extends
		Presenter<MyFave100Presenter.MyView, MyFave100Presenter.MyProxy> {
	
	private HashMap<String, String> songArtistMap;
	private Timer suggestionsTimer;
	private ApplicationRequestFactory requestFactory;

	public interface MyView extends View {
		SuggestBox getItemInputBox();
		MusicSuggestionOracle getSuggestions();
		Grid getFaveList();
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
		songArtistMap = new HashMap<String, String>();		
		
		suggestionsTimer = new Timer() {
			public void run() {
				getAutocompleteList();
			}
		};
		
		refreshFaveList();
		
		this.getView().getItemInputBox().addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {	
				//To restrict amount of queries, don't bother searching unless more than 200ms have passed
				//since the last keystroke.		
				suggestionsTimer.cancel();
				// To restrict the amount of queries, don't search unless more than 1 character
				// And don't search if it was just an arrow key being pressed
				if(getView().getItemInputBox().getValue().length() > 1 && !KeyCodeEvent.isArrow(event.getNativeKeyCode())
						&& event.getNativeKeyCode() != KeyCodes.KEY_ENTER)
				{
					suggestionsTimer.schedule(200);
				}
			}
		});
		
		this.getView().getItemInputBox().addSelectionHandler(new SelectionHandler<Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {				
				Suggestion selectedItem = event.getSelectedItem();
				//selectedSuggestion.add(new Label("You selected: "+selectedItem.getReplacementString()));
				//selectedSuggestion.add(new Label("Artist: "+songArtistMap.get(selectedItem.getDisplayString())));
				
				FaveItemRequest faveItemRequest = requestFactory.faveItemRequest();
				
				//create a new FaveItem
				FaveItemProxy newFaveItem = faveItemRequest.create(FaveItemProxy.class);
				newFaveItem.setTitle(selectedItem.getReplacementString());
				
				//and persist
				Request<FaveItemProxy> createReq = faveItemRequest.persist().using(newFaveItem);
				createReq.fire();
				
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
	       		songArtistMap.clear();
	       		
	    	    JsArray<Entry> entries = result.getResults();
	         
	    	    for (int i = 0; i < entries.length(); i++) {
	    	    	Entry entry = entries.get(i);
	    	    	String suggestionEntry = entry.trackName()+"<span class='artistName'>"+entry.artistName()+"</span>";
	    	    	getView().getSuggestions().add(suggestionEntry);
	    		   	songArtistMap.put(suggestionEntry, entry.artistName());
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
				getView().getFaveList().resize(5, 5);
				getView().getFaveList().setText(0, 0, "Hi from Fave100!");
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

   public final native String trackName() /*-{
     return this.trackName;
   }-*/;
   
   public final native String artistName() /*-{
   return this.artistName;
 }-*/;
   
 }

class Result extends JavaScriptObject {
   protected Result() {}

   public final native JsArray<Entry> getResults() /*-{
     return this.results;
   }-*/;
 }
