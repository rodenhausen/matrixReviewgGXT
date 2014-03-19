package my.ex.client;

import my.ex.shared.model.Taxon;

import com.sencha.gxt.core.client.ValueProvider;

public class TaxonNameValueProvider implements ValueProvider<Taxon, String> {

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

}
