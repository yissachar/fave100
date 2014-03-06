package com.fave100.client.pagefragments.popups.addsong;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class AddSongView extends PopupViewWithUiHandlers<AddSongUiHandlers> implements AddSongPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, AddSongView> {
	}

	@UiField Label headerLabel;
	@UiField FlowPanel checkboxContainer;
	@UiField Button okButton;
	@UiField Button cancelButton;

	@Inject
	AddSongView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setListNames(List<String> listNames, String songName) {
		checkboxContainer.clear();
		for (String listName : listNames) {
			CheckBox checkBox = new CheckBox(listName);
			checkBox.setFormValue(listName);
			checkboxContainer.add(checkBox);
		}
		headerLabel.setText("Add \"" + songName + "\" to the following lists:");
		checkboxContainer.setHeight(String.valueOf(275 - headerLabel.getOffsetHeight()) + "px");
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("okButton")
	void onOkButtonClick(final ClickEvent event) {
		List<String> selectedLists = new ArrayList<String>();
		for (int i = 0; i < checkboxContainer.getWidgetCount(); i++) {
			Widget widget = checkboxContainer.getWidget(i);
			if (widget instanceof CheckBox) {
				if (((CheckBox)widget).getValue() == true)
					selectedLists.add(((CheckBox)widget).getFormValue());
			}
		}
		getUiHandlers().listsSelected(selectedLists);
		hide();
	}
}
