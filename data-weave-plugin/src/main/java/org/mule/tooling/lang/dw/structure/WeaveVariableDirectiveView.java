package org.mule.tooling.lang.dw.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

public class WeaveVariableDirectiveView extends PsiTreeElementBase<WeaveVariableDirective> {
  protected WeaveVariableDirectiveView(WeaveVariableDirective psiElement) {
    super(psiElement);
  }

  @NotNull
  @Override
  public Collection<StructureViewTreeElement> getChildrenBase() {
    return Arrays.asList();
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return getElement().getVariableDefinition().getName();
  }

  @Override
  public Icon getIcon(boolean open) {
    if (getElement() != null && getElement().isPrivate())  {
      RowIcon icon = new RowIcon(2);
      icon.setIcon(PlatformIcons.VARIABLE_ICON, 0);
      icon.setIcon(PlatformIcons.PRIVATE_ICON, 1);
      return icon;
    }
    return PlatformIcons.VARIABLE_ICON;
  }
}
