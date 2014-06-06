package com.fave100.client.pages.song.widgets.whyline.userlisthoverhead;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class UserListHoverHead extends Composite {

	private static UserListHoverHeadUiBinder uiBinder = GWT.create(UserListHoverHeadUiBinder.class);

	interface UserListHoverHeadUiBinder extends UiBinder<Widget, UserListHoverHead> {
	}

	@UiField Image avatarImage;
	@UiField FlowPanel details;
	@UiField Label userNameLabel;
	@UiField Panel listContainer;

	public UserListHoverHead() {
		initWidget(uiBinder.createAndBindUi(this));
		//details.setVisible(false);
	}

	public UserListHoverHead(String userName, Map<String, String> listPlaces, String avatar) {
		this();
		userNameLabel.setText(userName);
		for (Map.Entry<String, String> listPlace : listPlaces.entrySet()) {
			Hyperlink listLink = new Hyperlink();
			listLink.setText(listPlace.getKey());
			listLink.setTargetHistoryToken(listPlace.getValue());
			listContainer.add(listLink);
		}
		avatarImage.setUrl(avatar);

		// TODO: Jun 3, 2014 Should find a better way to set top than hardcoded values
		details.getElement().getStyle().setTop(-(57 + 20 * listPlaces.size()), Unit.PX);

	}

}
