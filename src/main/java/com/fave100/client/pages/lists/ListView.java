package com.fave100.client.pages.lists;

import java.util.HashMap;
import java.util.Map;

import com.fave100.client.CurrentUser;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.pages.PageView;
import com.fave100.client.resources.css.GlobalStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class ListView extends PageView<ListUiHandlers>
		implements ListPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ListView> {
	}

	interface ListStyle extends GlobalStyle {
		String fixedSearch();

		String hoverSideBar();
	}

	@UiField ListStyle style;
	@UiField HTMLPanel userContainer;
	@UiField Panel tagline;
	@UiField HTMLPanel userPageSideBar;
	@UiField HTMLPanel faveListContainer;
	@UiField HTMLPanel globalListDetailsContainer;
	@UiField HTMLPanel listManager;
	@UiField FocusPanel followCTAcontainer;
	@UiField Label followCTA;
	@UiField HTMLPanel userPageFaveList;
	@UiField HTMLPanel followingContainer;
	@UiField FlowPanel userProfile;
	@UiField Hyperlink profileLink;
	@UiField Image avatar;
	@UiField Label username;
	@UiField HTMLPanel favelist;
	@UiField Label userNotFound;
	private boolean following;

	@Inject
	public ListView(final Binder binder) {
		widget = binder.createAndBindUi(this);

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		resize();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		HTMLPanel slotPanel = getPanelForSlot(slot);

		if (slotPanel != null && content != null) {
			slotPanel.clear();
			slotPanel.add(content);
		}
	}

	private HTMLPanel getPanelForSlot(Object slot) {
		Map<Type<RevealContentHandler<?>>, HTMLPanel> slotMap = new HashMap<Type<RevealContentHandler<?>>, HTMLPanel>();
		slotMap.put(ListPresenter.FAVELIST_SLOT, favelist);
		slotMap.put(ListPresenter.STARRED_LISTS_SLOT, followingContainer);
		slotMap.put(ListPresenter.LIST_MANAGER_SLOT, listManager);
		slotMap.put(ListPresenter.GLOBAL_LIST_DETAILS_SLOT, globalListDetailsContainer);

		return slotMap.get(slot);
	}

	@UiHandler("followCTAcontainer")
	void onStarClick(final ClickEvent event) {
		getUiHandlers().followUser();
		if (!following) {
			followCTA.removeStyleName("button-warning");
		}
	}

	@UiHandler("followCTA")
	void onFollowOver(final MouseOverEvent event) {
		if (following) {
			followCTA.addStyleName("button-warning");
			followCTA.setText("Unfollow");
		}
	}

	@UiHandler("followCTA")
	void onFollowOut(final MouseOutEvent event) {
		followCTA.removeStyleName("button-warning");
		if (following) {
			followCTA.setText("Following");
		}
	}

	@UiHandler("registerLink")
	void onRegisterLinkClick(final ClickEvent event) {
		getUiHandlers().showRegister();
	}

	public native void nativeRenderShare() /*-{
		$wnd.FB.XFBML.parse();
		$wnd.twttr.widgets.load();
		$wnd.gapi.plusone.go();
	}-*/;

	@Override
	public void setPageDetails(final AppUser requestedUser, final CurrentUser currentUser) {
		tagline.setVisible(false);

		if (requestedUser == null) {
			userProfile.setVisible(false);

			// Only show call action to users who are not logged in
			if (!currentUser.isLoggedIn()) {
				tagline.setVisible(true);
			}
		}
		else {
			userProfile.setVisible(true);
			avatar.setUrl(requestedUser.getAvatarImage());
			username.setText(requestedUser.getUsername());
			profileLink.setText(requestedUser.getUsername());
		}

		if (currentUser.isLoggedIn() && currentUser.equals(requestedUser)) {
			showOwnPage();
		}
		else {
			showOtherPage();
		}
	}

	private void resize() {
		userPageSideBar.setHeight((Window.getClientHeight() - 50) + "px");
	}

	private void showOwnPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(false);
		profileLink.setVisible(true);
	}

	private void showOtherPage() {
		userContainer.setVisible(true);
		userNotFound.setVisible(false);
		username.setVisible(true);
		profileLink.setVisible(false);
	}

	@Override
	public String getFixedSearchStyle() {
		return style.fixedSearch();
	}

	@Override
	public void showUserNotFound() {
		userContainer.setVisible(false);
		userNotFound.setVisible(true);
	}

	@Override
	public void setFollowCTA(final boolean show, final boolean following) {
		this.following = following;

		if (show) {
			followCTAcontainer.setVisible(true);
		}
		else {
			followCTAcontainer.setVisible(false);
		}

		if (following) {
			followCTA.setText("Following");
		}
		else {
			followCTA.setText("Follow");
		}
	}

	@Override
	public void toggleSideBar() {
		if (userContainer.getStyleName().contains(style.hoverSideBar())) {
			userContainer.removeStyleName(style.hoverSideBar());
		}
		else {
			userContainer.addStyleName(style.hoverSideBar());
		}
	}
}
