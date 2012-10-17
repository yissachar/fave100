package com.fave100.client.widgets;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;

public class NonpersonalFaveList extends FaveListBase{

	public NonpersonalFaveList(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);	
		
		
		_cells.add(new HasCell<SongProxy, SongProxy>() {
			private final NonpersonalFaveListCell cell = new NonpersonalFaveListCell(requestFactory);

            @Override
            public Cell<SongProxy> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SongProxy> getFieldUpdater() {
				return null;
			}

			@Override
			public SongProxy getValue(final SongProxy object) {
				return object;
			}
		});
		
		createCellList();
	}	
}
