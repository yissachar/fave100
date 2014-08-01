package com.fave100.client.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class Page extends Composite {

	private static PageUiBinder uiBinder = GWT.create(PageUiBinder.class);

	interface PageUiBinder extends UiBinder<Widget, Page> {
	}

	@UiField HTMLPanel content;

	public Page() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiChild(tagname = "content")
	public void addContent(Widget content) {
		this.content.clear();
		this.content.add(content);
	}
}
