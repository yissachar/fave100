package com.fave100.client.pages.myfave100;

import com.fave100.client.requestfactory.FaveItemProxy;
import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
//import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.event.shared.EventBus;

public class MyFave100View extends ViewImpl implements
		MyFave100Presenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, MyFave100View> {
	}
	
	/*public interface DataGridResource extends DataGrid.Resources {
		@Source({ DataGrid.Style.DEFAULT_CSS, "DataGridOverride.css" })
		DataGrid.Style dataGridStyle();
	};*/
	
	@UiField(provided = true) SuggestBox itemInputBox;
	@UiField(provided = true) FaveDataGrid faveList;

	@Inject
	public MyFave100View(final Binder binder, final EventBus eventBus) {
		//DataGridResource resource = GWT.create(DataGridResource.class);	
		MusicSuggestionOracle suggestions = new MusicSuggestionOracle();
		itemInputBox = new SongSuggestBox(suggestions);
		faveList = new FaveDataGrid(eventBus);
		widget = binder.createAndBindUi(this);
		//faveList = new FaveDataGrid(0, resource);
		itemInputBox.getElement().setAttribute("placeholder", "Search songs...");
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public SongSuggestBox getItemInputBox() {
		return (SongSuggestBox) itemInputBox;
	}

	@Override
	public FaveDataGrid getFaveList() {
		return faveList;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == MyFave100Presenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@UiField Button rankButton;
	
	@Override
	public Button getRankButton() {
		return rankButton;
	}
	
}
