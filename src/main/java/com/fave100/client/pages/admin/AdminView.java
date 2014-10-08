package com.fave100.client.pages.admin;

import java.util.List;

import javax.inject.Inject;

import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.client.widgets.Icon;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

class AdminView extends ViewWithUiHandlers<AdminUiHandlers> implements AdminPresenter.MyView {

	interface Binder extends UiBinder<Widget, AdminView> {
	}

	public interface AdminStyle extends GlobalStyle {
		String adminWidget();
	}

	@UiField AdminStyle style;
	@UiField Panel adminPanel;
	@UiField Panel criticPanel;
	@UiField Panel listPanel;
	@UiField Button addAdminButton;
	@UiField Button addCriticButton;
	@UiField Button addListButton;

	@Inject
	AdminView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("addAdminButton")
	void onAddAdminButtonClicked(ClickEvent event) {
		getUiHandlers().showAddAdminPrompt();
	}

	@UiHandler("addCriticButton")
	void onAddCriticButtonClicked(ClickEvent event) {
		getUiHandlers().showAddCriticPrompt();
	}

	@UiHandler("addListButton")
	void onAddListButtonClicked(ClickEvent event) {
		getUiHandlers().showAddListPrompt();
	}

	@Override
	public void setAdmins(List<AppUser> admins) {
		adminPanel.clear();

		for (final AppUser admin : admins) {
			final FlowPanel adminWidget = new FlowPanel();

			Image userAvatar = new Image(admin.getAvatarImage());
			adminWidget.add(userAvatar);

			Label usernameLabel = new Label(admin.getUsername());
			adminWidget.add(usernameLabel);

			Icon deleteIcon = new Icon("fa-times");
			deleteIcon.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					adminPanel.remove(adminWidget);
					getUiHandlers().removeAdmin(admin);
				}
			});
			adminWidget.add(deleteIcon);

			adminWidget.addStyleName(style.adminWidget());
			adminPanel.add(adminWidget);
		}
	}

	@Override
	public void setCritics(List<AppUser> critics) {
		criticPanel.clear();

		for (final AppUser critic : critics) {
			final FlowPanel criticWidget = new FlowPanel();

			Image userAvatar = new Image(critic.getAvatarImage());
			criticWidget.add(userAvatar);

			Label usernameLabel = new Label(critic.getUsername());
			criticWidget.add(usernameLabel);

			Icon deleteIcon = new Icon("fa-times");
			deleteIcon.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					criticPanel.remove(criticWidget);
					getUiHandlers().removeCritic(critic);
				}
			});
			criticWidget.add(deleteIcon);

			criticWidget.addStyleName(style.adminWidget());
			criticPanel.add(criticWidget);
		}
	}

	@Override
	public void setFeaturedLists(List<String> lists) {
		listPanel.clear();

		for (final String list : lists) {
			final FlowPanel listWidget = new FlowPanel();

			Label listLabel = new Label(list);
			listWidget.add(listLabel);

			Icon deleteIcon = new Icon("fa-times");
			deleteIcon.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					listPanel.remove(listWidget);
					getUiHandlers().removeFeaturedList(list);
				}
			});
			listWidget.add(deleteIcon);

			listWidget.addStyleName(style.adminWidget());
			listPanel.add(listWidget);
		}
	}
}