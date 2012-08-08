package com.fave100.client.pages.logout;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LogoutView extends ViewImpl implements LogoutPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, LogoutView> {
	}

	@Inject
	public LogoutView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}
