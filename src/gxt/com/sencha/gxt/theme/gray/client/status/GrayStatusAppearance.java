/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.status.StatusBaseAppearance;

public class GrayStatusAppearance extends StatusBaseAppearance {

  public interface GrayStatusResources extends StatusBaseAppearance.StatusResources, ClientBundle {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/status/Status.css", "GrayStatus.css"})
    StatusStyle style();

    @Override
    @Source("com/sencha/gxt/theme/base/client/grid/loading.gif")
    ImageResource loading();

  }

  public GrayStatusAppearance() {
    super(GWT.<StatusResources> create(GrayStatusResources.class), GWT.<Template> create(Template.class));
  }
  
  public GrayStatusAppearance(GrayStatusResources resources, Template template) {
    super(resources, template);
  }

}
