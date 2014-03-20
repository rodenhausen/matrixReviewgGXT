package com.sencha.gxt.widget.core.client.grid;

import java.util.List;

import my.ex.client.ControlMode;
import my.ex.client.TaxonMatrixView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class MyGridView<M> extends GridView<M> {

	private TaxonMatrixView taxonMatrixView;
	private ColumnHeaderAppearance columnHeaderAppearance;
	private ColumnHeaderStyles columnHeaderStyles;

	public MyGridView(TaxonMatrixView taxonMatrixView) {
		this(GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class), taxonMatrixView);
	}

	public MyGridView(ColumnHeaderAppearance columnHeaderAppearance, TaxonMatrixView taxonMatrixView) {
		this.columnHeaderAppearance = columnHeaderAppearance;
		this.columnHeaderStyles = columnHeaderAppearance.styles();
		this.taxonMatrixView = taxonMatrixView;
	}

	@Override
	protected Menu createContextMenu(final int colIndex) {
		Menu menu = super.createContextMenu(colIndex);

		MenuItem item = new MenuItem();
		item.setText("Delete");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.deleteColumn(colIndex);
			}
		});
		menu.add(item);

		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(taxonMatrixView.isLocked(colIndex));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !taxonMatrixView.isLocked(colIndex);
				lockItem.setChecked(newValue);
				taxonMatrixView.setLocked(colIndex, newValue);
			}
		});
		menu.add(lockItem);
		
		final CheckMenuItem controlItem = new CheckMenuItem("Controlled");
		controlItem.setChecked(taxonMatrixView.isControlled(colIndex));
		controlItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean activate = !taxonMatrixView.isControlled(colIndex);
				if(activate) {
					ControlMode controlMode = taxonMatrixView.determineControlMode(colIndex);
					taxonMatrixView.setControlMode(colIndex, controlMode);
				} else {
					taxonMatrixView.setControlMode(colIndex, ControlMode.OFF);
				}
				controlItem.setChecked(activate);
			}
		});
		menu.add(controlItem);

		item = new MenuItem("Move after");
		menu.add(item);
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);

		item = new MenuItem("start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				cm.moveColumn(colIndex, 0);
			}
		});
		moveMenu.add(item);
		
		int cols = cm.getColumnCount();
		for (int i = 0; i < cols; i++) {
			final int theI = i;
			item = new MenuItem(cm.getColumnHeader(i).asString());
			item.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					cm.moveColumn(colIndex, theI + 1);
				}
			});
			moveMenu.add(item);
		}

		return menu;
	}
	
	/**
	 * Override mainly to render first column differently than others: No access
	 * to parent element (td of table) from Cell implementation (TaxonCell)
	 */
	@Override
	protected SafeHtml doRender(List<ColumnData> cs, List<M> rows, int startRow) {
		final int colCount = cm.getColumnCount();
		final int last = colCount - 1;

		int[] columnWidths = getColumnWidths();

		// root builder
		SafeHtmlBuilder buf = new SafeHtmlBuilder();

		final SafeStyles rowStyles = SafeStylesUtils
				.fromTrustedString("width: " + getTotalWidth() + "px;");

		final String unselectableClass = " " + unselectable;
		final String rowAltClass = " " + styles.rowAlt();
		final String rowDirtyClass = " " + styles.rowDirty();

		final String cellClass = styles.cell();
		/*System.out.println(styles.cellInner());
		System.out.println(styles.cell());
		System.out.println(styles.rowWrap());
		System.out.println(styles.rowBody());
		System.out.println(styles.rowBodyRow());
		System.out.println(styles.cellDirty());
		System.out.println(styles.rowDirty());
		*/
		final String headInnerClass = columnHeaderStyles.headInner();
		final String cellInnerClass = styles.cellInner();
		final String cellFirstClass = " x-grid-cell-first";
		final String cellLastClass = " x-grid-cell-last";
		final String cellDirty = " " + styles.cellDirty();

		final String rowWrap = styles.rowWrap();
		final String rowBody = styles.rowBody();
		final String rowBodyRow = styles.rowBodyRow();

		// loop over all rows
		for (int j = 0; j < rows.size(); j++) {
			M model = rows.get(j);

			ListStore<M>.Record r = ds.hasRecord(model) ? ds.getRecord(model)
					: null;

			int rowBodyColSpanCount = colCount;
			if (enableRowBody) {
				for (ColumnConfig<M, ?> c : cm.getColumns()) {
					if (c instanceof RowExpander) {
						rowBodyColSpanCount--;
					}
				}
			}

			int rowIndex = (j + startRow);

			String rowClasses = styles.row();

			if (!selectable) {
				rowClasses += unselectableClass;
			}
			if (isStripeRows() && ((rowIndex + 1) % 2 == 0)) {
				rowClasses += rowAltClass;
			}

			if (this.isShowDirtyCells() && r != null && r.isDirty()) {
				rowClasses += rowDirtyClass;
			}

			if (viewConfig != null) {
				rowClasses += " " + viewConfig.getRowStyle(model, rowIndex);
			}

			SafeHtmlBuilder trBuilder = new SafeHtmlBuilder();

			// loop each cell per row
			for (int i = 0; i < colCount; i++) {
				SafeHtml rv = getRenderedValue(rowIndex, i, model, r);
				ColumnConfig<M, ?> columnConfig = cm.getColumn(i);
				ColumnData columnData = cs.get(i);

				String cellClasses = "";
				//if( i != 0)
					cellClasses = cellClass;
				if (i == 0) {
					cellClasses += cellFirstClass;
				} else if (i == last) {
					cellClasses += cellLastClass;
				}
				
				String cellInnerClasses = "";
				//if (i == 0)
				//	cellInnerClasses = columnHeaderStyles.headInner();
				//if (i != 0)
				//	cellInnerClasses = cellInnerClass;
				if (columnConfig.getColumnTextClassName() != null) {
					cellInnerClasses += " "
							+ columnConfig.getColumnTextClassName();
				}

				String id = columnConfig.getColumnClassSuffix();

				if (columnData.getClassNames() != null) {
					cellClasses += " " + columnData.getClassNames();
				}

				if (id != null && !id.equals("")) {
					cellClasses += " x-grid-td-" + id;
				}

				if (this.isShowDirtyCells() && r != null
						&& r.getChange(columnConfig.getValueProvider()) != null) {
					cellClasses += cellDirty;
				}

				if (viewConfig != null) {
					cellClasses += " "
							+ viewConfig.getColStyle(model,
									cm.getValueProvider(i), rowIndex, i);
				}

				final SafeStyles cellStyles = columnData.getStyles();

				final SafeHtml tdContent;
				if (enableRowBody && i == 0) {
					tdContent = tpls.tdRowSpan(i, cellClasses, cellStyles,
							this.getRowBodyRowSpan(), rv);
				} else {
					tdContent = tpls.td(i, cellClasses, cellStyles,
							cellInnerClasses,
							columnConfig.getColumnTextStyle(), rv);
				}
				trBuilder.append(tdContent);
			}

			if (enableRowBody) {
				String cls = styles.dataTable() + " x-grid-resizer";

				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.append(tpls.tr("", trBuilder.toSafeHtml()));
				sb.appendHtmlConstant("<tr class=" + rowBodyRow
						+ "><td colspan=" + rowBodyColSpanCount
						+ "><div class=" + rowBody + "></div></td></tr>");

				buf.append(tpls.tr(rowClasses, tpls.tdWrap(colCount, "",
						rowWrap, tpls.table(cls, rowStyles, sb.toSafeHtml(),
								renderHiddenHeaders(columnWidths)))));

			} else {
				buf.append(tpls.tr(rowClasses, trBuilder.toSafeHtml()));
			}

		}
		// end row loop
		return buf.toSafeHtml();

	}
	
	/**
	 * Creates and initializes the column header and saves reference for future
	 * use.
	 */
	@Override
	protected void initColumnHeader() {
		header = new MyColumnHeader<M>(grid, cm, taxonMatrixView.getContainer()) {

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
	}

}
