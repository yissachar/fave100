package com.fave100.client.pages.myfave100;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MyFave100View extends ViewImpl implements
		MyFave100Presenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, MyFave100View> {
	}
	
	@UiField(provided = true) SuggestBox songSuggestBox;
	@UiField(provided = true) UserFaveDataGrid faveList;

	@Inject
	public MyFave100View(final Binder binder, final ApplicationRequestFactory requestFactory) {			
		MusicSuggestionOracle suggestions = new MusicSuggestionOracle();
		songSuggestBox = new SongSuggestBox(suggestions, requestFactory);
		faveList = new UserFaveDataGrid(requestFactory);
		widget = binder.createAndBindUi(this);
		songSuggestBox.getElement().setAttribute("placeholder", "Search songs...");
	}

	@Override
	public Widget asWidget() {
		return widget;
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
	
	@Override
	public SongSuggestBox getSongSuggestBox() {
		return (SongSuggestBox) songSuggestBox;
	}

	@Override
	public UserFaveDataGrid getFaveList() {
		return faveList;
	}
	
}
