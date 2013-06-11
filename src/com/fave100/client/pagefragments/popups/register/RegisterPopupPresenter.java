package com.fave100.client.pagefragments.popups.register;

import com.fave100.client.pagefragments.register.RegisterWidgetPresenter;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class RegisterPopupPresenter extends PresenterWidget<RegisterPopupPresenter.MyView> {

	public interface MyView extends PopupView {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> REGISTER_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private RegisterWidgetPresenter registerContainer;

	@Inject
	public RegisterPopupPresenter(
									final EventBus eventBus,
									final MyView view) {
		super(eventBus, view);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(REGISTER_SLOT, registerContainer);
	}
}
