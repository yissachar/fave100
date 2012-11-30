package com.fave100.client.widgets;

import com.fave100.client.pages.song.YouTubeJSONItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class YouTubeWidget extends Composite {

	private static YouTubeWidgetUiBinder uiBinder = GWT
			.create(YouTubeWidgetUiBinder.class);

	interface YouTubeWidgetUiBinder extends UiBinder<Widget, YouTubeWidget> {
	}

	@UiField Frame youTubePlayer;

	public YouTubeWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setVideoData(final YouTubeJSONItem video) {
		youTubePlayer.setUrl("http://youtube.com/embed/"+video.getVideoId());
	}

}
