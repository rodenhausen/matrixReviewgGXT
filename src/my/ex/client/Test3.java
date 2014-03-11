package my.ex.client;

import java.util.LinkedList;
import java.util.List;

import my.ex.client.model.Character;
import my.ex.client.model.Taxon;
import my.ex.client.model.TaxonMatrix;
import my.ex.client.model.Value;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Test3 implements EntryPoint {

	@Override
	public void onModuleLoad() {
		List<Character> characters = new LinkedList<Character>();
		Character a = new Character("a");
		Character b = new Character("b");
		Character c = new Character("c");
		characters.add(a);
		characters.add(b);
		characters.add(c);
		
		Taxon t1 = new Taxon("t1");
		Taxon t2 = new Taxon("t2");
		Taxon t3 = new Taxon("t3");

		TaxonMatrix taxonMatrix = new TaxonMatrix(characters);
		taxonMatrix.addTaxon(t1);
		taxonMatrix.addTaxon(t2);
		taxonMatrix.addTaxon(t3);
		
		
		TaxonMatrixView taxonMatrixView = new TaxonMatrixView();
		taxonMatrixView.init(taxonMatrix);
		
		//simulate etc site 
		DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		dock.addNorth(new HTML("header"), 2);
		dock.addSouth(new HTML("footer"), 2);
		dock.add(taxonMatrixView);
		
		//RootPanel.get().add(taxonMatrixView.asWidget());3
		RootLayoutPanel.get().add(dock);
	}

}
