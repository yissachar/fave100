package com.fave100.client.pages.lists.widgets.listmanager;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.favelist.ListAddedEvent;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class ListManagerPresenter extends
		PresenterWidget<ListManagerPresenter.MyView>
		implements ListManagerUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListManagerUiHandlers> {
		void refreshList(List<String> lists, String selected);

		void showError(String msg);

		void hideError();

		void setOwnList(boolean ownList);

		void hideDropdown();

		void show();

		void hide();

	}

	@ContentSlot public static final Type<RevealContentHandler<?>> AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();

	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private AppUserProxy _user;
	private String _hashtag;
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private boolean _globalList = false;
	@Inject AddListAutocompletePresenter autocomplete;

	@Inject
	public ListManagerPresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory, final CurrentUser currentUser, final PlaceManager placeManager) {
		super(eventBus, view);
		view.setUiHandlers(ListManagerPresenter.this);
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_currentUser = currentUser;
		_placeManager = placeManager;
	}

	@Override
	protected void onBind() {
		super.onBind();

		ListAddedEvent.register(_eventBus, new ListAddedEvent.Handler() {
			@Override
			public void onListAdded(final ListAddedEvent event) {
				addHashtag(event.getList());
			}
		});
	}

	@Override
	public void onReveal() {
		super.onReveal();
		setInSlot(AUTOCOMPLETE_SLOT, autocomplete);
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().hideDropdown();
	}

	@Override
	public void addHashtag(final String name) {
		if (name.isEmpty())
			return;

		final String error = Validator.validateHashtag(name);
		if (error != null) {
			getView().showError(error);
			return;
		}

		if (_currentUser.getHashtags().size() >= Constants.MAX_LISTS_PER_USER) {
			getView().showError("You can't have  more than " + Constants.MAX_LISTS_PER_USER + " lists");
			return;
		}

		final Request<Void> addFavelistReq = _requestFactory.faveListRequest().addFaveListForCurrentUser(name);
		addFavelistReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				_currentUser.addHashtag(name);
				refreshUsersLists();
				getView().hideError();
				listChanged(name);
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				getView().showError(failure.getMessage());
			}
		});
	}

	@Override
	public void refreshUsersLists() {
		final List<String> hashtags = new ArrayList<String>();
		hashtags.addAll(_user.getHashtags());
		if (!hashtags.contains(Constants.DEFAULT_HASHTAG))
			hashtags.add(0, Constants.DEFAULT_HASHTAG);
		hashtags.remove(_hashtag);
		final boolean ownList = _currentUser != null && _currentUser.equals(_user);
		getView().setOwnList(ownList);
		getView().refreshList(hashtags, _hashtag);
		// Hide the whole list if only default list in there and other user page
		if (hashtags.size() <= 0 && !ownList) {
			getView().hide();
		}
		else {
			getView().show();
		}
	}

	@Override
	public void listChanged(final String list) {
		if (_hashtag.equals(list))
			return;

		getView().hideDropdown();

		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.lists)
				.with(ListPresenter.USER_PARAM, _user.getUsername())
				.with(ListPresenter.LIST_PARAM, list)
				.build());
	}

	@Override
	public void getGlobalAutocomplete(final String searchTerm) {
		if (searchTerm.isEmpty()) {
			final List<String> emptyList = new ArrayList<String>();
			getView().refreshList(emptyList, _hashtag);
			return;
		}

		final Request<List<String>> autocompleteReq = _requestFactory.faveListRequest().getHashtagAutocomplete(searchTerm);
		autocompleteReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(final List<String> suggestions) {
				getView().refreshList(suggestions, _hashtag);
			}
		});
	}

	@Override
	public void setGlobalList(final boolean global) {
		_globalList = global;
	}

	@Override
	public boolean isGlobalList() {
		return _globalList;
	}

	@Override
	public void setAutocompleteFocus(boolean focus) {
		autocomplete.getView().setFocus(focus);
	}

	@Override
	public void clearSearch() {
		autocomplete.getView().clearSearch();
	}

	/* Getters and Setters */

	public AppUserProxy getUser() {
		return _user;
	}

	public void setUser(final AppUserProxy user) {
		this._user = user;
	}

	public String getHashtag() {
		return _hashtag;
	}

	public void setHashtag(final String _hashtag) {
		this._hashtag = _hashtag;
	}
}

interface ListManagerUiHandlers extends UiHandlers {

	void addHashtag(String name);

	void listChanged(String list);

	void setGlobalList(boolean global);

	boolean isGlobalList();

	void getGlobalAutocomplete(String searchTerm);

	void refreshUsersLists();

	void setAutocompleteFocus(boolean focus);

	void clearSearch();
}