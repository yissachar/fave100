package com.fave100.client.pages.lists.widgets.autocomplete.song;

import static com.google.gwt.query.client.GQuery.$;

import java.util.List;

import com.fave100.client.entities.SongDto;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.helpbubble.HelpBubble;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SongAutocompleteView extends ViewWithUiHandlers<SongAutocompleteUiHandlers> implements
		SongAutocompletePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, SongAutocompleteView> {
	}

	interface SongAutocompleteStyle extends GlobalStyle {
		String selected();

		String artistName();

		String placeholder();
	}

	@UiField SongAutocompleteStyle style;
	@UiField TextBox searchBox;
	@UiField Label inlineSearchCount;
	@UiField HTMLPanel searchLoadingIndicator;
	@UiField InlineLabel clearSearchButton;
	@UiField(provided = true) HelpBubble helpBubble;
	@UiField HTMLPanel resultsArea;
	@UiField FocusPanel eventCatcher;
	@UiField HTMLPanel resultsPanel;
	@UiField Label pageStats;
	@UiField Button previousButton;
	@UiField Button nextButton;
	@UiField FocusPanel backToTopButton;
	final private String placeholder = "To add a song, start typing here";
	Timer searchTimer;

	@Inject
	public SongAutocompleteView(final Binder binder) {
		final String helpText = "Use the search box to search for your favorite songs and add them to your list";
		helpBubble = new HelpBubble("Song search", helpText, 200, HelpBubble.Direction.LEFT);
		helpBubble.setVisible(false);
		widget = binder.createAndBindUi(this);
		resultsArea.setVisible(false);
		backToTopButton.setVisible(false);
		// Set a placeholder text
		DomEvent.fireNativeEvent(Document.get().createBlurEvent(), searchBox);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("searchBox")
	void onFocus(final FocusEvent event) {
		if (searchBox.getText().equals(placeholder)) {
			searchBox.setText("");
			searchBox.removeStyleName(style.placeholder());
		}
	}

	@UiHandler("searchBox")
	void onBlur(final BlurEvent event) {
		if (searchBox.getText().isEmpty() || searchBox.getText().equals(placeholder)) {
			searchBox.setText(placeholder);
			searchBox.addStyleName(style.placeholder());
		}
	}

	@UiHandler("searchBox")
	void onKeyUp(final KeyUpEvent event) {
		// If arrow key pressed
		if (KeyUpEvent.isArrow(event.getNativeKeyCode())) {
			// If down or up arrow, adjust song selection accordingly
			if (event.isDownArrow()) {
				getUiHandlers().setSelection(1, true);
			}
			else if (event.isUpArrow()) {
				getUiHandlers().setSelection(-1, true);
			}
			else if (event.isRightArrow()) {
				if (getUiHandlers().getSelection() >= 0) {
					getUiHandlers().modifyPage(1);
					moveCursorToEnd();
				}
			}
			else if (event.isLeftArrow()) {
				if (getUiHandlers().getSelection() >= 0) {
					getUiHandlers().modifyPage(-1);
					moveCursorToEnd();
				}
			}
		}
		else if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
			// Enter key pressed, add currently selected song to favelist
			getUiHandlers().songSelected();
		}
		else if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
			// Escape key, cancel search
			getUiHandlers().getAutocompleteResults("", true);
		}
		else {
			// Otherwise search for song
			if (searchTimer != null)
				searchTimer.cancel();
			searchTimer = new Timer() {
				@Override
				public void run() {
					searchLoadingIndicator.setVisible(true);
					inlineSearchCount.setText("");
					getUiHandlers().getAutocompleteResults(searchBox.getText(), true);
				}
			};
			searchTimer.schedule(50);
		}
	}

	private void moveCursorToEnd() {
		searchBox.setCursorPos(searchBox.getText().length());
	}

	@UiHandler("clearSearchButton")
	void onClearSearchButtonClick(final ClickEvent event) {
		clearSearch();
		getUiHandlers().getAutocompleteResults("", true);
	}

	@UiHandler("eventCatcher")
	void onHover(final MouseMoveEvent event) {
		for (int i = 0; i < resultsPanel.getWidgetCount(); i++) {
			final Widget w = resultsPanel.getWidget(i);
			// If mouse move occurs over song suggestion, set it as selected
			if (event.getClientX() >= w.getAbsoluteLeft()
					&& event.getClientX() <= w.getAbsoluteLeft() + w.getOffsetWidth()
					&& event.getClientY() + Window.getScrollTop() >= w.getAbsoluteTop()
					&& event.getClientY() + Window.getScrollTop() <= w.getAbsoluteTop() + w.getOffsetHeight())
			{
				getUiHandlers().setSelection(i, false);
			}
		}
	}

	@UiHandler("eventCatcher")
	void onResultsClick(final ClickEvent event) {
		getUiHandlers().songSelected();
	}

	@UiHandler("previousButton")
	void onPreviousClick(final ClickEvent event) {
		getUiHandlers().modifyPage(-1);
	}

	@UiHandler("nextButton")
	void onNextClick(final ClickEvent event) {
		getUiHandlers().modifyPage(1);
	}

	@UiHandler("backToTopButton")
	void onBackToTopClick(final ClickEvent event) {
		$("html").animate("scrollTop: 0", 300);
		$("body").animate("scrollTop: 0", 300);
	}

	@Override
	public void setSuggestions(final List<SongDto> suggestions, final int total) {
		resultsPanel.clear();
		searchLoadingIndicator.setVisible(false);
		inlineSearchCount.removeStyleName("error");

		if (suggestions == null || suggestions.size() == 0) {
			resultsArea.setVisible(false);
			if (suggestions != null) {
				if (suggestions.size() == 0)
					inlineSearchCount.setText("0");
				else
					inlineSearchCount.setText("");
			}
			return;
		}

		int numResults = 0;
		for (final SongDto suggestion : suggestions) {
			numResults++;
			final HTMLPanel songItem = new HTMLPanel("");
			final Label songName = new Label(suggestion.getSong());
			final Label artistName = new Label(suggestion.getArtist());
			artistName.getElement().addClassName(style.artistName());
			songItem.add(songName);
			songItem.add(artistName);
			resultsPanel.add(songItem);
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("Results ");
		sb.append(5 * getUiHandlers().getPage() + 1);
		sb.append(" - ");
		sb.append(5 * getUiHandlers().getPage() + numResults);
		sb.append(" of ");
		sb.append(total);
		pageStats.setText(sb.toString());
		resultsArea.setVisible(true);
		inlineSearchCount.setText(String.valueOf(total));
	}

	@Override
	public void setSearchError(final String error) {
		inlineSearchCount.setText(error);
		inlineSearchCount.addStyleName("error");
		searchLoadingIndicator.setVisible(false);
	}

	@Override
	public void setSelection(final int selection) {
		for (int i = 0; i < resultsPanel.getWidgetCount(); i++) {
			resultsPanel.getWidget(i).getElement().removeClassName(style.selected());
		}
		if (selection >= 0)
			resultsPanel.getWidget(selection).getElement().addClassName(style.selected());
	}

	@Override
	public String getSearchTerm() {
		return searchBox.getText();
	}

	@Override
	public void showPrevious(final boolean show) {
		previousButton.setEnabled(show);
	}

	@Override
	public void showNext(final boolean show) {
		nextButton.setEnabled(show);
	}

	@Override
	public void clearSearch() {
		searchBox.setText("");
		inlineSearchCount.setText("");
		searchLoadingIndicator.setVisible(false);
	}

	@Override
	public void showHelp() {
		helpBubble.setVisible(true);
	}

	@Override
	public void hideHelp() {
		helpBubble.setVisible(false);
	}

	@Override
	public void resizeSearch() {
		resultsArea.setWidth(searchBox.getOffsetWidth() + "px");
	}

	@Override
	public void showBackToTop(final boolean show) {
		backToTopButton.setVisible(show);
	}

	@Override
	public void setFocus() {
		searchBox.setFocus(true);
	}
}
