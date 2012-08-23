package com.fave100.client.pages.myfave100;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class MyFave100View extends ViewImpl implements
		MyFave100Presenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, MyFave100View> {
	}
	
	@UiField(provided = true) SuggestBox songSuggestBox;
	@UiField(provided = true) PersonalFaveList personalFaveList;

	@Inject
	public MyFave100View(final Binder binder, final ApplicationRequestFactory requestFactory) {			
		final MusicSuggestionOracle suggestions = new MusicSuggestionOracle();
		songSuggestBox = new SongSuggestBox(suggestions, requestFactory);
		personalFaveList = new PersonalFaveList(requestFactory);
		widget = binder.createAndBindUi(this);
		songSuggestBox.getElement().setAttribute("placeholder", "Add a song...");
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	@UiField HTMLPanel faveFeed;
	
	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();			
			if(content != null) {
				topBar.add(content);
			}
		}
		if(slot == MyFave100Presenter.FAVE_FEED_SLOT) {
			faveFeed.clear();			
			if(content != null) {
				faveFeed.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@Override
	public SongSuggestBox getSongSuggestBox() {
		return (SongSuggestBox) songSuggestBox;
	}

	@Override
	public PersonalFaveList getPersonalFaveList() {
		return personalFaveList;
	}
	
}
