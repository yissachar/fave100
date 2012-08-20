package com.fave100.client.pages.home;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.widgets.FaveDataGrid;
import com.fave100.client.widgets.FaveListBase;
import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class HomeView extends ViewImpl implements HomePresenter.MyView {
	
	private final Widget widget;

	public interface Binder extends UiBinder<Widget, HomeView> {
	}
	
	@UiField(provided = true) FaveDataGrid masterFaveDataGrid;
	
	@Inject
	public HomeView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		masterFaveDataGrid = new FaveDataGrid(requestFactory);		
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == HomePresenter.TOP_BAR_SLOT) {
			topBar.clear();			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@Override
	public FaveDataGrid getMasterFaveDataGrid() {
		return masterFaveDataGrid;
	}
	
	@UiField FaveListBase masterFaveList;

	@Override
	public FaveListBase getMasterFaveList() {
		return masterFaveList;
	}
}
