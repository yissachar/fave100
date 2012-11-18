package com.fave100.client.pages.search;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.events.ResultPageChangedEvent;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.widgets.SimplePager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SearchView extends ViewWithUiHandlers<SearchUiHandlers> implements SearchPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, SearchView> {
	}

	@UiField HTMLPanel topBar;
	@UiField FormPanel searchForm;
	@UiField TextBox songSearchBox;
	@UiField TextBox artistSearchBox;
	@UiField Button searchButton;
	@UiField Label searchStatus;
	@UiField(provided = true) CellList<MusicbrainzResult> iTunesResults;
	@UiField(provided = true) SimplePager pager;

	@Inject
	public SearchView(final Binder binder, final ApplicationRequestFactory requestFactory,
			final EventBus eventBus) {
		final AdvancedSearchResultCell cell = new AdvancedSearchResultCell(requestFactory);
		iTunesResults = new CellList<MusicbrainzResult>(cell);
		pager = new SimplePager(eventBus);
		widget = binder.createAndBindUi(this);

		pager.setVisible(false);

		ResultPageChangedEvent.register(eventBus, new ResultPageChangedEvent.Handler() {

			@Override
			public void onPageChanged(final ResultPageChangedEvent event) {
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
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();

			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiHandler("searchForm")
	void onSearchFormSubmit(final SubmitEvent event) {
		getUiHandlers().showResults(songSearchBox.getValue(),
				artistSearchBox.getValue());
	}

	/*@UiHandler("pager")
	void onPageChanged(final ResultPageChangedEvent event) {
		Window.alert("Page num: "+event.getPageNumber());
	}*/

	@Override
	public void setResultCount(final int count) {
		if(count == 0) {
			searchStatus.setText("No matches found");
			pager.setMaxPageNumber(1);
		} else {
			searchStatus.setText(count+" matches found");
			pager.setMaxPageNumber((int)Math.ceil( (double) count/SearchPresenter.RESULTS_PER_PAGE));
		}
	}

	@Override
	public void setResults(final List<MusicbrainzResult> resultList) {
		iTunesResults.setRowData(resultList);
		pager.setVisible(true);
	}

	@Override
	public void resetView() {
		songSearchBox.setValue("");
		artistSearchBox.setValue("");
		setResults(new ArrayList<MusicbrainzResult>());
		searchStatus.setText("");
	}

	@Override
	public int getPageNum() {
		return pager.getPageNumber();
	}
}
