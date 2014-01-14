package com.fave100.client.pagefragments.login.aboutpopup;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class AboutPopupPresenter extends PresenterWidget<AboutPopupPresenter.MyView> implements AboutPopupUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<AboutPopupUiHandlers> {
		void loadImages();
	}

	@Inject
	AboutPopupPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {
		getView().loadImages();
	}

}
