package com.fave100.client.pages.song.widgets.youtube;

import java.util.List;

import com.fave100.client.events.song.YouTubePlayerEndedEvent;
import com.fave100.shared.requestfactory.YouTubeSearchResultProxy;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

public class YouTubePresenter extends PresenterWidget<YouTubePresenter.MyView>
		implements YouTubeUiHandlers {

	public interface MyView extends View, HasUiHandlers<YouTubeUiHandlers> {
		void setVideoData(List<YouTubeSearchResultProxy> videos);

		void clearVideo();
	}

	private EventBus _eventBus;

	@Inject
	public YouTubePresenter(
							final EventBus eventBus,
							final MyView view) {
		super(eventBus, view);
		_eventBus = eventBus;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	public void setYouTubeVideos(final List<YouTubeSearchResultProxy> videos) {
		getView().setVideoData(videos);
	}

	public void clearVideo() {
		getView().clearVideo();
	}

	@Override
	public void dispatchEndedEvent() {
		_eventBus.fireEvent(new YouTubePlayerEndedEvent());
	}
}

interface YouTubeUiHandlers extends UiHandlers {
	void dispatchEndedEvent();
}