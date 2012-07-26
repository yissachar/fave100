package com.fave100.client.pages.register;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class RegisterView extends ViewImpl implements RegisterPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, RegisterView> {
	}

	@Inject
	public RegisterView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField HTMLPanel topBar;
	
	@Override
	public void setInSlot(Object slot, Widget content) {
		if(slot == RegisterPresenter.TOP_BAR_SLOT) {
			topBar.clear();
			
			if(content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}
	
	@UiField HTMLPanel signInWithGoogleSpan;

	@Override
	public HTMLPanel getSignInWithGoogleSpan() {
		return signInWithGoogleSpan;
	}

	@UiField HTMLPanel registerContainer;
	
	@Override
	public HTMLPanel getRegisterContainer() {
		return registerContainer;
	}
}
