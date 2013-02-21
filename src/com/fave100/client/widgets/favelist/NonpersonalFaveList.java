package com.fave100.client.widgets.favelist;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;

public class NonpersonalFaveList extends FaveListBase{

	public NonpersonalFaveList(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);


		_cells.add(new HasCell<FaveItemProxy, FaveItemProxy>() {
			private final NonpersonalFaveListCell cell = new NonpersonalFaveListCell(requestFactory);

            @Override
            public Cell<FaveItemProxy> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveItemProxy, FaveItemProxy> getFieldUpdater() {
				return null;
			}

			@Override
			public FaveItemProxy getValue(final FaveItemProxy object) {
				return object;
			}
		});

		createCellList();
	}
}
