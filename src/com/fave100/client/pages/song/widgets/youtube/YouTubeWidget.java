package com.fave100.client.pages.song.widgets.youtube;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class YouTubeWidget extends Composite {

	private static YouTubeWidgetUiBinder uiBinder = GWT
			.create(YouTubeWidgetUiBinder.class);

	interface YouTubeWidgetUiBinder extends UiBinder<Widget, YouTubeWidget> {
	}

	@UiField SimplePanel framePanel;
	@UiField HTMLPanel thumbnailPanel;
	private ArrayList<Image> thumbList = new ArrayList<Image>();
	private Frame youTubePlayer;

	public YouTubeWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setVideoData(final JsArray<YouTubeJSONItem> videos) {
		if (videos.length() == 0) {
			this.setVisible(false);
		}
		else {
			this.setVisible(true);
			// TODO: wmode transparent breaks some IE but needed to ensure embed respects z-index
			youTubePlayer = new Frame("http://youtube.com/embed/" + videos.get(0).getVideoId() + "?wmode=transparent");
			youTubePlayer.setWidth("640px");
			youTubePlayer.setHeight("360px");
			framePanel.clear();
			thumbnailPanel.clear();
			thumbList.clear();
			framePanel.add(youTubePlayer.asWidget());
			for (int i = 1; i < videos.length(); i++) {
				final Image ytThumb = new Image();
				ytThumb.setUrl(videos.get(i).getThumbnail());
				ytThumb.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						final YouTubeJSONItem currVideo = videos.get(0);
						final YouTubeJSONItem clickedVideo = videos.get(thumbList.indexOf(ytThumb) + 1);
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
	public void clearVideo() {
		youTubePlayer.setUrl("");
	}
}
