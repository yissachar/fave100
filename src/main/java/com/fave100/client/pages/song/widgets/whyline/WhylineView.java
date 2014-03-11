package com.fave100.client.pages.song.widgets.whyline;

import java.util.List;

import com.fave100.client.generated.entities.UserListResult;
import com.fave100.client.generated.entities.Whyline;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.pages.song.widgets.whyline.userlisthoverhead.UserListHoverHead;
import com.fave100.client.place.NameTokens;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class WhylineView extends ViewWithUiHandlers<WhylineUiHandlers> implements WhylinePresenter.MyView {
	public interface Binder extends UiBinder<FlowPanel, WhylineView> {
	}

	@UiField FlowPanel whylinePanel;
	@UiField FlowPanel userListsPanel;

	private ParameterTokenFormatter _parameterTokenFormatter;

	@Inject
	WhylineView(Binder binder, ParameterTokenFormatter parameterTokenFormatter) {
		initWidget(binder.createAndBindUi(this));
		_parameterTokenFormatter = parameterTokenFormatter;

	}

	@Override
	public void setWhylines(List<Whyline> whylines) {
		whylinePanel.clear();
		for (Whyline whyline : whylines) {
			whylinePanel.add(new WhylineWidget(whyline));
		}
	}

	@Override
	public void setUserLists(List<UserListResult> userLists) {
		userListsPanel.clear();
		for (UserListResult userList : userLists) {
			String listPlaceToken = _parameterTokenFormatter
					.toPlaceToken(new PlaceRequest.Builder()
							.nameToken(NameTokens.lists)
							.with(ListPresenter.USER_PARAM, userList.getUserName())
							.with(ListPresenter.LIST_PARAM, userList.getListName())
							.build());
			userListsPanel.add(new UserListHoverHead(userList.getUserName(), userList.getListName(), listPlaceToken, userList.getAvatar()));
		}

	}
}