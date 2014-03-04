package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;

public class MenuExtendedCell<C> extends AbstractCell<C> {

	private AnchorElement btn;
	private ColumnHeaderAppearance appearance;
	private ColumnHeaderStyles styles;
	
    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
      /**
       * The template for this Cell, which includes styles and a value.
       * 
       * @param styles the styles to include in the style attribute of the div
       * @param value the safe value. Since the value type is {@link SafeHtml},
       *          it will not be escaped before including it in the template.
       *          Alternatively, you could make the value type String, in which
       *          case the value would be escaped.
       * @return a {@link SafeHtml} instance
       */
      @SafeHtmlTemplates.Template("<a href=\"#\" class=\"{0}\" style=\"height: 22px;\"></a>")
      SafeHtml cell(String styleClass);
    }
	
    private static Templates templates = GWT.create(Templates.class);
	
	public MenuExtendedCell() {
		this(GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}
	
	public MenuExtendedCell(ColumnHeaderAppearance appearance) {
		this.appearance = appearance;
		styles = appearance.styles();
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			C value, SafeHtmlBuilder sb) {
		if(value == null)
			return;
		
		//SafeHtml safeValue = SafeHtmlUtils.fromString(value);
		//context.
		
		SafeHtml rendered = templates.cell(styles.headButton());
	    sb.append(rendered);
	    System.out.println(sb.toSafeHtml().asString());
		
		/*sb.append(SafeHtmlUtils.fromString("test"));

		btn = Document.get().createAnchorElement();
		btn.setHref("#");
		btn.setClassName(styles.headButton());
		System.out.println("inner: " + btn.getString());
		btn.getInnerHTML()
		sb.append(SafeHtmlUtils.fromString(btn.getString()));*/
	}

}
