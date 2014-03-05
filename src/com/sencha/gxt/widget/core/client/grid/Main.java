package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.resources.css.InterfaceGenerator;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] args2 = { "-standalone", "-typeName", "some.package.MyCssResource", "-css", 
				"C:/git2/matrixReviewgGXT/src/gxt/com/sencha/gxt/theme/base/client/grid/ColumnHeader.css" };
		InterfaceGenerator.main(args2);
	}

}
