package com.fave100.client.pages.lists.widgets.usersfollowing;

import java.util.List;

import com.fave100.shared.Constants;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.GssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class UsersFollowingView extends ViewWithUiHandlers<UsersFollowingUiHandlers> implements UsersFollowingPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UsersFollowingView> {
	}

	public interface UsersFollowingStyle extends GssResource {
		String listLink();

		String deleteButton();

		String nonMobileListContainer();
	}

	@UiField UsersFollowingStyle style;
	@UiField Label followingTitle;
	@UiField FlowPanel listContainer;
	@UiField Button moreFollowingButton;

	@Inject
	public UsersFollowingView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		if (Window.getClientWidth() > Constants.MOBILE_WIDTH_PX) {
			listContainer.addStyleName(style.nonMobileListContainer());
		}
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("moreFollowingButton")
	void onMoreFollowingClick(final ClickEvent event) {
		getUiHandlers().getMoreFollowing();
	}

	@Override
	public void setFollowing(final List<FlowPanel> flowPanels) {
		listContainer.clear();
		if (flowPanels == null || flowPanels.size() == 0) {
			followingTitle.setVisible(false);
			moreFollowingButton.setVisible(false);
		}
		else {
			followingTitle.setVisible(true);
			moreFollowingButton.setVisible(true);
			for (final FlowPanel flowPanel : flowPanels) {
				listContainer.add(flowPanel);
			}
		}
	}

	@Override
	public void addFollowing(final List<FlowPanel> flowPanels) {
		for (final FlowPanel flowPanel : flowPanels) {
			listContainer.add(flowPanel);
		}
	}

	@Override
	public void hideMoreFollowingButton() {
		moreFollowingButton.setVisible(false);
	}

	@Override
	public UsersFollowingStyle getStyle() {
		return style;
	}

	@Override
	public void show() {
		widget.setVisible(true);
	}

	@Override
	public void hide() {
		widget.setVisible(false);
	}
}
