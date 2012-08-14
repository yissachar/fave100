package com.fave100.client.pages.userlist;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserlistView extends ViewImpl implements UserlistPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, UserlistView> {
	}

	@Inject
	public UserlistView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == UserlistPresenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@UiField InlineHTML usersList;
	
	@Override
	public InlineHTML getUserList() {
		return usersList;
	}
}
