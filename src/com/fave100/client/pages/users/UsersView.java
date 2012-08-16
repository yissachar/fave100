package com.fave100.client.pages.users;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.widgets.FaveDataGrid;
import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UsersView extends ViewImpl implements UsersPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UsersView> {		
	}
	
	@UiField(provided = true) FaveDataGrid userFaveDataGrid;

	@Inject
	public UsersView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		userFaveDataGrid = new FaveDataGrid(requestFactory);
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == UsersPresenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@UiField HTMLPanel userProfile;

	@Override
	public HTMLPanel getUserProfile() {
		return userProfile;
	}
	
	@Override
	public FaveDataGrid getUserFaveDataGrid() {
		return userFaveDataGrid;
	}
	
	@UiField Button followButton;	

	@Override
	public Button getFollowButton() {
		return followButton;
	}
	
	@UiField Image avatar;

	@Override
	public Image getAvatar() {
		return avatar;
	}
	
	@UiField SpanElement username;

	@Override
	public SpanElement getUsernameSpan() {
		return username;
	}
	
	@UiField Anchor fave100TabLink;

	@Override
	public Anchor getFave100TabLink() {
		return fave100TabLink;
	}
	
	@UiField Anchor activityTabLink;

	@Override
	public Anchor getActivityTabLink() {
		return activityTabLink;
	}
	
	@UiField InlineHTML activityTab;

	@Override
	public InlineHTML getActivityTab() {
		return activityTab;
	}
	
}
