/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.nodemodel.INode;

import java.util.List;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * Registry of commonly used text in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Labels {

  @Inject private INodes nodes;
  @Inject private Options options;
  @Inject private Fields properties;

  public Object labelFor(Object o) {
    if (o instanceof Option) {
      Option option = (Option) o;
      return labelFor(option);
    }
    if (o instanceof MessageExtension) {
      MessageExtension extend = (MessageExtension) o;
      return labelFor(extend);
    }
    if (o instanceof Extensions) {
      Extensions extensions = (Extensions) o;
      return labelFor(extensions);
    }
    if (o instanceof Import) {
      Import anImport = (Import) o;
      return labelFor(anImport);
    }
    if (o instanceof Literal) {
      Literal literal = (Literal) o;
      return labelFor(literal);
    }
    if (o instanceof MessageField) {
      MessageField field = (MessageField) o;
      return labelFor(field);
    }
    if (o instanceof Rpc) {
      Rpc rpc = (Rpc) o;
      return labelFor(rpc);
    }
    return null;
  }

  private Object labelFor(Option option) {
    IndexedElement e = options.rootSourceOf(option);
    String name = options.nameForOption(e);
    StringBuilder b = new StringBuilder();
    boolean isCustomOption = option instanceof CustomOption || option instanceof CustomFieldOption;
    if (isCustomOption) b.append("(");
    b.append(name);
    if (isCustomOption) b.append(")");
    if (option instanceof CustomOption) {
      appendFields(b, ((CustomOption) option).getFields());
    }
    if (option instanceof CustomFieldOption) {
      appendFields(b, ((CustomFieldOption) option).getFields());
    }
    return b.toString();
  }
  
  private void appendFields(StringBuilder b, List<OptionField> fields) {
    for (OptionField field : fields) {
      b.append(".");
      if (field instanceof MessageOptionField) {
        IndexedElement source = ((MessageOptionField) field).getTarget();
        b.append(options.nameForOption(source));
      }
      if (field instanceof ExtensionOptionField) {
        IndexedElement source = ((ExtensionOptionField) field).getTarget();
        b.append("(")
         .append(options.nameForOption(source))
         .append(")");
      }
    }
  }

  private Object labelFor(MessageExtension extension) {
    return messageName(extension.getMessage());
  }

  private Object labelFor(Extensions extensions) {
    StringBuilder builder = new StringBuilder();
    EList<Range> ranges = extensions.getRanges();
    int rangeCount = ranges.size();
    for (int i = 0; i < rangeCount; i++) {
      if (i > 0) builder.append(", ");
      Range range = ranges.get(i);
      builder.append(range.getFrom());
      String to = range.getTo();
      if (to != null) {
        builder.append(" > ").append(to);
      }
    }
    return builder.toString();
  }

  private Object labelFor(Import anImport) {
    INode node = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    if (node == null) return anImport.getImportURI();
    return node.getText();
  }

  private Object labelFor(Literal literal) {
    StyledString text = new StyledString(literal.getName());
    String index = String.format(" [%d]", literal.getIndex());
    text.append(index, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(MessageField field) {
    StyledString text = new StyledString(field.getName());
    String typeName = properties.typeNameOf(field);
    if (typeName == null) typeName = "<unresolved reference>"; // TODO move to
                                                               // properties
                                                               // file
    String indexAndType = String.format(" [%d] : %s", field.getIndex(), typeName);
    text.append(indexAndType, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(Rpc rpc) {
    StyledString text = new StyledString(rpc.getName());
    String types = String.format(" : %s > %s", messageName(rpc.getArgType()), messageName(rpc.getReturnType()));
    text.append(types, DECORATIONS_STYLER);
    return text;
  }

  private String messageName(MessageLink link) {
    return link.getTarget().getName();
  }
}
