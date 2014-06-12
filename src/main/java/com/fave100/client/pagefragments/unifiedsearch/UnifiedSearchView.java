package com.fave100.client.pagefragments.unifiedsearch;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.Utils;
import com.fave100.client.entities.SongDto;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.Icon;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class UnifiedSearchView extends ViewWithUiHandlers<UnifiedSearchUiHandlers> implements UnifiedSearchPresenter.MyView {

	public interface Binder extends UiBinder<HTMLPanel, UnifiedSearchView> {
	}

	interface UnifiedSearchStyle extends GlobalStyle {
		String song();

		String artist();

		String selected();
	}

	@UiField UnifiedSearchStyle style;
	@UiField Label currentSearchType;
	@UiField Panel currentSearchTypeContainer;
	@UiField Icon removeSearchTypeButton;
	@UiField Panel searchContainer;
	@UiField TextBox searchBox;
	@UiField Icon searchIndicator;
	@UiField Panel searchLoadingIndicator;
	@UiField Panel searchResults;
	@UiField FlowPanel searchSuggestionsContainer;
	@UiField Label loadMoreButton;
	@UiField Panel modePanel;
	@UiField Label addModeOption;
	@UiField Label browseModeOption;
	@UiField Label helpText;
	private Label _noResultsLabel = new Label("No results found");
	private Timer searchTimer;
	private HandlerRegistration rootClickHandler = null;
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
	UnifiedSearchView(Binder binder) {
		initWidget(binder.createAndBindUi(this));
		searchResults.setVisible(false);
		searchLoadingIndicator.setVisible(false);
		modePanel.setVisible(false);
		loadMoreButton.setVisible(false);

		ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element target = Element.as(event.getNativeEvent().getEventTarget());
				if (!Utils.widgetContainsElement(searchBox, target)
						&& !Utils.widgetContainsElement(currentSearchTypeContainer, target)
						&& !Utils.widgetContainsElement(modePanel, target)
						&& !Utils.widgetContainsElement(loadMoreButton, target)) {
					clearSearchResults();
				}
			}
		};
		rootClickHandler = RootPanel.get().addDomHandler(clickHandler, ClickEvent.getType());
	}

	@UiHandler("removeSearchTypeButton")
	void onRemoveSearchTypeClick(ClickEvent event) {
		getSongTypeSuggestions();
	}

	@UiHandler("searchBox")
	void onSearchFocus(FocusEvent event) {
		searchContainer.addStyleName(style.selected());
		if (!currentSearchTypeContainer.isVisible()) {
			searchResults.setVisible(true);
			getSongTypeSuggestions();
		}
	}

	@UiHandler("searchBox")
	void onSearchBlur(BlurEvent event) {
		searchContainer.removeStyleName(style.selected());
	}

	@UiHandler("searchBox")
	void onKeyDown(final KeyDownEvent event) {
		if (!currentSearchTypeContainer.isVisible()) {
			event.preventDefault();
			event.stopPropagation();
		}
		else if (KeyCodes.KEY_BACKSPACE == event.getNativeKeyCode() && searchBox.getText().equals("")) {
			getSongTypeSuggestions();
		}
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
		else if (currentSearchTypeContainer.isVisible()) {
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

					searchIndicator.setVisible(false);
					searchLoadingIndicator.setVisible(true);
					getUiHandlers().getSearchResults(searchTerm);
				}
			};
			searchTimer.schedule(50);
		}

		refreshSelection();
	}

	@UiHandler("loadMoreButton")
	void onLoadMoreButtonClick(ClickEvent event) {
		getUiHandlers().loadMore();
	}

	@UiHandler("addModeOption")
	void onAddModeOptionClick(ClickEvent event) {
		addModeOption.addStyleName(style.selected());
		browseModeOption.removeStyleName(style.selected());
		getUiHandlers().setAddMode(true);
		refreshHelpText();
	}

	@UiHandler("browseModeOption")
	void onBrowseModeOptionClick(ClickEvent event) {
		browseModeOption.addStyleName(style.selected());
		addModeOption.removeStyleName(style.selected());
		getUiHandlers().setAddMode(false);
		refreshHelpText();
	}

	private void refreshHelpText() {
		helpText.setText(getUiHandlers().getHelpText());
	}

	private void clearSearchResults() {
		modePanel.setVisible(false);
		searchBox.setText("");
		searchSuggestionsContainer.clear();
		searchResults.setVisible(false);
		searchIndicator.setVisible(true);
		searchLoadingIndicator.setVisible(false);
		loadMoreButton.setVisible(false);
		getUiHandlers().clearSearchResults();
	}

	private void refreshSelection() {
		int selection = getUiHandlers().getSelection();
		int widgetCount = searchSuggestionsContainer.getWidgetCount();

		for (int i = 0; i < widgetCount; i++) {
			searchSuggestionsContainer.getWidget(i).getElement().removeClassName(style.selected());
		}

		if (selection >= 0 && widgetCount > 0) {
			Widget selectedWidget = searchSuggestionsContainer.getWidget(selection);
			selectedWidget.addStyleName(style.selected());
			selectedWidget.getElement().scrollIntoView();
		}
	}

	@Override
	public void setSelectedSearchType(SearchType searchType) {
		if (searchType == null)
			return;

		String searchText = "";
		switch (searchType) {
			case SONGS:
				searchText = "Songs";
				break;
			case USERS:
				searchText = "Users";
				break;
			case LISTS:
				searchText = "Lists";

			default:
				break;
		}

		currentSearchType.setText(searchText);
		currentSearchTypeContainer.setVisible(true);
	}

	@Override
	public void setSongSuggestions(List<SongDto> songs, boolean loadMore) {
		setupSearchContainer(loadMore);

		for (SongDto song : songs) {
			final FocusPanel eventCatcherPanel = new FocusPanel();
			FlowPanel panel = new FlowPanel();
			Label songTitle = new Label(song.getSong());
			songTitle.addStyleName(style.song());
			Label artist = new Label(song.getArtist());
			artist.addStyleName(style.artist());
			panel.add(songTitle);
			panel.add(artist);
			eventCatcherPanel.add(panel);

			eventCatcherPanel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					getUiHandlers().setSelection(searchSuggestionsContainer.getWidgetIndex(eventCatcherPanel));
					refreshSelection();
				}
			});

			eventCatcherPanel.addMouseOutHandler(suggestionMouseOutHandler);
			eventCatcherPanel.addClickHandler(suggestionsClickHandler);

			searchSuggestionsContainer.add(eventCatcherPanel);
		}

		modePanel.setVisible(songs.size() > 0 && _currentUser.isLoggedIn());

		if (songs.size() == 0 && !loadMore) {
			searchSuggestionsContainer.add(_noResultsLabel);
		}

		showSuggestions(songs.size());
	}

	@Override
	public void setStringSuggestions(List<String> suggestions, boolean loadMore) {
		setupSearchContainer(loadMore);

		for (String suggestion : suggestions) {
			final Label suggestionLabel = new Label(suggestion);
			suggestionLabel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					getUiHandlers().setSelection(searchSuggestionsContainer.getWidgetIndex(suggestionLabel));
					refreshSelection();
				}
			});

			suggestionLabel.addMouseOutHandler(suggestionMouseOutHandler);
			suggestionLabel.addClickHandler(suggestionsClickHandler);

			searchSuggestionsContainer.add(suggestionLabel);
		}

		if (suggestions.size() == 0 && !loadMore) {
			searchSuggestionsContainer.add(_noResultsLabel);
		}

		showSuggestions(suggestions.size());
	}

	private void setupSearchContainer(boolean loadMore) {
		if (loadMore) {
			if (searchSuggestionsContainer.getWidgetCount() == UnifiedSearchPresenter.SELECTIONS_PER_PAGE) {
				searchSuggestionsContainer.getElement().getStyle().setHeight(searchSuggestionsContainer.getOffsetHeight(), Unit.PX);
			}
		}
		else {
			searchSuggestionsContainer.clear();
			searchSuggestionsContainer.getElement().getStyle().setProperty("height", "initial");
		}
	}

	@Override
	public void setSongTypeSuggestions(List<SearchType> suggestions) {
		searchSuggestionsContainer.clear();

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
					getUiHandlers().setSelection(searchSuggestionsContainer.getWidgetIndex(suggestionLabel));
					refreshSelection();
				}
			});

			suggestionLabel.addMouseOutHandler(suggestionMouseOutHandler);
			suggestionLabel.addClickHandler(suggestionsClickHandler);

			searchSuggestionsContainer.add(suggestionLabel);
		}

		showSuggestions(suggestions.size());
	}

	private void getSongTypeSuggestions() {
		currentSearchTypeContainer.setVisible(false);
		getUiHandlers().setSearchType(null);
		searchResults.setVisible(true);
		getUiHandlers().getSearchResults("");
	}

	private void showSuggestions(int resultsSize) {
		loadMoreButton.setVisible(resultsSize > 0);
		if (resultsSize == UnifiedSearchPresenter.SELECTIONS_PER_PAGE) {
			loadMoreButton.setText("Load more results");
		}
		else {
			loadMoreButton.setText("Loaded " + getUiHandlers().getTotalResults() + " results");
		}

		searchIndicator.setVisible(true);
		searchLoadingIndicator.setVisible(false);
		searchResults.setVisible(true);
		refreshHelpText();
	}
}
