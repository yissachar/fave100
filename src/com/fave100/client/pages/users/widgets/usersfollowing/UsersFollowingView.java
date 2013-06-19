package com.fave100.client.pages.users.widgets.usersfollowing;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class UsersFollowingView extends ViewImpl implements UsersFollowingPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UsersFollowingView> {
	}

	public interface UsersFollowingStyle extends CssResource {
		String listLink();

		String deleteButton();
	}

	@UiField UsersFollowingStyle style;
	@UiField FlowPanel listContainer;

	@Inject
	public UsersFollowingView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setStarredLists(final FlowPanel flowPanel) {
		listContainer.clear();
		listContainer.add(flowPanel);
	}

	@Override
	public UsersFollowingStyle getStyle() {
		return style;
	}
}
