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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.List;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.tabris.internal.ui.PageDescriptor;
import com.eclipsesource.tabris.internal.ui.TestAction;
import com.eclipsesource.tabris.internal.ui.TestPage;
import com.eclipsesource.tabris.internal.ui.UIDescriptor;


public class UIConfigurationTest {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( UIConfiguration.class ) );
  }

  @Test
  public void testTransistionListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( TransitionListener.class ) );
  }

  @Test( expected = IllegalStateException.class )
    public void testAddPageConfigurationFailsWithDuplicateId() {
      UIConfiguration configuration = new UIConfiguration();
      PageConfiguration configuration1 = new PageConfiguration( "foo", TestPage.class );
      PageConfiguration configuration2 = new PageConfiguration( "foo", TestPage.class );

      configuration.addPageConfiguration( configuration1 );
      configuration.addPageConfiguration( configuration2 );
    }


  @Test( expected = IllegalArgumentException.class )
    public void testAddPageConfigurationFailsWithNullConfiguration() {
      UIConfiguration configuration = new UIConfiguration();

      configuration.addPageConfiguration( null );
    }

  @Test( expected = IllegalArgumentException.class )
    public void testAddActionConfigurationFailsWithNullConfiguration() {
      UIConfiguration configuration = new UIConfiguration();

      configuration.addActionConfiguration( null );
    }

  @Test( expected = IllegalStateException.class )
    public void testAddActionConfigurationFailsWithDuplicateId() {
      UIConfiguration configuration = new UIConfiguration();
      ActionConfiguration configuration1 = new ActionConfiguration( "foo", TestAction.class );
      ActionConfiguration configuration2 = new ActionConfiguration( "foo", TestAction.class );

      configuration.addActionConfiguration( configuration1 );
      configuration.addActionConfiguration( configuration2 );
    }

  @Test( expected = IllegalArgumentException.class )
  public void testAddTransitionListenerFailsWithNullListener() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addTransitionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testremoveTransitionListenerFailsWithNullListener() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.removeTransitionListener( null );
  }

  @Test
    public void testAddActionConfigurationReturnsSameUiInstance() {
      UIConfiguration configuration = new UIConfiguration();

      UIConfiguration actualConf
        = configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

      assertSame( configuration, actualConf );
    }

  @Test
    public void testAddActionConfigurationWithProminenceReturnsSameUiInstance() {
      UIConfiguration configuration = new UIConfiguration();

      UIConfiguration actualConf
        = configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

      assertSame( configuration, actualConf );
    }

  @Test
  public void testAddPageConfigurationDoesNotReturnNull() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class ).setTopLevel( true );

    UIConfiguration actualConf = configuration.addPageConfiguration( pageConfiguration );

    assertNotNull( actualConf );
  }

  @Test
  public void testGetDescriptorIsNotSafeCopy() {
    UIConfiguration configuration = new UIConfiguration();

    UIDescriptor contentHolder1 = configuration.getAdapter( UIDescriptor.class );
    UIDescriptor contentHolder2 = configuration.getAdapter( UIDescriptor.class );

    assertSame( contentHolder1, contentHolder2 );
    assertNotNull( contentHolder1 );
  }

  @Test
  public void testCanGetDescriptor() {
    UIConfiguration configuration = new UIConfiguration();

    UIDescriptor uiDescriptor = configuration.getAdapter( UIDescriptor.class );

    assertNotNull( uiDescriptor );
  }

  @Test
  public void testAddsPageToHolder() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class ).setTopLevel( true );

    configuration.addPageConfiguration( pageConfiguration );

    PageDescriptor actualDescriptor = configuration.getAdapter( UIDescriptor.class ).getPageDescriptor( "foo" );
    assertNotNull( actualDescriptor );
  }

  @Test
  public void testAddsActionToHolder() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    assertNotNull( configuration.getAdapter( UIDescriptor.class ).getActionDescriptor( "foo" ) );
  }

  @Test
  public void testAddsActionWitProminenceToHolder() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    assertNotNull( configuration.getAdapter( UIDescriptor.class ).getActionDescriptor( "foo" ) );
  }

  @Test
  public void testAddsTransitionListener() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );

    configuration.addTransitionListener( listener );

    List<TransitionListener> listeners = configuration.getAdapter( UIDescriptor.class ).getTransitionListeners();
    assertTrue( listeners.contains( listener ) );
    assertEquals( 1, listeners.size() );
  }

  @Test
  public void testAddsTransitionListenerReturnsUI() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );

    UIConfiguration actualUI = configuration.addTransitionListener( listener );

    assertSame( configuration, actualUI );
  }

  @Test
  public void testRemovesTransitionListener() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );
    configuration.addTransitionListener( listener );

    configuration.removeTransitionListener( listener );

    List<TransitionListener> listeners = configuration.getAdapter( UIDescriptor.class ).getTransitionListeners();
    assertTrue( listeners.isEmpty() );
  }

  @Test
  public void testSetsForeground() {
    UIConfiguration configuration = new UIConfiguration();
    RGB foreground = new RGB( 233, 233, 233 );

    configuration.setForeground( foreground );

    RGB actualForeground = configuration.getForeground();
    assertEquals( foreground, actualForeground );
  }

  @Test
  public void testSetsForegroundWithRGB() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setForeground( 233, 233, 233 );

    RGB actualForeground = configuration.getForeground();
    assertEquals( new RGB( 233, 233, 233 ), actualForeground );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetForegroundFailsWithNull() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setForeground( null );
  }

  @Test
  public void testSetsBackground() {
    UIConfiguration configuration = new UIConfiguration();
    RGB background = new RGB( 233, 233, 233 );

    configuration.setBackground( background );

    RGB actualBackground = configuration.getBackground();
    assertEquals( background, actualBackground );
  }

  @Test
  public void testSetsBackgroundWithRGB() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setBackground( 233, 233, 233 );

    RGB actualBackground = configuration.getBackground();
    assertEquals( new RGB( 233, 233, 233 ), actualBackground );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBackgroundFailsWithNull() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setBackground( null );
  }

}
