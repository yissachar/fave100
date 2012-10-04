package com.fave100.client.pages.search;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SearchView extends ViewWithUiHandlers<SearchUiHandlers> implements SearchPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, SearchView> {
	}
	
	@UiField HTMLPanel topBar;
	@UiField TextBox searchBox;
	@UiField Button searchButton;
	@UiField Label searchStatus;
	@UiField ListBox searchAttributesListBox;
	@UiField(provided=true) CellList<MusicbrainzResult> iTunesResults;

	@Inject
	public SearchView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		final AdvancedSearchResultCell cell = new AdvancedSearchResultCell(requestFactory);
		iTunesResults = new CellList<MusicbrainzResult>(cell);
		widget = binder.createAndBindUi(this);
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
	
	@UiHandler("searchButton")
	public void onClick(final ClickEvent event) {
		getUiHandlers().showResults(searchBox.getValue(), searchAttributesListBox.getValue(searchAttributesListBox.getSelectedIndex()));
	}
	
	@UiHandler("searchBox")
	public void onKeyUp(final KeyUpEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			searchButton.click();
		}
	}

	@Override
	public void setResults(final List<MusicbrainzResult> resultList) {
		iTunesResults.setRowData(resultList);
		if(resultList.size() == 0) {
			searchStatus.setText("No matches found");
		} else {
			searchStatus.setText(resultList.size()+" matches found");
		}
	}

	@Override
	public void resetView() {
		searchBox.setValue("");
		setResults(new ArrayList<MusicbrainzResult>());
		searchStatus.setText("");
	}
}
