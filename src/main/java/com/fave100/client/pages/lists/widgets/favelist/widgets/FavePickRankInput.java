package com.fave100.client.pages.lists.widgets.favelist.widgets;

import java.util.HashSet;

import com.fave100.client.events.favelist.RankInputUnfocusEvent;
import com.fave100.client.resources.css.GlobalStyle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class FavePickRankInput extends Composite {

	private static FavePickRankInputUiBinder uiBinder = GWT.create(FavePickRankInputUiBinder.class);

	interface FavePickRankInputUiBinder extends UiBinder<Widget, FavePickRankInput> {
	}

	interface FavePickRankInputStyle extends GlobalStyle {
		String rankEditTextBoxEditing();
	}

	@UiField FavePickRankInputStyle style;
	@UiField TextBox rankEditTextBox;
	private FavePickWidget _favePickWidget;
	private EventBus _eventBus;
	private HashSet<Integer> _permittedKeys = new HashSet<>();

	public FavePickRankInput(FavePickWidget favePickWidget, EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		_favePickWidget = favePickWidget;
		_eventBus = eventBus;

		String rank = Integer.toString(favePickWidget.getRank());
		rankEditTextBox.setText(rank);

		_permittedKeys.add(KeyCodes.KEY_TAB);
		_permittedKeys.add(KeyCodes.KEY_BACKSPACE);
		_permittedKeys.add(KeyCodes.KEY_ESCAPE);
		_permittedKeys.add(KeyCodes.KEY_DELETE);
		_permittedKeys.add(KeyCodes.KEY_ENTER);
		_permittedKeys.add(KeyCodes.KEY_HOME);
		_permittedKeys.add(KeyCodes.KEY_END);
		_permittedKeys.add(KeyCodes.KEY_UP);
		_permittedKeys.add(KeyCodes.KEY_DOWN);
		_permittedKeys.add(KeyCodes.KEY_LEFT);
		_permittedKeys.add(KeyCodes.KEY_RIGHT);
	}

	@UiHandler("rankEditTextBox")
	void onClick(final ClickEvent event) {
		rankEditTextBox.addStyleName(style.rankEditTextBoxEditing());
		rankEditTextBox.setFocus(true);
		rankEditTextBox.selectAll();
	}

	@UiHandler("rankEditTextBox")
	void onKeyDown(final KeyDownEvent event) {
		final int keyCode = event.getNativeKeyCode();

		// Only allow numbers and special keys
		if (event.getNativeEvent().getShiftKey() || (!isNumberKey(keyCode) && !_permittedKeys.contains(keyCode))) {
			event.preventDefault();
			event.stopPropagation();
		}

		if (keyCode == KeyCodes.KEY_ENTER) {
			_favePickWidget.updateRank(rankEditTextBox.getText());
			_eventBus.fireEvent(new RankInputUnfocusEvent());
		}
		else if (keyCode == KeyCodes.KEY_ESCAPE) {
			_eventBus.fireEvent(new RankInputUnfocusEvent());
		}
	}

	private boolean isNumberKey(int keyCode) {
		return (keyCode >= 48 && keyCode <= 57) || (keyCode >= 96 && keyCode <= 105);
	}

	@UiHandler("rankEditTextBox")
	void onBlur(final BlurEvent event) {
		rankEditTextBox.removeStyleName(style.rankEditTextBoxEditing());
		_favePickWidget.updateRank(rankEditTextBox.getText());
		_eventBus.fireEvent(new RankInputUnfocusEvent());
	}

	public void focus() {
		rankEditTextBox.fireEvent(new ClickEvent() {
		});
	}

}
