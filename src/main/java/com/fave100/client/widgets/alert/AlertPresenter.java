package com.fave100.client.widgets.alert;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class AlertPresenter extends PresenterWidget<AlertPresenter.MyView> implements AlertUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<AlertUiHandlers> {
	}

	private AlertCallback _alertCallback;

	@Inject
	AlertPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);

		getView().setUiHandlers(this);
	}

	@Override
	public void cancel() {
		_alertCallback.onCancel();
	}

	@Override
	public void ok() {
		_alertCallback.onOk();
	}

	public void setAlertCallback(AlertCallback callback) {
		_alertCallback = callback;
	}

}
