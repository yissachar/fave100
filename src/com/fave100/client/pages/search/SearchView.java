package com.fave100.client.pages.search;

import com.fave100.client.pages.BasePresenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHTML;
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
	@UiField InlineHTML iTunesResults;

	@Inject
	public SearchView(final Binder binder) {
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
		getUiHandlers().showResults(searchBox.getValue());
	}

	@Override
	public void setResults(final String results) {
		iTunesResults.setHTML(results);
	}
}
