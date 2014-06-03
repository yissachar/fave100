package com.fave100.client.pages.song.widgets.whyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.client.generated.entities.UserListResult;
import com.fave100.client.generated.entities.Whyline;
import com.fave100.client.pages.song.widgets.whyline.userlisthoverhead.UserListHoverHead;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class WhylineView extends ViewWithUiHandlers<WhylineUiHandlers> implements WhylinePresenter.MyView {
	public interface Binder extends UiBinder<FlowPanel, WhylineView> {
	}

	@UiField Panel userListsContainer;
	@UiField Panel whylineContainer;
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
		whylineContainer.setVisible(whylines.size() > 0);
		whylinePanel.clear();
		for (Whyline whyline : whylines) {
			whylinePanel.add(new WhylineWidget(whyline));
		}
	}

	@Override
	public void setUserLists(List<UserListResult> userLists) {
		userListsContainer.setVisible(userLists.size() > 0);
		userListsPanel.clear();

		HashMap<UserListResult, List<String>> userListMap = new HashMap<>();
		HashMap<String, UserListResult> nameToUserListMap = new HashMap<>();

		for (UserListResult userList : userLists) {

			if (nameToUserListMap.containsKey(userList.getUserName()) && userListMap.containsKey(nameToUserListMap.get(userList.getUserName()))) {
				userListMap.get(nameToUserListMap.get(userList.getUserName())).add(userList.getListName());
			}
			else {
				List<String> allLists = new ArrayList<>();
				allLists.add(userList.getListName());
				userListMap.put(userList, allLists);
				nameToUserListMap.put(userList.getUserName(), userList);
			}
		}

		for (Map.Entry<UserListResult, List<String>> userListEntry : userListMap.entrySet()) {
			Map<String, String> listPlaces = new HashMap<>();
			for (String list : userListEntry.getValue()) {
				String listPlaceToken = _parameterTokenFormatter
						.toPlaceToken(new PlaceRequest.Builder()
								.nameToken(NameTokens.lists)
								.with(PlaceParams.USER_PARAM, userListEntry.getKey().getUserName())
								.with(PlaceParams.LIST_PARAM, list)
								.build());
				listPlaces.put(list, listPlaceToken);
			}
			userListsPanel.add(new UserListHoverHead(userListEntry.getKey().getUserName(), listPlaces, userListEntry.getKey().getAvatar()));
		}

	}
}