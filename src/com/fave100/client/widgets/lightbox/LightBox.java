package com.fave100.client.widgets.lightbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class LightBox extends PopupPanel {

	private static LighBoxUiBinder uiBinder = GWT.create(LighBoxUiBinder.class);

	interface LighBoxUiBinder extends UiBinder<Widget, LightBox> {
	}

	@UiField FocusPanel lightBoxBackground;
	@UiField HTMLPanel content;

	public LightBox() {
		add(uiBinder.createAndBindUi(this));
	}

	@UiChild(tagname = "content")
	public void addContent(Widget content, String width, String height) {
		this.content.add(content);
		this.content.setWidth(width);
		this.content.setHeight(height);
	}

	@UiHandler("lightBoxBackground")
	void onBackgroundClick(ClickEvent event) {
		hide();
	}

}
