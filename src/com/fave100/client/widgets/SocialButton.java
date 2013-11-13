package com.fave100.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class SocialButton extends Composite implements HasClickHandlers {

	private static SocialButtonUiBinder uiBinder = GWT.create(SocialButtonUiBinder.class);

	interface SocialButtonUiBinder extends UiBinder<Widget, SocialButton> {
	}

	@UiField FocusPanel focusPanel;
	@UiField HTMLPanel buttonContainer;
	@UiField Image imgIcon;
	@UiField InlineLabel buttonLabel;
	private String href = "";

	@UiConstructor
	public SocialButton(ImageResource img, String text, String style) {
		initWidget(uiBinder.createAndBindUi(this));
		imgIcon.setResource(img);
		buttonLabel.setText(text);
		buttonContainer.addStyleName(style);
	}

	@UiHandler("focusPanel")
	public void onClick(ClickEvent event) {
		if (!href.isEmpty())
			Window.open(href, "_blank", "");
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setText(String text) {
		buttonLabel.setText(text);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

}