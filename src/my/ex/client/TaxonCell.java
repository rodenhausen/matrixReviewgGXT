package my.ex.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TaxonCell<C> extends MenuExtendedCell<C> {

	@Override
	public void render(Context context,	C value, SafeHtmlBuilder sb) {
		if (value == null)
			return;

		SafeHtml rendered = templates.cell(columnHeaderStyles.header() + " " + columnHeaderStyles.head(), columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value.toString());
		sb.append(rendered);
	}

}
