package com.fave100.client.pages.song.widgets.youtube;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.generated.entities.YouTubeSearchResult;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class YouTubeView extends ViewWithUiHandlers<YouTubeUiHandlers> implements YouTubePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, YouTubeView> {
	}

	@UiField Label errorMessage;
	@UiField SimplePanel framePanel;
	@UiField HTMLPanel thumbnailPanel;
	private ArrayList<Image> thumbList = new ArrayList<Image>();
	private List<YouTubeSearchResult> _videos;
	private int _timesSkipped = 0;

	@Inject
	public YouTubeView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		errorMessage.setVisible(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setVideoData(final List<YouTubeSearchResult> videos) {
		// Clear old data		
		thumbnailPanel.clear();
		thumbList.clear();
		_videos = videos;

		if (videos == null || videos.size() == 0) {
			errorMessage.setVisible(true);
			getUiHandlers().dispatchEndedEvent();
		}
		else {
			errorMessage.setVisible(false);
			createIframeScript(this, videos.get(0).getVideoId());
			for (int i = 1; i < videos.size(); i++) {
				final Image ytThumb = new Image();
				ytThumb.setUrl(videos.get(i).getThumbnail());
				ytThumb.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						_timesSkipped = 0;
						final YouTubeSearchResult currVideo = videos.get(0);
						final YouTubeSearchResult clickedVideo = videos.get(thumbList.indexOf(ytThumb) + 1);
						videos.set(0, clickedVideo);
						videos.set(thumbList.indexOf(ytThumb) + 1, currVideo);
						setVideoData(videos);
					}
				});
				thumbList.add(ytThumb);
				thumbnailPanel.add(ytThumb);
			}
		}
	}

	// Needed because IE will continue playing the video even if the frame is removed
	@Override
	public native void clearVideo() /*-{
		if ($wnd.player) {
			// Dumb hack to get IE to stop the video
			var src = $wnd.player.getIframe().src;
			$wnd.player.getIframe().src = "";
			$wnd.player.getIframe().src = src;
			$wnd.player.destroy();
			$wnd.videoCleared = true;
		}
	}-*/;

	// Load YouTube iframe API async
	public native void createIframeScript(YouTubeView widget, String videoID) /*-{
		var player;
		if (!$wnd.player) {
			$wnd.onYouTubeIframeAPIReady = function onYouTubeIframeAPIReady() {
				$wnd.createPlayer(videoID);
			}
		} else {
			if (!$wnd.videoCleared) {
				$wnd.player.loadVideoById({
					videoId : videoID
				});
			} else {
				// Need to recreate the YouTube iframe player from scratch
				$wnd.container = $doc.getElementById('ytcontainer');
				$wnd.ytplayer = $doc.getElementById('ytplayer');
				if (!$wnd.ytplayer) {
					$wnd.ytplayer = $doc.createElement('div');
					$wnd.ytplayer.id = 'ytplayer';
				}

				$wnd.container.appendChild($wnd.ytplayer);
				$wnd.createPlayer(videoID);
				$wnd.videoCleared = false;
			}
		}

		$wnd.createPlayer = function createPlayer(videoID) {
			$wnd.player = new $wnd.YT.Player('ytplayer', {
				height : '360',
				width : '640',
				videoId : videoID,
				playerVars : {
					wmode : 'transparent',
					autoplay : 1
				},
				events : {
					'onStateChange' : $wnd.onPlayerStateChange,
					'onError' : $wnd.onPlayerError
				}
			});
			$wnd.videoCleared = false;
		}

		$wnd.onPlayerStateChange = function onPlayerStateChange(event) {
			if (event.data == $wnd.YT.PlayerState.ENDED) {
				widget.@com.fave100.client.pages.song.widgets.youtube.YouTubeView::dispatchEndedEvent()();
			}
		}

		// On error, just skip to the next video
		$wnd.onPlayerError = function onPlayerError(event) {
			widget.@com.fave100.client.pages.song.widgets.youtube.YouTubeView::skipVideo()();
		}

		var tag = $doc.getElementById("yt_iframe_api");
		if (!tag) {
			tag = $doc.createElement('script');
			tag.id = "yt_iframe_api";
			tag.src = "https://www.youtube.com/iframe_api";
			var firstScriptTag = $doc.getElementsByTagName('script')[0];
			firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
		}

	}-*/;

	private void dispatchEndedEvent() {
		_timesSkipped = 0;
		getUiHandlers().dispatchEndedEvent();
	}

	private void skipVideo() {
		if (_videos.size() >= 2) {
			final YouTubeSearchResult currVid = _videos.get(0);
			_timesSkipped++;
			// Had to skip video twice, jump to next song in list			
			if (_timesSkipped >= 2) {
				dispatchEndedEvent();
			}
			_videos.set(0, _videos.get(1));
			_videos.set(1, currVid);
			setVideoData(_videos);
		}
		else {
			dispatchEndedEvent();
		}
	}
}
