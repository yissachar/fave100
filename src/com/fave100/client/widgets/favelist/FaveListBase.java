package com.fave100.client.widgets.favelist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.HTMLPanel;

public class FaveListBase extends HTMLPanel{
	
	protected List<HasCell<SongProxy, ?>> _cells = new ArrayList<HasCell<SongProxy,?>>();
	protected CellList<SongProxy> _cellList;
	
	public FaveListBase(final ApplicationRequestFactory requestFactory) {
		super("");
		
		createCellList();
	}
	
	public void createCellList() {		
		final CompositeCell<SongProxy> cell = new CompositeCell<SongProxy>(_cells);
		createCellList(cell);
	}
	
	public Element createCellList(final Cell<SongProxy> cell) {
		clear();
		_cellList = new CellList<SongProxy>(cell);
		add(_cellList);	
		_cellList.getRowContainer().addClassName("faveList");
		return _cellList.getRowContainer();
	}
	
	public void setRowData(final List<SongProxy> data) {
		_cellList.setRowData(data);
	}
}
