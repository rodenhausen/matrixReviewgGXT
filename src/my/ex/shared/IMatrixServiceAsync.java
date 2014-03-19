package my.ex.shared;

import my.ex.shared.model.TaxonMatrix;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IMatrixServiceAsync {
	
	public void getMatrix(AsyncCallback<TaxonMatrix> callback);
	
}
