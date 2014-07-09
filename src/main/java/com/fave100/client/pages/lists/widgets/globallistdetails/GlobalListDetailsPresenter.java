package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.events.LoginDialogRequestedEvent;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.StringResultCollection;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> implements GlobalListDetailsUiHandlers {

	public interface MyView extends View, HasUiHandlers<GlobalListDetailsUiHandlers> {

		void setTrendingLists(String hashtag, List<String> lists, CurrentUser currentUser);

		void show();

		void hide();

	}

	private EventBus _eventBus;
	private CurrentUser _currentUser;
	private FaveApi _api;
	private String _hashtag;

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final FaveApi api) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_api = api;
		getView().setUiHandlers(this);
	}

	@Override
	public void onReveal() {
		super.onReveal();

		_api.call(_api.service().trending().getTrendingFaveLists(), new AsyncCallback<StringResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(StringResultCollection result) {
				List<String> trending = new ArrayList<>();
				for (StringResult stringResult : result.getItems()) {
					trending.add(stringResult.getValue());
				}
				getView().setTrendingLists(_hashtag, trending, _currentUser);
			}
		});
	}

	@Override
	public void showRegister() {
		_eventBus.fireEvent(new LoginDialogRequestedEvent(true));
	}

}

interface GlobalListDetailsUiHandlers extends UiHandlers {

	void showRegister();

}
