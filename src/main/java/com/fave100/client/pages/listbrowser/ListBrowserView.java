package com.fave100.client.pages.listbrowser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.pages.PageView;
import com.fave100.client.resources.css.GlobalStyle;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class ListBrowserView extends PageView<ListBrowserUiHandlers> implements ListBrowserPresenter.MyView {

	interface Binder extends UiBinder<Widget, ListBrowserView> {
	}

	interface ListBrowserStyle extends GlobalStyle {
		String letterHeading();

		String nameContainer();

		String clear();
	}

	@UiField ListBrowserStyle style;
	@UiField Panel listContainer;
	@UiField Label errorMsg;

	private ParameterTokenFormatter _parameterTokenFormatter;

	@Inject
	ListBrowserView(Binder uiBinder, ParameterTokenFormatter parameterTokenFormatter) {
		initWidget(uiBinder.createAndBindUi(this));
		_parameterTokenFormatter = parameterTokenFormatter;
	}

	@Override
	public void setLists(List<StringResult> lists) {
		clear();

		// Map list names to their buckets
		LinkedHashMap<String, List<StringResult>> buckets = new LinkedHashMap<String, List<StringResult>>();
		for (StringResult list : lists) {
			String startingLetter = list.getValue().substring(0, 1);
			try {
				Integer.parseInt(startingLetter);
				startingLetter = "#";
			}
			catch (Exception e) {
				// Starting letter not a number, leave as is
			}

			if (!buckets.containsKey(startingLetter)) {
				buckets.put(startingLetter.toUpperCase(), new ArrayList<StringResult>());
			}

			buckets.get(startingLetter.toUpperCase()).add(list);
		}

		for (Map.Entry<String, List<StringResult>> entry : buckets.entrySet()) {

			Label letterHeading = new Label(entry.getKey());
			letterHeading.addStyleName(style.letterHeading());
			listContainer.add(letterHeading);

			FlowPanel nameContainer = new FlowPanel();
			nameContainer.addStyleName(style.nameContainer());

			int i = -1;
			for (StringResult list : entry.getValue()) {
				InlineHyperlink listName = new InlineHyperlink();
				listName.setText(list.getValue());
				listName.setTargetHistoryToken(_parameterTokenFormatter.toPlaceToken(
						new PlaceRequest.Builder()
								.nameToken(NameTokens.lists)
								.with(PlaceParams.LIST_PARAM, list.getValue())
								.build())
						.toString());
				nameContainer.add(listName);

				i++;
				if (i == 4) {
					listName.addStyleName(style.clear());
					i = 0;
				}
			}

			listContainer.add(nameContainer);
		}
	}

	@Override
	public void clear() {
		listContainer.clear();
		setError("");
	}

	@Override
	public void setError(String error) {
		errorMsg.setText(error);
	}
}
