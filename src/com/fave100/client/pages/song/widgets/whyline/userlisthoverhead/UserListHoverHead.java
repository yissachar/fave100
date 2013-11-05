package com.fave100.client.pages.song.widgets.whyline.userlisthoverhead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserListHoverHead extends Composite {

	private static UserListHoverHeadUiBinder uiBinder = GWT.create(UserListHoverHeadUiBinder.class);

	interface UserListHoverHeadUiBinder extends UiBinder<Widget, UserListHoverHead> {
	}

	@UiField Image avatarImage;
	@UiField FlowPanel details;
	@UiField Label userNameLabel;
	@UiField Hyperlink listNameLink;

	public UserListHoverHead() {
		initWidget(uiBinder.createAndBindUi(this));
		//details.setVisible(false);
	}

	public UserListHoverHead(String userName, String listName, String listPlaceToken, String avatar) {
		this();
		userNameLabel.setText(userName);
		listNameLink.setText(listName);
		listNameLink.setTargetHistoryToken(listPlaceToken);
		avatarImage.setUrl(avatar);
	}

	/*@UiHandler("avatarImage")
	void onAvatarMouseOver(MouseOverEvent event) {
		details.setVisible(true);
		//details.getElement().setAttribute("left", avatarImage.getParent().getAbsoluteLeft() + "px");
	}

	@UiHandler("avatarImage")
	void onAvatarMouseOout(MouseOutEvent event) {
		details.setVisible(false);
	}*/

}
