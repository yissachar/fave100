package com.fave100.client.pages.lists.widgets.addsongsearch;

import com.fave100.client.CurrentUser;
import com.fave100.client.widgets.search.SearchPresenter;
import com.fave100.client.widgets.search.SearchType;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class AddSongSearchPresenter extends PresenterWidget<AddSongSearchPresenter.MyView> implements AddSongSearchUiHandlers {

	public interface MyView extends PopupView, HasUiHandlers<AddSongSearchUiHandlers> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> SEARCH_SLOT = new Type<RevealContentHandler<?>>();

	private SearchPresenter _search;

	@Inject
	AddSongSearchPresenter(final EventBus eventBus, final MyView view, SearchPresenter searchPresenter, PlaceManager placeManager,
							CurrentUser currentUser) {
		super(eventBus, view);
		_search = searchPresenter;
		_search.setSingleSearch(SearchType.SONGS);
		_search.setSuggestionSelectedAction(new AddSongSuggestionSelectedAction(placeManager, currentUser, getView()));
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

}
