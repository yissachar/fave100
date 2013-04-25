package com.fave100.client.pagefragments.popups.login;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

public class LoginPopupView extends PopupViewImpl implements LoginPopupPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, LoginPopupView> {
	}

	@UiField FocusPanel lightBoxBackground;
	@UiField HTMLPanel loginContainer;

	@Inject
	public LoginPopupView(final EventBus eventBus, final Binder binder) {
		super(eventBus);
		widget = binder.createAndBindUi(this);
		setAutoHideOnNavigationEventEnabled(true);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		super.setInSlot(slot, content);

		if (slot == LoginPopupPresenter.LOGIN_SLOT) {
			loginContainer.clear();
			if (content != null) {
				loginContainer.add(content);
				loginContainer.addStyleName("fullLoginPage");
			}
		}
	}

	@UiHandler("lightBoxBackground")
	void onBackgroundClick(final ClickEvent event) {
		hide();
	}
}
