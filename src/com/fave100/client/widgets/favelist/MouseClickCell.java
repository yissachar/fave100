package com.fave100.client.widgets.favelist;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class MouseClickCell extends AbstractCell<String>{

	public MouseClickCell() {
		super("click");
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		if(value == null) return;
		
	    SafeHtml safeValue = SafeHtmlUtils.fromString(value);
	    sb.append(safeValue);		
	}
}
