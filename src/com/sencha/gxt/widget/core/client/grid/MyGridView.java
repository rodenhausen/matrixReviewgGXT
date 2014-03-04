package com.sencha.gxt.widget.core.client.grid;

import my.ex.client.TaxonMatrixView;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class MyGridView<M> extends GridView<M> {

	private TaxonMatrixView taxonMatrixView;

	public MyGridView(TaxonMatrixView taxonMatrixView) {
		this.taxonMatrixView = taxonMatrixView;
	}

	/*@Override
	protected void initColumnHeader() {
		header = new MyColumnHeader<M>(grid, cm) {

			@Override
			protected Menu getContextMenu(int column) {
				return createContextMenu(column);
			}

			@Override
			protected void onColumnSplitterMoved(int colIndex, int width) {
				super.onColumnSplitterMoved(colIndex, width);
				MyGridView.this.onColumnSplitterMoved(colIndex, width);
			}

			@Override
			protected void onHeaderClick(Event ce, int column) {
				super.onHeaderClick(ce, column);
				MyGridView.this.onHeaderClick(column);
			}

			@Override
			protected void onKeyDown(Event ce, int index) {
				ce.stopPropagation();
				// auto select on key down
				if (grid.getSelectionModel() instanceof CellSelectionModel<?>) {
					CellSelectionModel<?> csm = (CellSelectionModel<?>) grid
							.getSelectionModel();
					csm.selectCell(0, index);
				} else {
					grid.getSelectionModel().select(0, false);
				}
			}

		};
		header.setSplitterWidth(splitterWidth);
		header.setMinColumnWidth(grid.getMinColumnWidth());
	}*/

	@Override
	protected Menu createContextMenu(final int colIndex) {
		Menu menu = super.createContextMenu(colIndex);
		/*final Menu menu = new Menu();

		if (cm.isSortable(colIndex)) {
			MenuItem item = new MenuItem();
			item.setText(DefaultMessages.getMessages().gridView_sortAscText());
			item.setIcon(header.getAppearance().sortAscendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					doSort(colIndex, SortDir.ASC);

				}
			});
			menu.add(item);

			item = new MenuItem();
			item.setText(DefaultMessages.getMessages().gridView_sortDescText());
			item.setIcon(header.getAppearance().sortDescendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					doSort(colIndex, SortDir.DESC);

				}
			});
			menu.add(item);
		}

		MenuItem columns = new MenuItem();
		columns.setText(DefaultMessages.getMessages().gridView_columnsText());
		columns.setIcon(header.getAppearance().columnsIcon());
		columns.setData("gxt-columns", "true");

		final Menu columnMenu = new Menu();

		int cols = cm.getColumnCount();
		for (int i = 0; i < cols; i++) {
			ColumnConfig<M, ?> config = cm.getColumn(i);
			if (shouldNotCount(i, false)) {
				continue;
			}
			final int fcol = i;
			final CheckMenuItem check = new CheckMenuItem();
			check.setHideOnClick(false);
			check.setText(cm.getColumnHeader(i).asString());
			check.setChecked(!cm.isHidden(i));

			if (!config.isHideable()) {
				check.disable();
			}

			check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

				@Override
				public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
					cm.setHidden(fcol, !cm.isHidden(fcol));
					restrictMenu(columnMenu);

				}
			});
			columnMenu.add(check);
		}

		restrictMenu(columnMenu);
		columns.setEnabled(columnMenu.getWidgetCount() > 0);
		columns.setSubMenu(columnMenu);
		menu.add(columns);*/
		
		
		MenuItem item = new MenuItem();
		item.setText("Delete");
		//item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.deleteColumn(colIndex);
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Lock");
		//item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.toggleEditing(colIndex);
			}
		});
		menu.add(item);
		
		return menu;
	}

}
