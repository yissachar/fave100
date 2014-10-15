package com.fave100.client.widgets.welcomeinfo;

import com.fave100.client.events.RegisterDialogRequestedEvent;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.Icon;
import com.fave100.shared.Constants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class WelcomeInfo extends Composite {

	private static WelcomeInfoUiBinder uiBinder = GWT.create(WelcomeInfoUiBinder.class);

	interface WelcomeInfoUiBinder extends UiBinder<Widget, WelcomeInfo> {
	}

	interface WelcomeInfoStyle extends GlobalStyle {
		String flyoutHidden();
	}

	@UiField WelcomeInfoStyle style;
	@UiField HTMLPanel infoFlyout;
	@UiField Icon infoIcon;
	@UiField Anchor registerLink;
	private EventBus _eventBus;

	public WelcomeInfo() {
		initWidget(uiBinder.createAndBindUi(this));

		// Hide welcome info if they've seen it already
		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			if (storage.getItem(Constants.SAW_WELCOME_INFO_STORAGE_KEY) != null) {
				infoFlyout.addStyleName(style.flyoutHidden());
			}
			else {
				storage.setItem(Constants.SAW_WELCOME_INFO_STORAGE_KEY, "true");
			}
		}
	}

	@UiHandler("infoIcon")
	void onInfoIconClicked(ClickEvent event) {
		if (infoFlyout.getStyleName().contains(style.flyoutHidden())) {
			infoFlyout.removeStyleName(style.flyoutHidden());
		}
		else {
			infoFlyout.addStyleName(style.flyoutHidden());
		}
	}

	@UiHandler("registerLink")
	void onRegisterLinkClicked(ClickEvent event) {
		_eventBus.fireEvent(new RegisterDialogRequestedEvent());
	}

	public void setEventBus(EventBus eventBus) {
		_eventBus = eventBus;
	}

}
