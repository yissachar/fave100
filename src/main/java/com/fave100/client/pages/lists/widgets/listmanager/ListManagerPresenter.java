package com.fave100.client.pages.lists.widgets.listmanager;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.events.favelist.ListAddedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.widgets.alert.AlertCallback;
import com.fave100.client.widgets.alert.AlertPresenter;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class ListManagerPresenter extends
		PresenterWidget<ListManagerPresenter.MyView>
		implements ListManagerUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListManagerUiHandlers> {
		void refreshList(List<String> lists, String selected, boolean ownList);

		void showError(String msg);

		void hideError();

		void setOwnList(boolean ownList);

		void hideDropdown();

		void show();

		void hide();

	}

	@ContentSlot public static final Type<RevealContentHandler<?>> AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();

	private EventBus _eventBus;
	private AppUser _user;
	private String _hashtag;
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private FaveApi _api;
	private boolean _globalList = false;
	@Inject AddListAutocompletePresenter autocomplete;
	@Inject AlertPresenter alertPresenter;

	@Inject
	public ListManagerPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final PlaceManager placeManager,
								final FaveApi api) {
		super(eventBus, view);
		view.setUiHandlers(ListManagerPresenter.this);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_placeManager = placeManager;
		_api = api;
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

		_api.call(_api.service().user().addFaveListForCurrentUser(name), new RestCallback<Void>() {

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					getView().showError(response.getText());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Already handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				_currentUser.addHashtag(name);
				refreshUsersLists();
				getView().hideError();
				listChanged(name);
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
		getView().refreshList(hashtags, _hashtag, ownList);
		getView().show();
	}

	@Override
	public void listChanged(final String list) {
		if (_hashtag.equals(list))
			return;

		getView().hideDropdown();

		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.lists)
				.with(PlaceParams.USER_PARAM, _user.getUsername())
				.with(PlaceParams.LIST_PARAM, list)
				.build());
	}

	@Override
	public void deleteList(final String listName) {
		alertPresenter.setAlertCallback(new AlertCallback() {

			@Override
			public void onOk() {
				_api.call(_api.service().user().deleteFaveListForCurrentUser(listName), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub

					}
				});
				_currentUser.deleteList(listName);
				refreshUsersLists();
			}

			@Override
			public void onCancel() {
				// Alert canceled, do nothing				
			}
		});
		addToPopupSlot(alertPresenter);
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

	public AppUser getUser() {
		return _user;
	}

	public void setUser(final AppUser user) {
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

	void refreshUsersLists();

	void setAutocompleteFocus(boolean focus);

	void clearSearch();

	void deleteList(String listName);
}