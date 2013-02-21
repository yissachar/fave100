package com.fave100.client.pages.users;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * A SuggestBox that provides song suggestions
 *
 * @author yissachar.radcliffe
 *
 */
public class SongSuggestBox extends SuggestBox {

	private MusicSuggestionOracle			suggestions;
	private HashMap<String, SongProxy>		itemSuggestionMap;
	private Timer							suggestionsTimer;
	private ApplicationRequestFactory		requestFactory;
	private List<Request<List<SongProxy>>>	requests;

	public SongSuggestBox(final MusicSuggestionOracle suggestions,
			final ApplicationRequestFactory requestFactory) {
		super(suggestions);
		this.suggestions = suggestions;
		this.requestFactory = requestFactory;
		itemSuggestionMap = new HashMap<String, SongProxy>();
		requests = new LinkedList<Request<List<SongProxy>>>();

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
		if(this.getValue().isEmpty() || this.getValue().length() <= 2) return;

		final Request<List<SongProxy>> autocompleteReq = requestFactory
				.songRequest().getAutocomplete(this.getValue());
		// Add the request to the list of running requests
		requests.add(autocompleteReq);
		autocompleteReq.fire(new Receiver<List<SongProxy>>() {
			@Override
			public void onSuccess(final List<SongProxy> results) {
				// If the completed request is not the latest request, ignore it
				if(requests.indexOf(autocompleteReq) != requests.size()-1
					|| requests.indexOf(autocompleteReq) == -1) {
					requests.remove(autocompleteReq);
					return;
				}

				requests.clear();

				// Clear the current suggestions
				suggestions.clearSuggestions();
				itemSuggestionMap.clear();

				// Get the new suggestions from the autocomplete API
				for (int i = 0; i < results.size(); i++) {
					final SongProxy entry = results.get(i);

					final String suggestionEntry = ""
							+ entry.getName()
							+ "</br><span class='artistName'>"
							+ entry.getArtist() + "</span>";
					String mapEntry = entry.getName();
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

			@Override
			public void onFailure(final ServerFailure failure) {
				requests.remove(autocompleteReq);
			}
		});
	}

	// Returns MusicbrainzResults mapped from the display string passed in
	public SongProxy getFromSuggestionMap(final String key) {
		return itemSuggestionMap.get(key);
	}
}