package my.ex.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import my.ex.shared.model.Character;
import my.ex.shared.model.Taxon;
import my.ex.shared.model.TaxonMatrix;
import my.ex.shared.model.Value;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.dnd.core.client.MyGridDragSource;
import com.sencha.gxt.dnd.core.client.MyGridDropTarget;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.editing.MyGridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.google.gwt.cell.client.AbstractCell;

import com.sencha.gxt.widget.core.client.form.Field;

public class TaxonMatrixView implements IsWidget {
	
	private TaxonMatrix taxonMatrix;
	private MyGrid<Taxon> grid;
	private MyGridInlineEditing<Taxon> editing;
	private FlowLayoutContainer container = new FlowLayoutContainer();
	private RowExpander<Taxon> expander;
	
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
		//yes or no? store.setAutoCommit(false);
		
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			store.add(taxon);
		}
		
		List<ColumnConfig<Taxon, ?>> l = new ArrayList<ColumnConfig<Taxon, ?>>();
		l.add(expander);
		l.add(this.createNameColumnConfig());
		for (final Character character : taxonMatrix.getCharacters()) 
			l.add(createCharacterColumnConfig(character));
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(l);
		
		grid.reconfigure(store, cm);
		
		//set up editing
		editing = new MyGridInlineEditing<Taxon>(grid);
		for (int i=1; i<l.size(); i++) {
			ColumnConfig columnConfig = l.get(i);
			this.enableEditing(columnConfig);
		}
		
		// set up filtering (tied to the store internally, so has to be done after grid is reconfigured with new store object)
		StringFilter<Taxon> taxonNameFilter = new StringFilter<Taxon>(
				new TaxonNameValueProvider());
		GridFilters<Taxon> filters = new GridFilters<Taxon>();
		filters.setLocal(true);
		filters.addFilter(taxonNameFilter);
		filters.initPlugin(grid);
		
		/*Filter<Taxon, String> taxonConceptFilter = new Filter<Taxon, String>(
				nameValueProvider) {
			protected TextField field;
		
			@Override
			public List<FilterConfig> getFilterConfig() {
				FilterConfig cfg = createNewFilterConfig();
				cfg.setType("string");
				cfg.setComparison("contains");
				String valueToSend = field.getCurrentValue();
				cfg.setValue(getHandler().convertToString(valueToSend));
				return Arrays.asList(cfg);
			}
		
			@Override
			public Object getValue() {
				return field.getCurrentValue();
			}
		
			@Override
			protected Class<String> getType() {
				return String.class;
			}
		};
		filters.addFilter();*/
	}

	private MyGrid<Taxon> createGrid() {
		MyGrid<Taxon> grid = new MyGrid<Taxon>(new ListStore<Taxon>(new TaxonModelKeyProvider()), 
				new ColumnModel<Taxon>(new ArrayList<ColumnConfig<Taxon, ?>>()), this);
		grid.getView().setForceFit(false); // if change in column width we want the table to become wider not stay fixed at overall width
		grid.setColumnReordering(true);
		
		//set up row drag and drop for taxon move
		MyGridDragSource<Taxon> dragSource = new MyGridDragSource<Taxon>(grid);
		MyGridDropTarget<Taxon> target = new MyGridDropTarget<Taxon>(grid, container);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(true);

		// set up row expander
		expander = new RowExpander<Taxon>(new IdentityValueProvider<Taxon>() {
			  @Override
			  public void setValue(Taxon object, Taxon value) {
				  System.out.println("set value");
			  }
			  @Override
			  public Taxon getValue(Taxon object) {
				  System.out.println("get value");
			    return object;
			  }
			  @Override
			  public String getPath() {
				  System.out.println("get path");
			    return "";
			  }
		}, new AbstractCell<Taxon>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Taxon value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant(value.getDescription());
			}
		});
		expander.initPlugin(grid);
		
		//possibly bring up edit menu for row?
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
		container.setScrollMode(ScrollMode.AUTO);
		
		VerticalPanel panel = new VerticalPanel();		
		panel.add(grid);
		HorizontalPanel functionsPanel = new HorizontalPanel();
		panel.add(functionsPanel);
		
		Button addTaxonButton = new Button("Add Taxon");
		addTaxonButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(int i=0; i<10; i++)
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
		container.add(panel);
		return container;
		//return panel;
	}

	private ColumnConfig<Taxon, String> createNameColumnConfig() {
		ColumnConfig<Taxon, String> nameCol = new ColumnConfig<Taxon, String>(
			new TaxonNameValueProvider(), 200, "Taxon Concept");
				
		nameCol.setCell(new TaxonCell<String>(grid));
		return nameCol;
	}
	
	private ColumnConfig<Taxon, String> createCharacterColumnConfig(final Character character) {
		ColumnConfig<Taxon, String> characterCol = new ColumnConfig<Taxon, String>(
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
		characterCol.setCell(new MenuExtendedCell<String>());
		return characterCol;
		
	}

	public void deleteColumn(int colIndex) {
		//name is not allowed to be deleted
		if(colIndex > 0)
			this.removeCharacter(colIndex - 1);
	}

	public void toggleEditing(int colIndex) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		Field<?> field = editing.getEditor(columnConfig);
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

	public Container getContainer() {
		return container;
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
