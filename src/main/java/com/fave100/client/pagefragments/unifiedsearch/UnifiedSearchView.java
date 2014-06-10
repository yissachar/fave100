package com.fave100.client.pagefragments.unifiedsearch;

import java.util.List;

import com.fave100.client.Utils;
import com.fave100.client.entities.SongDto;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.Icon;
import com.google.gwt.dom.client.Element;
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
	@UiField TextBox searchBox;
	@UiField Panel searchResults;
	@UiField FlowPanel searchSuggestionsContainer;
	@UiField FlowPanel buttonContainer;
	@UiField Icon previousButton;
	@UiField Icon nextButton;
	private Timer searchTimer;
	private HandlerRegistration rootClickHandler = null;

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
		}
	};

	@Inject
	UnifiedSearchView(Binder binder) {
		initWidget(binder.createAndBindUi(this));
		buttonContainer.setVisible(false);
		searchResults.setVisible(false);

		ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element target = Element.as(event.getNativeEvent().getEventTarget());
				if (!Utils.widgetContainsElement(buttonContainer, target) && !Utils.widgetContainsElement(searchBox, target)
						&& !Utils.widgetContainsElement(currentSearchTypeContainer, target)) {
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
		if (!currentSearchTypeContainer.isVisible()) {
			searchResults.setVisible(true);
			getSongTypeSuggestions();
		}
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
			else if (event.isRightArrow()) {
				if (getUiHandlers().getSelection() >= 0) {
					getUiHandlers().incrementPage();
					moveCursorToEnd();
				}
			}
			else if (event.isLeftArrow()) {
				if (getUiHandlers().getSelection() >= 0) {
					getUiHandlers().decrementPage();
					moveCursorToEnd();
				}
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
					//searchLoadingIndicator.setVisible(true);
					//inlineSearchCount.setText("");
					searchSuggestionsContainer.setVisible(true);
					getUiHandlers().getSearchResults(searchTerm);
				}
			};
			searchTimer.schedule(50);
		}

		refreshSelection();
	}

	@UiHandler("previousButton")
	void onPreviousButtonClick(ClickEvent event) {
		if (previousButton.isEnabled()) {
			getUiHandlers().decrementPage();
		}
	}

	@UiHandler("nextButton")
	void onNextButtonClick(ClickEvent event) {
		if (nextButton.isEnabled()) {
			getUiHandlers().incrementPage();
		}
	}

	private void moveCursorToEnd() {
		searchBox.setCursorPos(searchBox.getText().length());
	}

	private void clearSearchResults() {
		searchBox.setText("");
		searchSuggestionsContainer.clear();
		searchResults.setVisible(false);
		buttonContainer.setVisible(false);
		getUiHandlers().clearSearchResults();
	}

	private void refreshSelection() {
		int selection = getUiHandlers().getSelection();
		int widgetCount = searchSuggestionsContainer.getWidgetCount();

		for (int i = 0; i < widgetCount; i++) {
			searchSuggestionsContainer.getWidget(i).getElement().removeClassName(style.selected());
		}

		if (selection >= 0 && widgetCount > 0) {
			searchSuggestionsContainer.getWidget(selection).addStyleName(style.selected());
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
	public void setSongSuggestions(List<SongDto> songs) {
		searchSuggestionsContainer.clear();
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

		setButtons(songs.size());
	}

	@Override
	public void setStringSuggestions(List<String> suggestions) {
		searchSuggestionsContainer.clear();
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

		setButtons(suggestions.size());
	}

	@Override
	public void setSongTypeSuggestions(List<SearchType> suggestions) {
		searchSuggestionsContainer.clear();
		searchSuggestionsContainer.setVisible(true);

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

		setButtons(suggestions.size());
	}

	private void getSongTypeSuggestions() {
		currentSearchTypeContainer.setVisible(false);
		getUiHandlers().setSearchType(null);
		getUiHandlers().getSearchResults("");
	}

	private void setButtons(int resultsSize) {
		previousButton.setEnabled(getUiHandlers().getPage() > 0);
		nextButton.setEnabled(resultsSize == UnifiedSearchPresenter.SELECTIONS_PER_PAGE);
		buttonContainer.setVisible(previousButton.isEnabled() || nextButton.isEnabled());
		searchResults.setVisible(resultsSize > 0);
	}
}
