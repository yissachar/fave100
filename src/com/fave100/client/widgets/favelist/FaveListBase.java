package com.fave100.client.widgets.favelist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;

public class FaveListBase extends FlowPanel {

	protected List<HasCell<FaveItemProxy, ?>> _cells = new ArrayList<HasCell<FaveItemProxy,?>>();
	protected CellList<FaveItemProxy> _cellList;


	public FaveListBase(final ApplicationRequestFactory requestFactory) {
//		super("");

		createCellList();
	}

	public void createCellList() {
		final CompositeCell<FaveItemProxy> cell = new CompositeCell<FaveItemProxy>(_cells);
		createCellList(cell);
	}

	public Element createCellList(final Cell<FaveItemProxy> cell) {
		clear();
		_cellList = new CellList<FaveItemProxy>(cell);
		add(_cellList);
		_cellList.getRowContainer().addClassName("faveList");
		return _cellList.getRowContainer();
	}

	public void setRowData(final List<FaveItemProxy> data) {
		_cellList.setRowData(data);
	}
}
