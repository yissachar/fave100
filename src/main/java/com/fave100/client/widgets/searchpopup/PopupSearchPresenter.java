package com.fave100.client.widgets.searchpopup;

import com.fave100.client.CurrentUser;
import com.fave100.client.widgets.search.SearchPresenter;
import com.fave100.client.widgets.search.SearchType;
import com.fave100.client.widgets.search.SuggestionSelectedAction;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class PopupSearchPresenter extends PresenterWidget<PopupSearchPresenter.MyView> implements PopupSearchUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<PopupSearchUiHandlers> {
		void reposition();
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> SEARCH_SLOT = new Type<RevealContentHandler<?>>();

	private SearchPresenter _search;
	private Command _suggestionSelectedCommand;

	@Inject
	PopupSearchPresenter(final EventBus eventBus, final MyView view, SearchPresenter searchPresenter, PlaceManager placeManager,
							CurrentUser currentUser) {
		super(eventBus, view);
		_search = searchPresenter;
		_search.setSearchCompletedAction(new PopupSearchCompletedAction(getView()));
		_search.setDarkText(true);
		_search.setFullPageSearch(true);

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(SEARCH_SLOT, _search);
		_search.focus();
	}

	public void onSuggestionSelected() {
		_suggestionSelectedCommand.execute();
	}

	public void setSearchType(SearchType searchType) {
		_search.setSingleSearch(searchType);
	}

	public void setSuggestionSelectedAction(SuggestionSelectedAction action) {
		_search.setSuggestionSelectedAction(action);
	}

}
