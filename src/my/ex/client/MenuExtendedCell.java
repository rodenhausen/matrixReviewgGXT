package my.ex.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStyles;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class MenuExtendedCell<C> extends AbstractCell<C> {

	private ColumnHeaderAppearance columnHeaderAppearance;
	private GridAppearance gridAppearance;
	private ColumnHeaderStyles columnHeaderStyles;
	private GridStyles gridStyles;

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=\"{0}\"><div class=\"{1}\" style=\"width: calc(100% - 9px); height:14px\">{3}<a href=\"#\" class=\"{2}\" style=\"height: 22px;\"></a></div></div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value);
	}

	private static Templates templates = GWT.create(Templates.class);


	public MenuExtendedCell() {
		this(GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class), GWT.<GridAppearance> create(GridAppearance.class));
	}

	public MenuExtendedCell(ColumnHeaderAppearance columnHeaderAppearance, GridAppearance gridAppearance) {
		super(BrowserEvents.MOUSEOVER, BrowserEvents.MOUSEOUT, BrowserEvents.CLICK);

		this.columnHeaderAppearance = columnHeaderAppearance;
		this.gridAppearance = gridAppearance;
		columnHeaderStyles = columnHeaderAppearance.styles();
		gridStyles = gridAppearance.styles();
	}

	@Override
	public void render(Context context,	C value, SafeHtmlBuilder sb) {
		if (value == null)
			return;

		SafeHtml rendered = templates.cell("", columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value.toString());
		sb.append(rendered);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, C value,
			NativeEvent event, ValueUpdater<C> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		
		//System.out.println("parent " + parent);
		
		//A is the link used for menu; parent is parent of event
		com.google.gwt.user.client.Element aGrandParent = null;
		com.google.gwt.user.client.Element aParent = null;
		if(parent.getChildCount() > 0) { 
			aGrandParent = (com.google.gwt.user.client.Element)parent.getChild(0);
			if(aGrandParent.getChildCount() > 0) {
				aParent = (com.google.gwt.user.client.Element)aGrandParent.getChild(0);
			} else {
				System.out.println("no parent " + parent);
			}
		} else {
			System.out.println("no grand parent " + parent);
		}
		
		if(aParent != null && aGrandParent != null) {
		
			if(event.getType().equals(BrowserEvents.MOUSEOVER)) {
				//System.out.println(parent.getInnerHTML().toString());
				//System.out.println(((Element)parent.getChild(0)).getInnerHTML().toCharArray());
				//System.out.println(((Element)parent.getChild(0).getChild(0)).getInnerHTML().toCharArray());
				
				/*
				System.out.println(styles.headOver());
				System.out.println(styles.columnMoveBottom());
				System.out.println(styles.columnMoveTop());
				System.out.println(styles.head());
				System.out.println(styles.headButton());
				System.out.println(styles.header());
				System.out.println(styles.headInner());
				System.out.println(styles.headMenuOpen());
				System.out.println(styles.headOver());
				System.out.println(styles.headRow());
				System.out.println(styles.sortAsc());
				System.out.println(styles.sortDesc());
				System.out.println(styles.sortIcon());
				System.out.println(styles.headerInner());
				*/
				
				//client.Element is a newer version of dom.Element. It actually only extends it if you look in source
				//http://stackoverflow.com/questions/9024548/gwt-why-is-there-two-element-types
	
	
				
				//DOM.setStyleAttribute(aParent, "width", "189px");
				//DOM.setStyleAttribute(aParent, "height", "14px");
				
				aGrandParent.addClassName(columnHeaderStyles.headOver());
				
				//aParent.addClassName(styles.headInner());
				//DOM.setStyleAttribute(aGrandParent, "display", "block");
			}
	
			if(event.getType().equals(BrowserEvents.MOUSEOUT)) {
				aGrandParent.removeClassName(columnHeaderStyles.headOver());
				aGrandParent.removeClassName(columnHeaderStyles.headMenuOpen());
			}
			if (event.getType().equals(BrowserEvents.CLICK)) {
				if (Element.is(event.getEventTarget())) {
					Element clickedElement = Element.as(event.getEventTarget());
					if(clickedElement.getClassName().equals(columnHeaderStyles.headButton())) {
						aGrandParent.addClassName(columnHeaderStyles.headMenuOpen());
						this.showColumnMenu(clickedElement, context.getColumn(), context.getIndex());
					}
				}
				// event.preventDefault();
				// event.stopPropagation();
			}
		}
	}
	
	public void showColumnMenu(Element parent, final int column, final int row) {
		Menu menu = createContextMenu(column, row);
		if (menu != null) {
			menu.setId("cell" + column + "." + row + "-menu");
			menu.addHideHandler(new HideHandler() {
				@Override
				public void onHide(HideEvent event) {
					//h.activateTrigger(false);
					//if (container instanceof Component) {
					//	((Component) container).focus();
					//}
				}
			});
			menu.show(parent, new AnchorAlignment(Anchor.TOP_LEFT,
					Anchor.BOTTOM_LEFT, true));
		}
	}
		
	  /**
	   * Creates a context menu for the given column, including sort menu items and
	   * column visibility sub-menu.
	   * 
	   * @param colIndex the column index
	   * @return the context menu for the given column
	   */
	  protected Menu createContextMenu(final int colIndex, final int rowIndex) {
	    final Menu menu = new Menu();
	    MenuItem test = new MenuItem("wuha");
	    test.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				System.out.println("wuha at " + colIndex + " " + rowIndex);
			}
	    });
	    menu.add(test);
	    return menu;
	  }
}
