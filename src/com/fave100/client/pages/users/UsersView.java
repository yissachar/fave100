package com.fave100.client.pages.users;

import java.util.List;

import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.widgets.NonpersonalFaveList;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class UsersView extends ViewWithUiHandlers<UsersUiHandlers>
	implements UsersPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UsersView> {		
	}
	
	@UiField(provided = true) NonpersonalFaveList userFaveList;
	@UiField HTMLPanel userProfile;	
	@UiField Button followButton;		
	@UiField Image avatar;	
	@UiField SpanElement username;
	@UiField Anchor fave100TabLink;
	@UiField Anchor activityTabLink;
	@UiField InlineHTML activityTab;

	@Inject
	public UsersView(final Binder binder, final ApplicationRequestFactory requestFactory) {
		userFaveList = new NonpersonalFaveList(requestFactory);
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == UsersPresenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@UiHandler("followButton")
	void onFollowButtonClicked(final ClickEvent event) {
		getUiHandlers().follow();
	}
	
	@UiHandler("fave100TabLink")
	void onFave100TabClicked(final ClickEvent event) {
		getUiHandlers().goToFave100Tab();
	}
	
	@UiHandler("activityTabLink")
	void onActivityTabClicked(final ClickEvent event) {
		getUiHandlers().goToActivityTab();
	}

	@Override
	public void setFollowed() {
		followButton.setHTML("Following");
		followButton.setEnabled(false);
	}

	@Override
	public void setUnfollowed() {
		followButton.setHTML("Follow");
		followButton.setEnabled(true);
	}

	@Override
	public void showActivityTab(final SafeHtml html) {
		activityTab.setVisible(true);
    	userFaveList.setVisible(false);
    	fave100TabLink.removeStyleName("selected");
		activityTabLink.addStyleName("selected");
		activityTab.setHTML(html);
	}

	@Override
	public void showFave100Tab() {
		userFaveList.setVisible(true);
    	activityTab.setVisible(false);
    	activityTabLink.removeStyleName("selected");
		fave100TabLink.addStyleName("selected");
	}

	@Override
	public void setUserProfile(final AppUserProxy user) {
		avatar.setUrl(user.getAvatar());
		username.setInnerText(user.getUsername());
	}

	@Override
	public void setUserFaveList(final List<SongProxy> faveList) {
		userFaveList.setRowData(faveList);
	}
	
}
