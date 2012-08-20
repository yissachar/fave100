package com.fave100.client.widgets;

import com.fave100.client.requestfactory.FaveListItem;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.user.cellview.client.CellList;

public class FaveItemCellList extends CellList<FaveListItem>{
	
	public FaveItemCellList(CompositeCell<FaveListItem> cell){
		super(cell);
		//super(new FaveItemCell());
	}

}
