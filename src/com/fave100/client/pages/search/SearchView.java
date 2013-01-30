package com.fave100.client.pages.search;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.events.ResultPageChangedEvent;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.widgets.SimplePager;
import com.fave100.client.widgets.advancedsearch.SearchResultCell;
import com.fave100.client.widgets.advancedsearch.SearchResultCellList;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SearchView extends ViewWithUiHandlers<SearchUiHandlers> implements
		SearchPresenter.MyView {

	private final Widget	widget;

	public interface Binder extends UiBinder<Widget, SearchView> {
	}

	@UiField
	HTMLPanel				topBar;
	@UiField
	FormPanel				searchForm;
	@UiField
	TextBox					songSearchBox;
	@UiField
	TextBox					artistSearchBox;
	@UiField
	Button					searchButton;
	@UiField
	Label					searchStatus;
	@UiField(provided = true)
	SearchResultCellList	iTunesResults;
	@UiField(provided = true)
	SimplePager				pager;

	@Inject
	public SearchView(final Binder binder,
			final ApplicationRequestFactory requestFactory,
			final EventBus eventBus) {
		final SearchResultCell cell = new SearchResultCell();
		iTunesResults = new SearchResultCellList(cell);
		pager = new SimplePager(eventBus);
		widget = binder.createAndBindUi(this);

		pager.setVisible(false);

		// Handle page change events
		ResultPageChangedEvent.register(eventBus,
				new ResultPageChangedEvent.Handler() {

					@Override
					public void onPageChanged(final ResultPageChangedEvent event) {
						Window.scrollTo(0, 0);
						getUiHandlers().showResults(songSearchBox.getValue(),
								artistSearchBox.getValue());
					}
				});
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();

			if (content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("searchForm")
	void onSearchFormSubmit(final SubmitEvent event) {
		pager.setPageNumber(1);
		getUiHandlers().showResults(songSearchBox.getValue(),
				artistSearchBox.getValue());
	}

	@Override
	public void setResultCount(final int count) {
		if (count == 0) {
			searchStatus.setText("No matches found");
			pager.setMaxPageNumber(1);
		} else {
			searchStatus.setText(count + " matches found");
			pager.setMaxPageNumber((int) Math.ceil((double) count
					/ SearchPresenter.RESULTS_PER_PAGE));
		}
	}

	@Override
	public void setResults(final List<SongProxy> resultList) {
		iTunesResults.setRowData(resultList);
		iTunesResults.setCellUiHandlers(getUiHandlers());
		pager.setVisible(true);
	}

	@Override
	public void resetView() {
		songSearchBox.setValue("");
		artistSearchBox.setValue("");
		setResults(new ArrayList<SongProxy>());
		searchStatus.setText("");
	}

	@Override
	public int getPageNum() {
		return pager.getPageNumber();
	}
}
