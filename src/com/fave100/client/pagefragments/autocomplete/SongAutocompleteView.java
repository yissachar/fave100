package com.fave100.client.pagefragments.autocomplete;

import java.util.List;

import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
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

	interface SongAutocompleteStyle extends CssResource {
		String selected();

		String artistName();
	}

	@UiField SongAutocompleteStyle style;
	@UiField TextBox searchBox;
	@UiField HTMLPanel resultsArea;
	@UiField FocusPanel eventCatcher;
	@UiField HTMLPanel resultsPanel;
	@UiField Label pageStats;
	@UiField Button previousButton;
	@UiField Button nextButton;

	@Inject
	public SongAutocompleteView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		searchBox.getElement().setAttribute("placeholder",
				"Search songs...");
		resultsArea.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
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
		}
		else if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
			// Enter key pressed, add currently selected song to favelist
			getUiHandlers().songSelected();
		}
		else {
			// Otherwise search for song
			getUiHandlers().getAutocompleteResults(searchBox.getText(), true);
		}
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

	@Override
	public void setSuggestions(final List<SongProxy> suggestions, final int total) {
		resultsPanel.clear();
		if (suggestions == null || suggestions.size() == 0) {
			resultsArea.setVisible(false);
			return;
		}

		int numResults = 0;
		for (final SongProxy suggestion : suggestions) {
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
		sb.append(numResults * getUiHandlers().getPage() + 1);
		sb.append(" - ");
		sb.append(numResults * getUiHandlers().getPage() + numResults);
		sb.append(" of ");
		sb.append(total);
		pageStats.setText(sb.toString());
		resultsArea.setVisible(true);

	}

	@Override
	public void setSelection(final int selection) {
		for (int i = 0; i < resultsPanel.getWidgetCount(); i++) {
			resultsPanel.getWidget(i).getElement().removeClassName(style.selected());
		}
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
	}
}
