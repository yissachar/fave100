package com.fave100.client.widgets.alert;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class AlertView extends PopupViewWithUiHandlers<AlertUiHandlers> implements AlertPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, AlertView> {
	}

	@Inject
	AlertView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().cancel();
	}

	@UiHandler("okButton")
	void onOkButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().ok();
	}
}
