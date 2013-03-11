package com.fave100.client.pages.users;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fave100.client.pages.search.SearchPresenter.SearchResultFactory;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.SearchResultProxy;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * A SuggestBox that provides song suggestions
 *
 * @author yissachar.radcliffe
 *
 */
public class SongSuggestBox extends SuggestBox {

	private MusicSuggestionOracle					suggestions;
	private HashMap<String, SongProxy>				itemSuggestionMap;
	private Timer									suggestionsTimer;
	private List<AsyncCallback<JavaScriptObject>>	requests;

	public SongSuggestBox(final MusicSuggestionOracle suggestions) {
		super(suggestions);
		this.suggestions = suggestions;
		itemSuggestionMap = new HashMap<String, SongProxy>();
		requests = new LinkedList<AsyncCallback<JavaScriptObject>>();

		suggestionsTimer = new Timer() {
			@Override
			public void run() {
				getAutocompleteList();
			}
		};

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				// To restrict amount of queries, don't bother searching unless
				// more than 200ms have passed since the last keystroke.
				suggestionsTimer.cancel();
				// Don't search if it was just an arrow key being pressed
				if (!KeyCodeEvent.isArrow(event.getNativeKeyCode())
						&& event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
					event.preventDefault();
					// Min delay 200ms, Max 1500
					int delay = 200 + (20 * getText().length());
					if (delay > 1500)
						delay = 1500;
					suggestionsTimer.schedule(delay);
				}
			}
		});
	}

	private void getAutocompleteList() {
		// Not enough letters to try an autocomplete, show nothing
		if(this.getValue().isEmpty() || this.getValue().length() <= 2) {
			suggestions.clearSuggestions();
			showSuggestionList();
			return;
		}

		final String url = Constants.SEARCH_URL+"searchTerm="+this.getValue()+"&limit=5";
		final AsyncCallback<JavaScriptObject> autocompleteReq = new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(final Throwable caught) {
				requests.remove(this);
			}

			@Override
			public void onSuccess(final JavaScriptObject jsObject) {
				if(requests.indexOf(this) != requests.size()-1
					|| requests.indexOf(this) == -1) {
					requests.remove(this);
					return;
				}

				requests.clear();

				// Clear the current suggestions
				suggestions.clearSuggestions();
				itemSuggestionMap.clear();

				final JSONObject obj = new JSONObject(jsObject);
				final SearchResultFactory factory = GWT.create(SearchResultFactory.class);
				final AutoBean<SearchResultProxy> autoBean = AutoBeanCodex.decode(factory, SearchResultProxy.class, obj.toString());
				final List<SongProxy> results = autoBean.as().getResults();

				// Get the new suggestions from the autocomplete API
				for (int i = 0; i < results.size(); i++) {
					final SongProxy entry = results.get(i);

					final String suggestionEntry = ""
							+ entry.getSong()
							+ "</br><span class='artistName'>"
							+ entry.getArtist() + "</span>";
					String mapEntry = entry.getSong();
					// Use white space to sneak in duplicate song titles into
					// the hashmap
					while (itemSuggestionMap.get(mapEntry) != null) {
						mapEntry += " ";
					}
					itemSuggestionMap.put(mapEntry, entry);

					suggestions.addSuggestion(mapEntry, suggestionEntry);
				}
				showSuggestionList();

			}
		};
		requests.add(autocompleteReq);
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, autocompleteReq);

	}

	// Returns MusicbrainzResults mapped from the display string passed in
	public SongProxy getFromSuggestionMap(final String key) {
		return itemSuggestionMap.get(key);
	}
}