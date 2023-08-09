package com.fave100.client.pages.admin;

import com.fave100.client.FaveApi;
import com.fave100.client.Notification;
import com.fave100.client.gatekeepers.AdminGatekeeper;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.AppUserCollection;
import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.generated.entities.FeaturedLists;
import com.fave100.client.pages.PagePresenter;
import com.fave100.client.widgets.search.SearchType;
import com.fave100.client.widgets.search.SuggestionSelectedAction;
import com.fave100.client.widgets.searchpopup.PopupSearchPresenter;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

import java.util.ArrayList;
import java.util.List;

public class AdminPresenter extends PagePresenter<AdminPresenter.MyView, AdminPresenter.MyProxy> implements AdminUiHandlers {

	interface MyView extends View, HasUiHandlers<AdminUiHandlers> {

		void setAdmins(List<AppUser> admins);

		void setCritics(List<AppUser> critics);

		void setFeaturedLists(List<String> lists);

		void setFeaturedListsRandomized(boolean randomized);
	}

	@NameToken(NameTokens.admin)
	@ProxyCodeSplit
	@UseGatekeeper(AdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<AdminPresenter> {
	}

	private PopupSearchPresenter _adminSearchPresenter;
	private FaveApi _api;
	private RestCallback<Void> removeItemCallback;
	private RestCallback<Void> addSpecialUserCallback;

	@ContentSlot public static final Type<RevealContentHandler<?>> ADMIN_SEARCH_SLOT = new Type<RevealContentHandler<?>>();

	@Inject
	AdminPresenter(EventBus eventBus, MyView view, MyProxy proxy, PopupSearchPresenter adminSearchPresenter, FaveApi api) {
		super(eventBus, view, proxy);

		_adminSearchPresenter = adminSearchPresenter;
		_api = api;

		removeItemCallback = new RestCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// Handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				// Success already handled - the widget is removed client side immediately
				// Only on failure do we need to handle anything
			}

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					onReveal();
					if (response.getStatusCode() == 401) {
						Notification.show("You are not authorized for that", true);
					}
					else {
						Notification.show("Something went wrong: " + response.getText(), true);
					}
				}

			}
		};

		addSpecialUserCallback = new RestCallback<Void>() {

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() == 401) {
					Notification.show("You are not authorized for that action", true);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Error handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				onReveal();
			}
		};

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		_api.call(_api.service().user().getAdmins(), new RestCallback<AppUserCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// Handled in setResponse
			}

			@Override
			public void onSuccess(AppUserCollection result) {
				getView().setAdmins(result.getItems());
			}

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() == 401) {
					Notification.show("You are not authorized for that action", true);
				}
			}
		});

		_api.call(_api.service().user().getCritics(), new RestCallback<AppUserCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// Handled in setResponse
			}

			@Override
			public void onSuccess(AppUserCollection result) {
				getView().setCritics(result.getItems());
			}

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() == 401) {
					Notification.show("You are not authorized for that action", true);
				}
			}
		});

		_api.call(_api.service().favelists().getFeaturedLists(), new RestCallback<FeaturedLists>() {

			@Override
			public void onFailure(Throwable caught) {
				// Handled in setResponse
			}

			@Override
			public void onSuccess(FeaturedLists result) {
				getView().setFeaturedLists(result.getLists());
				getView().setFeaturedListsRandomized(result.isRandomized());
			}

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() == 401) {
					Notification.show("You are not authorized for that action", true);
				}
			}
		});
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().setAdmins(new ArrayList<AppUser>());
		getView().setCritics(new ArrayList<AppUser>());
		getView().setFeaturedLists(new ArrayList<String>());
	}

	@Override
	public void showAddAdminPrompt() {
		_adminSearchPresenter.setSearchType(SearchType.USERS);
		_adminSearchPresenter.setSuggestionSelectedAction(new SuggestionSelectedAction() {

			@Override
			public void execute(SearchType searchType, Object selectedItem) {
				String username = (String)selectedItem;
				_api.call(_api.service().user().createAdmin(username), addSpecialUserCallback);
				removeFromPopupSlot(_adminSearchPresenter);
			}
		});
		addToPopupSlot(_adminSearchPresenter);
	}

	@Override
	public void showAddCriticPrompt() {
		_adminSearchPresenter.setSearchType(SearchType.USERS);
		_adminSearchPresenter.setSuggestionSelectedAction(new SuggestionSelectedAction() {

			@Override
			public void execute(SearchType searchType, Object selectedItem) {
				String username = (String)selectedItem;
				_api.call(_api.service().user().createCritic(username), addSpecialUserCallback);
				removeFromPopupSlot(_adminSearchPresenter);
			}
		});
		addToPopupSlot(_adminSearchPresenter);
	}

	@Override
	public void showAddListPrompt() {
		_adminSearchPresenter.setSearchType(SearchType.LISTS);
		_adminSearchPresenter.setSuggestionSelectedAction(new SuggestionSelectedAction() {

			@Override
			public void execute(SearchType searchType, Object selectedItem) {
				String list = (String)selectedItem;
				_api.call(_api.service().favelists().addFeaturedList(list), addSpecialUserCallback);
				removeFromPopupSlot(_adminSearchPresenter);
			}
		});
		addToPopupSlot(_adminSearchPresenter);
	}

	@Override
	public void removeAdmin(AppUser admin) {
		_api.call(_api.service().user().removeAdmin(admin.getUsername()), removeItemCallback);
	}

	@Override
	public void removeCritic(AppUser critic) {
		_api.call(_api.service().user().removeCritic(critic.getUsername()), removeItemCallback);
	}

	@Override
	public void removeFeaturedList(String list) {
		_api.call(_api.service().favelists().removeFeaturedList(list), removeItemCallback);

	}

	@Override
	public void setFeatureListsRandom(boolean random) {
		BooleanResult randomized = new BooleanResult();
		randomized.setValue(random);

		_api.call(_api.service().favelists().setFeaturedFavelistsRandomized(randomized), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub				
			}
		});
	}
}