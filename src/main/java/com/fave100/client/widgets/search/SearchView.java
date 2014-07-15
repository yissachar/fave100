package com.fave100.client.widgets.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.client.CurrentUser;
import com.fave100.client.Utils;
import com.fave100.client.entities.ItunesSearchResult;
import com.fave100.client.entities.SongDto;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.FaveTextBox;
import com.fave100.client.widgets.Icon;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SearchView extends ViewWithUiHandlers<SearchUiHandlers> implements SearchPresenter.MyView {

	public interface Binder extends UiBinder<HTMLPanel, SearchView> {
	}

	interface SearchStyle extends GlobalStyle {
		String suggestionPanel();

		String song();

		String artist();

		String selected();

		String darkText();
	}

	@UiField SearchStyle style;
	@UiField Panel container;
	@UiField Panel currentSearchTypeContainer;
	@UiField Label currentSearchType;
	@UiField FaveTextBox searchBox;
	@UiField Icon searchIndicator;
	@UiField Image searchLoadingIndicatorWhite;
	@UiField Image searchLoadingIndicatorBlack;
	@UiField Panel searchTypeSelector;
	@UiField Label searchSongsOption;
	@UiField Label searchUsersOption;
	@UiField Label searchListsOption;
	@UiField Panel searchResults;
	@UiField ScrollPanel searchSuggestionsContainer;
	@UiField FlowPanel searchSuggestions;
	@UiField Label loadedAllLabel;
	@UiField Panel loadMoreLoadingIndicator;
	private Label _noResultsLabel = new Label("No results found");
	private Timer searchTimer;
	private HandlerRegistration rootClickHandler = null;
	private boolean _loadedAllResults;
	private boolean _darkText;
	private Map<SongDto, Image> _albumImages = new HashMap<>();
	@Inject CurrentUser _currentUser;

	private MouseOutHandler suggestionMouseOutHandler = new MouseOutHandler() {

		@Override
		public void onMouseOut(MouseOutEvent event) {
			getUiHandlers().deselect();
			refreshSelection();
		}
	};

	private ClickHandler suggestionsClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			getUiHandlers().selectSuggestion();
			searchBox.setFocus(true);
		}
	};

	@Inject
	SearchView(Binder binder) {
		initWidget(binder.createAndBindUi(this));
		clearSearchResultsNoUiHandlers();
		final Widget thisWidget = this.asWidget();

		ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element target = Element.as(event.getNativeEvent().getEventTarget());
				if (!Utils.widgetContainsElement(searchBox, target)
						&& !Utils.widgetContainsElement(loadedAllLabel, target)) {
					clearSearchResults();
				}

				if (!Utils.widgetContainsElement(currentSearchTypeContainer, target)) {
					searchTypeSelector.setVisible(false);
				}
			}
		};
		rootClickHandler = RootPanel.get().addDomHandler(clickHandler, ClickEvent.getType());
		searchTypeSelector.setVisible(false);
	}

	@UiHandler("currentSearchTypeContainer")
	void onCurrentSearchTypeClick(ClickEvent event) {
		searchTypeSelector.setVisible(!searchTypeSelector.isVisible());
	}

	@UiHandler("searchSongsOption")
	void onSearchSongsOptionClick(ClickEvent event) {
		getUiHandlers().setSearchType(SearchType.SONGS);
	}

	@UiHandler("searchUsersOption")
	void onSearchUsersOptionClick(ClickEvent event) {
		getUiHandlers().setSearchType(SearchType.USERS);
	}

	@UiHandler("searchListsOption")
	void onSearchListsOptionClick(ClickEvent event) {
		getUiHandlers().setSearchType(SearchType.LISTS);
	}

	private void showSearchTypeOptions(Label label) {
		searchSongsOption.setVisible(true);
		searchUsersOption.setVisible(true);
		searchListsOption.setVisible(true);
		label.setVisible(false);
	}

	@UiHandler("searchBox")
	void onKeyUp(final KeyUpEvent event) {

		if (KeyUpEvent.isArrow(event.getNativeKeyCode())) {

			if (event.isDownArrow()) {
				getUiHandlers().incrementSelection();
			}
			else if (event.isUpArrow()) {
				getUiHandlers().decrementSelection();
			}
		}
		else if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
			getUiHandlers().selectSuggestion();
			clearSearchResults();
		}
		else if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
			clearSearchResults();
		}
		else {
			// Otherwise search for song
			final String searchTerm = searchBox.getText().trim();

			if (searchTerm.isEmpty()) {
				clearSearchResults();
				return;
			}

			if (searchTimer != null) {
				searchTimer.cancel();
			}

			searchTimer = new Timer() {
				@Override
				public void run() {
					if (searchTerm.trim().length() <= 2 && currentSearchType.getText().equals("Songs")) {
						return;
					}

					_loadedAllResults = false;
					searchIndicator.setVisible(false);
					if (_darkText) {
						searchLoadingIndicatorBlack.setVisible(true);
					}
					else {
						searchLoadingIndicatorWhite.setVisible(true);
					}
					getUiHandlers().getSearchResults(searchTerm);
				}
			};
			searchTimer.schedule(50);
		}

		refreshSelection();
	}

	@UiHandler("searchSuggestionsContainer")
	void onSearchScroll(ScrollEvent event) {
		Element elem = searchSuggestionsContainer.getElement();
		if (elem.getScrollTop() + elem.getOffsetHeight() == elem.getScrollHeight()) {
			loadMore();
		}
	}

	private void loadMore() {
		if (_loadedAllResults)
			return;

		getUiHandlers().loadMore();
		loadMoreLoadingIndicator.setVisible(true);
	}

	// UI handlers are not available in constructor so call without
	private void clearSearchResultsNoUiHandlers() {
		searchBox.setText("");
		searchSuggestions.clear();
		searchResults.setVisible(false);
		searchIndicator.setVisible(true);
		searchLoadingIndicatorWhite.setVisible(false);
		searchLoadingIndicatorBlack.setVisible(false);
		loadedAllLabel.setVisible(false);
		loadMoreLoadingIndicator.setVisible(false);
		resetHeight();
	}

	private void clearSearchResults() {
		clearSearchResultsNoUiHandlers();
		getUiHandlers().clearSearchResults();
	}

	private void refreshSelection() {
		int selection = getUiHandlers().getSelection();
		int widgetCount = searchSuggestions.getWidgetCount();

		for (int i = 0; i < widgetCount; i++) {
			searchSuggestions.getWidget(i).getElement().removeClassName(style.selected());
		}

		if (selection >= 0 && widgetCount > 0) {
			Widget selectedWidget = searchSuggestions.getWidget(selection);
			selectedWidget.addStyleName(style.selected());
			selectedWidget.getElement().scrollIntoView();
			if (_loadedAllResults && searchSuggestions.getWidgetIndex(selectedWidget) == searchSuggestions.getWidgetCount() - 1) {
				loadedAllLabel.getElement().scrollIntoView();
			}
		}
	}

	@Override
	public void setSelectedSearchType(SearchType searchType) {
		searchTypeSelector.setVisible(false);

		String searchText = "";
		switch (searchType) {
			case SONGS:
				showSearchTypeOptions(searchSongsOption);
				searchText = "Search songs";
				break;
			case USERS:
				showSearchTypeOptions(searchUsersOption);
				searchText = "Search users";
				break;
			case LISTS:
				showSearchTypeOptions(searchListsOption);
				searchText = "Search lists";

			default:
				break;
		}

		currentSearchType.setText(searchText);
	}

	@Override
	public void setSongSuggestions(List<SongDto> songs, List<ItunesSearchResult> itunesSearchResults, boolean loadMore) {
		setupSearchContainer(loadMore);
		_albumImages.clear();

		for (SongDto song : songs) {
			String albumArtUrl = "";

			final FocusPanel eventCatcherPanel = new FocusPanel();
			FlowPanel panel = new FlowPanel();
			panel.addStyleName(style.suggestionPanel());

			Image albumArt = new Image("http://a1.mzstatic.com/us/r30/Music6/v4/9c/16/f4/9c16f4b6-2b52-3fda-3017-b647200d1a32/859706808522_cover.60x60-50.jpg");
			panel.add(albumArt);
			_albumImages.put(song, albumArt);

			FlowPanel detailsPanel = new FlowPanel();

			Label songTitle = new Label(song.getSong());
			songTitle.addStyleName(style.song());
			detailsPanel.add(songTitle);

			Label artist = new Label(song.getArtist());
			artist.addStyleName(style.artist());
			detailsPanel.add(artist);

			panel.add(detailsPanel);
			eventCatcherPanel.add(panel);

			eventCatcherPanel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					getUiHandlers().setSelection(searchSuggestions.getWidgetIndex(eventCatcherPanel));
					refreshSelection();
				}
			});

			eventCatcherPanel.addMouseOutHandler(suggestionMouseOutHandler);
			eventCatcherPanel.addClickHandler(suggestionsClickHandler);

			searchSuggestions.add(eventCatcherPanel);
		}

		updateAlbumArt(itunesSearchResults);

		if (songs.size() == 0 && !loadMore) {
			searchSuggestions.add(_noResultsLabel);
		}

		if (!loadMore) {
			loadMore();
		}

		showSuggestions(songs.size(), true);
	}

	@Override
	public void setStringSuggestions(List<String> suggestions, boolean loadMore) {
		setupSearchContainer(loadMore);

		for (String suggestion : suggestions) {
			final Label suggestionLabel = new Label(suggestion);
			suggestionLabel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					getUiHandlers().setSelection(searchSuggestions.getWidgetIndex(suggestionLabel));
					refreshSelection();
				}
			});

			suggestionLabel.addMouseOutHandler(suggestionMouseOutHandler);
			suggestionLabel.addClickHandler(suggestionsClickHandler);

			searchSuggestions.add(suggestionLabel);
		}

		if (suggestions.size() == 0 && !loadMore) {
			searchSuggestions.add(_noResultsLabel);
		}

		if (!loadMore) {
			loadMore();
		}

		showSuggestions(suggestions.size(), true);
	}

	@Override
	public void setSongTypeSuggestions(List<SearchType> suggestions) {
		setupSearchContainer(false);

		for (SearchType suggestion : suggestions) {
			String prompt = "";
			switch (suggestion) {
				case SONGS:
					prompt = "Search songs";
					break;
				case USERS:
					prompt = "Search users";
					break;
				case LISTS:
					prompt = "Search lists";

				default:
					break;
			}
			final Label suggestionLabel = new Label(prompt);
			suggestionLabel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					getUiHandlers().setSelection(searchSuggestions.getWidgetIndex(suggestionLabel));
					refreshSelection();
				}
			});

			suggestionLabel.addMouseOutHandler(suggestionMouseOutHandler);
			suggestionLabel.addClickHandler(suggestionsClickHandler);

			searchSuggestions.add(suggestionLabel);
		}

		showSuggestions(suggestions.size(), false);
	}

	private void setupSearchContainer(boolean loadMore) {
		if (!loadMore) {
			searchSuggestions.clear();
			searchSuggestionsContainer.getElement().getStyle().setProperty("height", "initial");
		}
	}

	private void showSuggestions(int resultsSize, boolean showLoadMore) {
		if (!showLoadMore) {
			resetHeight();
		}

		loadedAllLabel.setVisible(false);
		if (resultsSize != SearchPresenter.SELECTIONS_PER_PAGE) {
			loadedAllLabel.setVisible(showLoadMore && getUiHandlers().getTotalResults() > 0);
			loadedAllLabel.setText("Loaded " + getUiHandlers().getTotalResults() + " result" + (getUiHandlers().getTotalResults() > 1 ? "s" : ""));
			_loadedAllResults = true;
		}
		else {
			_loadedAllResults = false;
		}

		searchIndicator.setVisible(true);
		searchLoadingIndicatorWhite.setVisible(false);
		searchLoadingIndicatorBlack.setVisible(false);
		searchResults.setVisible(true);
		loadMoreLoadingIndicator.setVisible(false);
		refreshSelection();

		if (searchSuggestions.getWidgetCount() == SearchPresenter.SELECTIONS_PER_PAGE) {
			int totalHeight = 0;
			for (int i = 0; i < SearchPresenter.SELECTIONS_PER_PAGE; i++) {
				totalHeight += searchSuggestions.getWidget(i).getOffsetHeight();
			}
			searchSuggestionsContainer.getElement().getStyle().setHeight(totalHeight, Unit.PX);
		}
	}

	private void resetHeight() {
		searchSuggestions.getElement().setScrollTop(0);
		searchSuggestionsContainer.getElement().getStyle().setProperty("height", "initial");
	}

	@Override
	public void setSingleSearch(boolean singleSearch) {
		currentSearchTypeContainer.setVisible(!singleSearch);
	}

	@Override
	public void focus() {
		searchBox.setFocus(true);
	}

	@Override
	public void setDarkText(boolean darkText) {
		_darkText = darkText;
		if (darkText) {
			container.addStyleName(style.darkText());
		}
		else {
			container.removeStyleName(style.darkText());
		}
	}

	@Override
	public void updateAlbumArt(List<ItunesSearchResult> itunesSearchResults) {

		if (itunesSearchResults == null)
			return;

		for (Map.Entry<SongDto, Image> entry : _albumImages.entrySet()) {
			for (ItunesSearchResult itunesSearchResult : itunesSearchResults) {
				if (itunesSearchResult.getTrackName().equals(entry.getKey().getSong())
						&& itunesSearchResult.getArtistName().equals(entry.getKey().getArtist())) {
					entry.getValue().setUrl(itunesSearchResult.getArtworkUrl60());
				}
			}
		}
	}
}
