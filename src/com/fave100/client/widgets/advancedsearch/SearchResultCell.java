package com.fave100.client.widgets.advancedsearch;

import com.fave100.client.pages.search.SearchUiHandlers;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * A result cell that will presented when users use the advanced search page
 * that allows users to add songs to their FaveList
 *
 * @author yissachar.radcliffe
 *
 */
public class SearchResultCell extends AbstractCell<SongProxy> {

	interface SearchResultCellUiRenderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String songTitle, String artist);

		void onBrowserEvent(SearchResultCell o, NativeEvent e, Element p,
				SongProxy n);
	}

	private static SearchResultCellUiRenderer	uiRenderer	= GWT.create(SearchResultCellUiRenderer.class);
	private SearchUiHandlers					uiHandlers;

	public SearchResultCell() {
		super("click");
	}

	@UiHandler({ "advancedSearchAddButton" })
	void onAddButtonClicked(final ClickEvent event, final Element parent,
			final SongProxy song) {
		uiHandlers.addSong(song);
	}

	@Override
	public void render(final Context context, final SongProxy song,
			final SafeHtmlBuilder builder) {
		uiRenderer.render(builder, song.getSong(), song.getArtist());
	}

	@Override
	public void onBrowserEvent(final Context context, final Element parent,
			final SongProxy song, final NativeEvent event,
			final ValueUpdater<SongProxy> updater) {
		uiRenderer.onBrowserEvent(this, event, parent, song);
	}

	public SearchUiHandlers getUiHandlers() {
		return uiHandlers;
	}

	public void setUiHandlers(final SearchUiHandlers uiHandlers) {
		this.uiHandlers = uiHandlers;
	}

}
