// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface WeavePrivateFunctionDefinition extends WeaveNamedElement {

  @Nullable
  WeaveDynamicReturn getDynamicReturn();

  @Nullable
  WeaveExpression getExpression();

  @NotNull
  List<WeaveFunctionParameter> getFunctionParameterList();

  @NotNull
  WeaveIdentifier getIdentifier();

  @Nullable
  WeaveType getType();

  @NotNull
  List<WeaveTypeParameter> getTypeParameterList();

  String getName();

  PsiElement setName(@NotNull String newName);

  PsiElement getNameIdentifier();

  //WARNING: getPresentation(...) is skipped
  //matching getPresentation(WeavePrivateFunctionDefinition, ...)
  //methods are not found in WeavePsiImplUtils

  //WARNING: getElementIcon(...) is skipped
  //matching getElementIcon(WeavePrivateFunctionDefinition, ...)
  //methods are not found in WeavePsiImplUtils

}
