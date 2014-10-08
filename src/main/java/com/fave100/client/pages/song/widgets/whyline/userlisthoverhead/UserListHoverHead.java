package com.fave100.client.pages.song.widgets.whyline.userlisthoverhead;

import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserListHoverHead extends Composite {

	private static UserListHoverHeadUiBinder uiBinder = GWT.create(UserListHoverHeadUiBinder.class);

	interface UserListHoverHeadUiBinder extends UiBinder<Widget, UserListHoverHead> {
	}

	@UiField FocusPanel container;
	@UiField Image avatarImage;
	@UiField FocusPanel details;
	@UiField Label userNameLabel;
	@UiField Panel listContainer;

	public UserListHoverHead() {
		initWidget(uiBinder.createAndBindUi(this));
		hideDetails();
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

		// Needs to escape from the container or it will get clipped
		details.removeFromParent();
		RootPanel.get().add(details);
	}

	@UiHandler("container")
	void onContainerMouseOver(MouseOverEvent event) {
		showDetails();
	}

	@UiHandler("container")
	void onContainerMouseOut(MouseOutEvent event) {
		hideDetails();
	}

	@UiHandler("details")
	void onDetailsMouseOver(MouseOverEvent event) {
		showDetails();
	}

	@UiHandler("details")
	void onDetailsMouseOut(MouseOutEvent event) {
		hideDetails();
	}

	private void showDetails() {
		details.setVisible(true);

		int maxWidth = userNameLabel.getOffsetWidth();
		Iterator<Widget> widgetIterator = listContainer.iterator();
		while (widgetIterator.hasNext()) {
			Widget widget = widgetIterator.next();
			maxWidth = Math.max(maxWidth, widget.getElement().getFirstChildElement().getOffsetWidth());
			maxWidth = Math.max(maxWidth, 100);
		}
		details.setWidth(maxWidth + "px");

		details.getElement().getStyle().setTop(getWidget().getAbsoluteTop() - details.getOffsetHeight(), Unit.PX);
		details.getElement().getStyle().setLeft(getWidget().getAbsoluteLeft() - details.getOffsetWidth() / 2 + avatarImage.getOffsetWidth() / 2, Unit.PX);
	}

	private void hideDetails() {
		details.setVisible(false);
	}

}
