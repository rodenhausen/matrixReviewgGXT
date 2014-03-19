package my.ex.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import my.ex.shared.IMatrixService;
import my.ex.shared.IMatrixServiceAsync;
import my.ex.shared.model.Character;
import my.ex.shared.model.Taxon;
import my.ex.shared.model.TaxonMatrix;
import my.ex.shared.model.Value;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		IMatrixServiceAsync matrixService = GWT.create(IMatrixService.class);
		// TaxonMatrix taxonMatrix = createSampleMatrix();
		matrixService.getMatrix(new AsyncCallback<TaxonMatrix>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(TaxonMatrix result) {
				TaxonMatrixView taxonMatrixView = new TaxonMatrixView();
				taxonMatrixView.init(result);

				// simulate etc site
				DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
				dock.addNorth(new HTML("header"), 2);
				dock.addSouth(new HTML("footer"), 2);
				dock.add(taxonMatrixView);

				// RootPanel.get().add(taxonMatrixView.asWidget());3
				RootLayoutPanel.get().add(dock);
			}
			
		});
	}

}
