package com.fave100.client.pages.song.widgets.whyline;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.UserListResult;
import com.fave100.client.generated.entities.UserListResultCollection;
import com.fave100.client.generated.entities.Whyline;
import com.fave100.client.generated.entities.WhylineCollection;
import com.fave100.client.generated.services.RestServiceFactory;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class WhylinePresenter extends PresenterWidget<WhylinePresenter.MyView> implements WhylineUiHandlers {
	public interface MyView extends View, HasUiHandlers<WhylineUiHandlers> {
		void setWhylines(List<Whyline> whylines);

		void setUserLists(List<UserListResult> userLists);
	}

	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;

	@Inject
	WhylinePresenter(final EventBus eventBus, final MyView view, final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view);
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;

		getView().setUiHandlers(this);
	}

	@Override
	public void onHide() {
		super.onHide();
		getView().setWhylines(new ArrayList<Whyline>());
		getView().setUserLists(new ArrayList<UserListResult>());
	}

	public void showWhylines(FaveItem song) {
		_dispatcher.execute(_restServiceFactory.songs().getWhylines(song.getId()), new AsyncCallback<WhylineCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(WhylineCollection result) {
				getView().setWhylines(result.getItems());
				/*if (whylineList.size() == 0) {
					final Label label = new Label("No whylines yet");
					label.addStyleName(style.noWhyline());
					whylines.add(label);
				}*/
			}
		});

		_dispatcher.execute(_restServiceFactory.songs().getFaveLists(song.getId()), new AsyncCallback<UserListResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(UserListResultCollection result) {
				getView().setUserLists(result.getItems());
			}
		});
	}
}
