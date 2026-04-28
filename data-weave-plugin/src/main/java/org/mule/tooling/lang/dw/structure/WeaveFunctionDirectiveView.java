package org.mule.tooling.lang.dw.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionParameter;
import org.mule.tooling.lang.dw.parser.psi.WeaveType;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WeaveFunctionDirectiveView extends PsiTreeElementBase<WeaveFunctionDirective> {
  protected WeaveFunctionDirectiveView(WeaveFunctionDirective psiElement) {
    super(psiElement);
  }

  @NotNull
  @Override
  public Collection<StructureViewTreeElement> getChildrenBase() {
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public String getPresentableText() {
    WeaveFunctionDefinition functionDefinition = getElement().getFunctionDefinition();
    return getFunctionLabel(functionDefinition);
  }

  @NotNull
  public static String getFunctionLabel(WeaveFunctionDefinition functionDefinition) {
    List<WeaveFunctionParameter> functionParameterList = functionDefinition.getFunctionParameterList();
    StringBuilder params = new StringBuilder();
    for (WeaveFunctionParameter weaveFunctionParameter : functionParameterList) {
        String variableName = weaveFunctionParameter.getNameIdentifier().getText();
      WeaveType type = weaveFunctionParameter.getType();
      if (type != null) {
        variableName = variableName + ": " + type.getText();
      }
      if (params.isEmpty()) {
        params.append(variableName);
      } else {
        params.append(", ").append(variableName);
      }
    }
    String functionDescription = functionDefinition.getName() + "(" + params + ")";
    if (functionDefinition.getType() != null) {
      functionDescription = functionDescription + ": " + functionDefinition.getType().getText();
    }
    return functionDescription;
  }

  @Override
  public Icon getIcon(boolean open) {
    if (getElement() != null && getElement().isPrivate())  {
      RowIcon icon = new RowIcon(2);
      icon.setIcon(PlatformIcons.FUNCTION_ICON, 0);
      icon.setIcon(PlatformIcons.PRIVATE_ICON, 1);
      return icon;
    } else if (getElement() != null && getElement().isInternal()) {
      RowIcon icon = new RowIcon(2);
      icon.setIcon(PlatformIcons.FUNCTION_ICON, 0);
      icon.setIcon(PlatformIcons.PACKAGE_LOCAL_ICON, 1);
      return icon;
    }
    return PlatformIcons.FUNCTION_ICON;
  }
}
