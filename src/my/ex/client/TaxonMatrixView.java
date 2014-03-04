package my.ex.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import my.ex.client.model.Character;
import my.ex.client.model.Taxon;
import my.ex.client.model.TaxonMatrix;
import my.ex.client.model.Value;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.MenuExtendedCell;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.grid.RowHeader;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import com.sencha.gxt.widget.core.client.form.Field;

public class TaxonMatrixView implements IsWidget {
	
	private TaxonMatrix taxonMatrix;
	private MyGrid<Taxon> grid;
	private GridInlineEditing<Taxon> editing;
	
	public TaxonMatrixView() {
		this.grid = createGrid();
	}
	
	private class TaxonModelKeyProvider implements ModelKeyProvider<Taxon> {
		@Override
		public String getKey(Taxon item) {
			return taxonMatrix.getId(item);
		}
	}

	public void init(final TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
		ListStore<Taxon> store = new ListStore<Taxon>(new TaxonModelKeyProvider());
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			store.add(taxon);
		}
		
		List<ColumnConfig<Taxon, ?>> l = new ArrayList<ColumnConfig<Taxon, ?>>();
		l.add(this.createNameColumnConfig());
		for (final Character character : taxonMatrix.getCharacters()) 
			l.add(createCharacterColumnConfig(character));
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(l);
		
		grid.reconfigure(store, cm);
		
		//set up editing
		editing = new GridInlineEditing<Taxon>(grid);
		for (ColumnConfig columnConfig : l) {
			this.enableEditing(columnConfig);
		}
	}

	private MyGrid<Taxon> createGrid() {
		MyGrid<Taxon> grid = new MyGrid<Taxon>(new ListStore<Taxon>(new TaxonModelKeyProvider()), new ColumnModel<Taxon>(new ArrayList<ColumnConfig<Taxon, ?>>()), this);
		grid.getView().setForceFit(true);
		grid.setColumnReordering(true);
		
		//set up row drag and drop for taxon move
		GridDragSource<Taxon> gds = new GridDragSource<Taxon>(grid);
		GridDropTarget<Taxon> target = new GridDropTarget<Taxon>(grid);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(true);
		

		
		// Filtering
		/*
		 * GridFilters<Taxon> filters = new GridFilters<Taxon>();
		 * filters.initPlugin(grid); filters.setLocal(true);
		 * 
		 * Filter<Taxon, String> taxonConceptFilter = new Filter<Taxon,
		 * String>(nameValueProvider) { protected TextField field;
		 * 
		 * @Override public List<FilterConfig> getFilterConfig() { FilterConfig
		 * cfg = createNewFilterConfig(); cfg.setType("string");
		 * cfg.setComparison("contains"); String valueToSend =
		 * field.getCurrentValue();
		 * cfg.setValue(getHandler().convertToString(valueToSend)); return
		 * Arrays.asList(cfg); }
		 * 
		 * @Override public Object getValue() { return field.getCurrentValue();
		 * }
		 * 
		 * @Override protected Class<String> getType() { return String.class; }
		 * }; filters.addFilter();
		 */
		/*grid.addRowClickHandler(new RowClickHandler() {
			@Override
			public void onRowClick(RowClickEvent event) {
				System.out.println("click");
				event.getEvent().preventDefault();
			}
		}); */
		
		return grid;
	}
	
	public void addTaxon(Taxon taxon) {
		this.taxonMatrix.addTaxon(taxon);
		grid.getStore().add(taxon);
	}
	
	public void removeTaxon(Taxon taxon) {
		this.taxonMatrix.removeTaxon(taxon);
		grid.getStore().remove(taxon);
	}
	
	public void addCharacter(Character character) {
		taxonMatrix.addCharacter(character);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = createCharacterColumnConfig(character);
		columns.add(columnConfig);
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		this.enableEditing(columnConfig);	
		grid.reconfigure(grid.getStore(), cm);
	}
	
	public void removeCharacter(int i) {
		taxonMatrix.removeCharacter(i);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = columns.remove(i + 1);
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		this.disableEditing(columnConfig);
		grid.reconfigure(grid.getStore(), cm);
	}

	@Override
	public Widget asWidget() {
		VerticalPanel panel = new VerticalPanel();		
		panel.add(grid);
		HorizontalPanel functionsPanel = new HorizontalPanel();
		panel.add(functionsPanel);
		
		Button addTaxonButton = new Button("Add Taxon");
		addTaxonButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTaxon(new Taxon("mu"));
			}
		});
		functionsPanel.add(addTaxonButton);
		
		Button addCharacterButton = new Button("Add Character");
		addCharacterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCharacter(new Character("ch"));
			}
		});
		functionsPanel.add(addCharacterButton);

		/*final TextButton removeButton = new TextButton("Remove Taxon");
		removeButton.setEnabled(false);
		SelectHandler removeButtonHandler = new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for (Taxon taxon : grid.getSelectionModel().getSelectedItems()) {
					grid.getStore().remove(taxon);
				}
				removeButton.setEnabled(false);
			}
		};
		removeButton.addSelectHandler(removeButtonHandler);
		functionsPanel.add(removeButton);
		
		grid.getSelectionModel().addSelectionChangedHandler(
				new SelectionChangedHandler<Taxon>() {
					@Override
					public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
						removeButton.setEnabled(!event.getSelection().isEmpty());
					}
				});
		*/
		
		/*final Button lockButton = new Button("Lock Taxon");
		lockButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (Taxon taxon : grid.getSelectionModel().getSelectedItems()) {
					//grid.getR
				}
			}
		});
		functionsPanel.add(lockButton);
		*/
				
		return panel;
	}

	private ColumnConfig<Taxon, String> createNameColumnConfig() {
		ColumnConfig<Taxon, String> nameCol = new ColumnConfig<Taxon, String>(
			new ValueProvider<Taxon, String>() {
				@Override
				public String getValue(Taxon object) {
					return object.getName();
				}
				@Override
				public void setValue(Taxon object, String value) {
					object.setName(value);
				}
				@Override
				public String getPath() {
					return "/name";
				}
			}, 200, "Taxon Concept");
		
		
		
		//List<HasCell<Taxon, ?>> childCells = new LinkedList<HasCell<Taxon, ?>>();
		//TextColumn<?> test = new TextColumn<?>();
		//Column<Taxon, ?> valueColumn = new Column<Taxon, ?>();
		//	childCells.add(new EditTextCell());
		//CompositeCell<Taxon> cell = new CompositeCell<Taxon>(childCells);
		
		
		nameCol.setCell(new MenuExtendedCell<String>());
		System.out.println(nameCol.getCell());
		//nameCol.setCell(new RowHeader());
		return nameCol;
	}
	
	private ColumnConfig<Taxon, String> createCharacterColumnConfig(final Character character) {
		return new ColumnConfig<Taxon, String>(
				new ValueProvider<Taxon, String>() {
					@Override
					public String getValue(Taxon object) {
						return object.get(character).getValue();
					}

					@Override
					public void setValue(Taxon object, String value) {
						object.put(character, new Value(value));
					}

					@Override
					public String getPath() {
						return "/" + character.getName() + "/value";
					}
				}, 200, character.getName());
	}

	public void deleteColumn(int colIndex) {
		//name is not allowed to be deleted
		if(colIndex > 0)
			this.removeCharacter(colIndex - 1);
	}

	public void toggleEditing(int colIndex) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		Field<?> field = editing.getEditor(columnConfig);
		System.out.println(editing.getEditor(columnConfig));
		if(editing.getEditor(columnConfig) != null) {
			this.disableEditing(columnConfig);
		} else {
			this.enableEditing(columnConfig);
		}
		
	}
	
	public void enableEditing(ColumnConfig columnConfig) {
		editing.addEditor(columnConfig, new TextField());
	}
	
	public void disableEditing(ColumnConfig columnConfig) {
		editing.removeEditor(columnConfig);
	}

	/*
	 * // final ListLoader<ListLoadConfig, ListLoadResult<Taxon>> loader = new
		// ListLoader<ListLoadConfig, ListLoadResult<Taxon>>(
		// proxy, reader);
		// loader.useLoadConfig(XmlAutoBeanFactory.instance.create(ListLoadConfig.class).as());
		// /loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig,
		// Taxon, ListLoadResult<Taxon>>(store));
	 */
}
