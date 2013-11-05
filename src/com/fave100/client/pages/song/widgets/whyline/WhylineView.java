package com.fave100.client.pages.song.widgets.whyline;

import java.util.List;

import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.pages.song.widgets.whyline.userlisthoverhead.UserListHoverHead;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.UserListResultProxy;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class WhylineView extends ViewWithUiHandlers<WhylineUiHandlers> implements WhylinePresenter.MyView {
	public interface Binder extends UiBinder<FlowPanel, WhylineView> {
	}

	@UiField FlowPanel whylinePanel;
	@UiField FlowPanel userListsPanel;

	@Inject
	WhylineView(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setWhylines(List<WhylineProxy> whylines) {
		whylinePanel.clear();
		for (WhylineProxy whyline : whylines) {
			whylinePanel.add(new WhylineWidget(whyline));
		}
	}

	@Override
	public void setUserLists(List<UserListResultProxy> userLists) {
		userListsPanel.clear();
		for (UserListResultProxy userList : userLists) {
			String listPlaceToken = new ParameterTokenFormatter()
					.toPlaceToken(new PlaceRequest.Builder()
							.nameToken(NameTokens.lists)
							.with(ListPresenter.USER_PARAM, userList.getUserName())
							.with(ListPresenter.LIST_PARAM, userList.getListName())
							.build());
			userListsPanel.add(new UserListHoverHead(userList.getUserName(), userList.getListName(), listPlaceToken, userList.getAvatar()));
		}

	}
}