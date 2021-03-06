/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.ui;

import static com.eclipsesource.tabris.internal.Clauses.whenNull;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;

import java.io.Serializable;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.eclipsesource.tabris.internal.ZIndexStackLayout;
import com.eclipsesource.tabris.internal.ui.Controller;
import com.eclipsesource.tabris.internal.ui.UIDescriptor;
import com.eclipsesource.tabris.internal.ui.UIImpl;
import com.eclipsesource.tabris.internal.ui.rendering.UIRenderer;


/**
 * <p>
 * The {@link TabrisUI} forms the core of the Tabris UI framework. It acts as the entry point for an UI.
 * The whole UI creation will be triggered from within the {@link TabrisUI#create(Shell)} method.
 * </p>
 * <p>
 * Usually you don't need to create this class directly. Consider using the {@link TabrisUIEntryPoint} instead. If you
 * are using data binding or other stuff were you need to have control over the {@link Display} the way to go is to
 * create a {@link TabrisUI} by yourself.
 * </p>
 *
 * @see TabrisUIEntryPoint
 *
 * @since 1.0
 */
@SuppressWarnings("restriction")
public class TabrisUI implements Serializable {

  private final UIConfiguration configuration;

  /**
   * <p>
   * Creates a new {@link TabrisUI} instance.
   * </p>
   *
   * @param configuration the configuration of the UI. Must not be <code>null</code>.
   *
   * @since 1.0
   */
  public TabrisUI( UIConfiguration configuration ) {
    whenNull( configuration ).throwIllegalArgument( "UIConfiguration must not be null" );
    getContext().getProtocolWriter().appendHead( "tabris.UI", JsonValue.valueOf( true ) );
    this.configuration = configuration;
  }

  /**
   * <p>
   * Will create the Tabris UI. Needs to be called within the creation of the UI e.g. in an
   * {@link EntryPoint#createUI()} method.
   * </p>
   *
   * @since 1.0
   */
  public void create( Shell shell ) {
    whenNull( shell ).throwIllegalArgument( "Shell must not be null" );
    prepareShell( shell );
    UIDescriptor uiDescriptor = configuration.getAdapter( UIDescriptor.class );
    UIRenderer uiRenderer = uiDescriptor.getRendererFactory().createUIRenderer( shell );
    Composite pageParent = uiRenderer.getPageParent();
    pageParent.setLayout( new ZIndexStackLayout() );
    Controller controller = new Controller( uiRenderer, uiDescriptor );
    UIImpl ui = prepareUi( pageParent.getDisplay(), uiRenderer, configuration, controller );
    configure( configuration, ui );
    setUiColors( pageParent.getDisplay(), uiRenderer, configuration );
    prepareController( controller, ui );
  }

  private void prepareShell( Shell shell ) {
    shell.setMaximized( true );
  }

  private UIImpl prepareUi( Display display, UIRenderer uiRenderer, UIConfiguration configuration, Controller controller ) {
    UIImpl ui = new UIImpl( display, controller, configuration );
    uiRenderer.setUi( ui );
    uiRenderer.setController( controller );
    return ui;
  }

  private void configure( UIConfiguration configuration, UIImpl ui ) {
    ui.markInitialized();
  }

  private void setUiColors( Display display, UIRenderer uiRenderer, UIConfiguration configuration ) {
    RGB background = configuration.getBackground();
    if( background != null ) {
      uiRenderer.setBackground( new Color( display, background ) );
    }
    RGB foreground = configuration.getForeground();
    if( foreground != null ) {
      uiRenderer.setForeground( new Color( display, foreground ) );
    }
  }

  private void prepareController( Controller controller, UIImpl ui ) {
    controller.createGlobalActions( ui );
    controller.createRootPages( ui );
  }
}
