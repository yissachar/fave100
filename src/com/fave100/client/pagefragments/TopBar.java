package com.fave100.client.pagefragments;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TopBar extends Composite {

	private static TopBarUiBinder uiBinder = GWT.create(TopBarUiBinder.class);

	interface TopBarUiBinder extends UiBinder<Widget, TopBar> {
	}
	
	@UiField SpanElement logInLink;
	
	public TopBar() {		
		initWidget(uiBinder.createAndBindUi(this));
		//UserService userService = UserServiceFactory.getUserService();
		//String foo = userService.createLoginURL("/");
		//logInLink.setInnerHTML("<a href='"+userService.createLoginURL("/")+"'>Log in</a>");
		
	}
}
