package com.fave100.client.pages.song.widgets.whyline;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.generated.entities.FaveItemDto;
import com.fave100.client.generated.entities.UserListResultCollection;
import com.fave100.client.generated.entities.UserListResultDto;
import com.fave100.client.generated.services.FaveListService;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class WhylinePresenter extends PresenterWidget<WhylinePresenter.MyView> implements WhylineUiHandlers {
	public interface MyView extends View, HasUiHandlers<WhylineUiHandlers> {
		void setWhylines(List<WhylineProxy> whylines);

		void setUserLists(List<UserListResultDto> userLists);
	}

	private ApplicationRequestFactory _requestFactory;
	private DispatchAsync _dispatcher;
	private FaveListService _faveListService;

	@Inject
	WhylinePresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory, final DispatchAsync dispatcher, final FaveListService faveListService) {
		super(eventBus, view);
		_requestFactory = requestFactory;
		_dispatcher = dispatcher;
		_faveListService = faveListService;

		getView().setUiHandlers(this);
	}

	@Override
	public void onHide() {
		super.onHide();
		getView().setWhylines(new ArrayList<WhylineProxy>());
		getView().setUserLists(new ArrayList<UserListResultDto>());
	}

	public void showWhylines(FaveItemDto song) {
		final Request<List<WhylineProxy>> whylineReq = _requestFactory.whylineRequest().getWhylinesForSong(song.getId());
		whylineReq.fire(new Receiver<List<WhylineProxy>>() {
			@Override
			public void onSuccess(final List<WhylineProxy> whylines) {
				getView().setWhylines(whylines);
				/*if (whylineList.size() == 0) {
					final Label label = new Label("No whylines yet");
					label.addStyleName(style.noWhyline());
					whylines.add(label);
				}*/
			}
		});

		_dispatcher.execute(_faveListService.getListsContainingSong(song.getId()), new AsyncCallback<UserListResultCollection>() {

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
