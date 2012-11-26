package com.fave100.client.pagefragments.favefeed;

import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class FaveFeedPresenter extends
		PresenterWidget<FaveFeedPresenter.MyView> {

	public interface MyView extends View {
		void setFaveFeedContent(List<String> activityList);
	}

	private ApplicationRequestFactory requestFactory;

	@Inject
	public FaveFeedPresenter(final EventBus eventBus, final MyView view,
			final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		this.requestFactory = requestFactory;
	}

	@Override
	protected void onBind() {
		super.onBind();

	}

	@Override
	protected void onReveal() {
		super.onReveal();

		// Update the FaveFeed
		final Request<List<String>> faveFeedReq = requestFactory.appUserRequest().getFaveFeedForCurrentUser();
		faveFeedReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(final List<String> faveFeed) {
				getView().setFaveFeedContent(faveFeed);
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				getView().setFaveFeedContent(null);
			}
		});
	}
}
