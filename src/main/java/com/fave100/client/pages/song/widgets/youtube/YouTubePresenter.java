package com.fave100.client.pages.song.widgets.youtube;

import java.util.List;

import com.fave100.client.events.song.YouTubePlayerEndedEvent;
import com.fave100.client.generated.entities.YouTubeSearchResult;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

public class YouTubePresenter extends PresenterWidget<YouTubePresenter.MyView>
		implements YouTubeUiHandlers {

	public interface MyView extends View, HasUiHandlers<YouTubeUiHandlers> {
		void setVideoData(List<YouTubeSearchResult> videos);

		void clearVideo();

		void stopVideo();

		void toggleThumbs();

		void hideThumbs();
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

	public void setYouTubeVideos(final List<YouTubeSearchResult> videos) {
		getView().setVideoData(videos);
	}

	public void clearVideo() {
		getView().clearVideo();
	}

	public void stopVideo() {
		getView().stopVideo();
	}

	public void toggleThumbs() {
		getView().toggleThumbs();
	}

	public void hideThumbs() {
		getView().hideThumbs();
	}

	@Override
	public void dispatchEndedEvent() {
		_eventBus.fireEvent(new YouTubePlayerEndedEvent());
	}
}

interface YouTubeUiHandlers extends UiHandlers {
	void dispatchEndedEvent();
}