package com.fave100.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class Icon extends Widget implements HasClickHandlers {

	private static IconUiBinder uiBinder = GWT.create(IconUiBinder.class);

	interface IconUiBinder extends UiBinder<Widget, Icon> {
	}

	public Icon() {
		uiBinder.createAndBindUi(this);
		setElement(Document.get().createElement("i"));
		getElement().addClassName("fa");
	}

	public Icon(String type) {
		this();
		getElement().addClassName(type);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
