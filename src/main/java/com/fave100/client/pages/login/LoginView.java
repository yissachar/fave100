package com.fave100.client.pages.login;

import com.fave100.client.pages.BasePresenter;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class LoginView extends ViewImpl implements LoginPresenter.MyView {

	private final Widget widget;
	@UiField HTMLPanel loginContainer;
	@UiField HTMLPanel topBar;

	public interface Binder extends UiBinder<Widget, LoginView> {
	}

	@Inject
	public LoginView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if (content != null) {
				topBar.add(content);
			}
		}

		if (slot == LoginPresenter.LOGIN_SLOT) {
			loginContainer.clear();
			if (content != null) {
				loginContainer.add(content);
				loginContainer.addStyleName("fullLoginPage");
			}
		}
	}
}
