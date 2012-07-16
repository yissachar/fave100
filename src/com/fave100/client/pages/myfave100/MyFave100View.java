package com.fave100.client.pages.myfave100;

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
	
	private final MusicSuggestionOracle suggestions = new MusicSuggestionOracle();
	
	@UiField(provided = true) SuggestBox itemInputBox;

	@Inject
	public MyFave100View(final Binder binder) {
		itemInputBox = new SuggestBox(suggestions);
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel selectedSuggestion;

	@Override
	public SuggestBox getItemInputBox() {
		return itemInputBox;
	}

	@Override
	public MusicSuggestionOracle getSuggestions() {
		return suggestions;
	}
}
