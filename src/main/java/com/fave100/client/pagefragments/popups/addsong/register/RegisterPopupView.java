package com.fave100.client.pagefragments.popups.addsong.register;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class RegisterPopupView extends PopupViewWithUiHandlers<RegisterPopupUiHandlers> implements RegisterPopupPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, RegisterPopupView> {
	}

	@UiField HTMLPanel registerContainer;

	@Inject
	RegisterPopupView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
		setAutoHideOnNavigationEventEnabled(true);
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		if (slot == RegisterPopupPresenter.REGISTER_SLOT) {
			registerContainer.clear();
			if (content != null) {
				registerContainer.add(content);
				registerContainer.addStyleName("fullLoginPage");
			}
		}
	}
}
