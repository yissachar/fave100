package com.fave100.client.pages.song.widgets.whyline;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.generated.entities.FaveItemDto;
import com.fave100.client.generated.entities.UserListResultCollection;
import com.fave100.client.generated.entities.UserListResultDto;
import com.fave100.client.generated.entities.WhylineCollection;
import com.fave100.client.generated.entities.WhylineDto;
import com.fave100.client.generated.services.FaveListService;
import com.fave100.client.generated.services.WhylineService;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class WhylinePresenter extends PresenterWidget<WhylinePresenter.MyView> implements WhylineUiHandlers {
	public interface MyView extends View, HasUiHandlers<WhylineUiHandlers> {
		void setWhylines(List<WhylineDto> whylines);

		void setUserLists(List<UserListResultDto> userLists);
	}

	private ApplicationRequestFactory _requestFactory;
	private DispatchAsync _dispatcher;
	private FaveListService _faveListService;
	private WhylineService _whylineService;

	@Inject
	WhylinePresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory, final DispatchAsync dispatcher, final FaveListService faveListService,
						final WhylineService whylineService) {
		super(eventBus, view);
		_requestFactory = requestFactory;
		_dispatcher = dispatcher;
		_faveListService = faveListService;
		_whylineService = whylineService;

		getView().setUiHandlers(this);
	}

	@Override
	public void onHide() {
		super.onHide();
		getView().setWhylines(new ArrayList<WhylineDto>());
		getView().setUserLists(new ArrayList<UserListResultDto>());
	}

	public void showWhylines(FaveItemDto song) {
		_dispatcher.execute(_whylineService.getSongWhylines(song.getId()), new AsyncCallback<WhylineCollection>() {

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
