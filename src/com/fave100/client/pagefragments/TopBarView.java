package com.fave100.client.pagefragments;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TopBarView extends ViewImpl implements TopBarPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, TopBarView> {
	}

	@Inject
	public TopBarView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@UiField SpanElement logInLogOutLink;

	@Override
	public SpanElement getLogInLogOutLink() {
		return logInLogOutLink;
	}
	
	@UiField SpanElement greeting;

	@Override
	public SpanElement getGreeting() {
		return greeting;
	}
	
	@UiField InlineHyperlink myFave100Link;

	@Override
	public InlineHyperlink getMyFave100Link() {
		return myFave100Link;
	}
	
	@UiField InlineHyperlink registerLink;
	
	@Override 
	public InlineHyperlink getRegisterLink() {
		return registerLink;
	}
}
