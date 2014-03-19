package my.ex.shared;

import my.ex.shared.model.TaxonMatrix;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("matrix")
public interface IMatrixService extends RemoteService {

	public TaxonMatrix getMatrix();
	
}
