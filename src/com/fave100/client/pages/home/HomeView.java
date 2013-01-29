package com.fave100.client.pages.home;

import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.widgets.UserThumbList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

	private final Widget	widget;

	public interface Binder extends UiBinder<Widget, HomeView> {
	}

	@UiField
	HTMLPanel							userThumbListPanel;
	private ApplicationRequestFactory	requestFactory;

	@Inject
	public HomeView(final Binder binder,
			final ApplicationRequestFactory requestFactory) {
		widget = binder.createAndBindUi(this);
		this.requestFactory = requestFactory;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		super.setInSlot(slot, content);
	}

	@Override
	public void addUserThumb(final AppUserProxy appUser) {
		final UserThumbList userThumbList = new UserThumbList(requestFactory,
				appUser);
		userThumbListPanel.add(userThumbList);
	}
}
