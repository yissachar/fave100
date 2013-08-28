package com.fave100.client.pages.users;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class UserView extends ViewWithUiHandlers<UsersUiHandlers>
		implements UserPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UserView> {
	}

	@Inject
	public UserView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}
