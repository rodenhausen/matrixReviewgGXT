package com.sencha.gxt.widget.core.client.grid;

import my.ex.client.TaxonMatrixView;

import com.sencha.gxt.data.shared.ListStore;

public class MyGrid<M> extends Grid<M> {

	  public MyGrid(ListStore<M> store, ColumnModel<M> cm, TaxonMatrixView taxonMatrixView) {
	    super(store, cm, new MyGridView<M>(taxonMatrixView));
	  }
	
	  //possibly need to override setView() also to make sure this is used with MyGridView only
}
