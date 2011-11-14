/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.junit.Assert.assertNotNull;

import com.google.eclipse.protobuf.junit.core.XtextRule;

import org.junit.*;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=154">Issue 154</a>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue154_AllowMultipleSemicolons_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());
  
  // syntax = "proto2";
  // 
  // message Foo {
  //   optional double bar = 1 [deprecated = true];;
  // }
  @Test public void should_allow_multiple_semicolons_at_the_end_of_field() {
    assertNotNull(xtext.root());
  }   
}
