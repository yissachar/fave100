package com.fave100.client.pages.profile;

import com.fave100.client.pages.BasePresenter;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ProfileView extends ViewWithUiHandlers<ProfileUiHandlers> implements ProfilePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ProfileView> {
	}

	@UiField HTMLPanel topBar;
	@UiField FormPanel profileForm;
	
	@Inject
	public ProfileView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if(slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@Override
	public void createActionUrl(final String url) {
		profileForm.setAction(url);
	}
	
	@UiHandler("profileForm")
	public void onSubmit(final SubmitCompleteEvent event) {
		// TODO: Figure out why this doesn't work and put back in
		//Window.alert(event.getResults());
		// Strip the blob key of HTML tags and pass it to the Presenter
		//getUiHandlers().setUserAvatarBlobKey(event.getResults().replaceAll("\\<.*?\\>", ""));
	}
}
