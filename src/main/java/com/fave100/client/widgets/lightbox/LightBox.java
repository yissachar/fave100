package com.fave100.client.widgets.lightbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class LightBox extends PopupPanel {

	private static LightBoxUiBinder uiBinder = GWT.create(LightBoxUiBinder.class);

	interface LightBoxUiBinder extends UiBinder<Widget, LightBox> {
	}

	@UiField HTMLPanel content;

	public LightBox() {
		add(uiBinder.createAndBindUi(this));
		setAnimationEnabled(false);
		setAutoHideEnabled(true);
		setGlassEnabled(true);
	}

	@UiChild(tagname = "content")
	public void addContent(Widget content, String width, String height) {
		this.content.add(content);
		this.content.setWidth(width);
		this.content.setHeight(height);

		this.getElement().getStyle().setProperty("maxWidth", "100%");
	}

	@Override
	public void show() {
		super.show();
		Document.get().getBody().getStyle().setOverflow(Overflow.HIDDEN);
	}

	@Override
	public void hide() {
		super.hide();
		Document.get().getBody().getStyle().setOverflow(Overflow.VISIBLE);
	}

}
