package org.mule.tooling.lang.dw.indexer;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.DataInputOutputUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.*;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.editor.indexing.IdentifierKind;
import org.mule.weave.v2.editor.indexing.IdentifierScope;
import org.mule.weave.v2.editor.indexing.IdentifierType;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class WeaveGlobalDefinitionsIndexer extends FileBasedIndexExtension<String, List<WeaveGlobalDefinitionsIndexer.GlobalElementIdentifier>> {

    public static final ID<String, List<GlobalElementIdentifier>> INDEX_ID = ID.create("weave.definition");


    @Override
    public @NotNull ID<String, List<GlobalElementIdentifier>> getName() {
        return INDEX_ID;
    }

    @Override
    public @NotNull DataIndexer<String, List<GlobalElementIdentifier>, FileContent> getIndexer() {
        return inputData -> {
            final HashMap<String, List<GlobalElementIdentifier>> result = new HashMap<>();
            final WeaveDocument weaveDocument = PsiTreeUtil.getChildOfType(inputData.getPsiFile(), WeaveDocument.class);
            if (weaveDocument != null && weaveDocument.getBody() == null && weaveDocument.getHeader() != null) {
                NameIdentifier nameIdentifier1 = VirtualFileSystemUtils.calculateNameIdentifier(inputData.getProject(), inputData.getFile());
                String qualifiedName = nameIdentifier1.fullQualifiedName();
                PsiElement[] children = weaveDocument.getHeader().getChildren();
                for (PsiElement child : children) {
                    if (child instanceof WeaveFunctionDirective directive) {
                        WeaveFunctionDefinition functionDefinition = directive.getFunctionDefinition();
                        if (functionDefinition != null) {
                            String fqn = functionDefinition.getName();
                            List<GlobalElementIdentifier> weaveIdentifiers = result.computeIfAbsent(fqn, k -> new ArrayList<>());
                            PsiElement nameIdentifier = functionDefinition.getNameIdentifier();
                            if (nameIdentifier != null) {
                                TextRange textRange = nameIdentifier.getTextRange();
                                int scope =  directive.isPrivate() ? IdentifierScope.PRIVATE() :
                                            (directive.isInternal() ? IdentifierScope.INTERNAL() : IdentifierScope.PUBLIC());
                                weaveIdentifiers.add(new GlobalElementIdentifier(textRange.getStartOffset(),
                                        textRange.getEndOffset(),
                                        functionDefinition.getName(),
                                        IdentifierKind.DEFINITION(),
                                        IdentifierType.FUNCTION(),
                                        qualifiedName,
                                        scope
                                ));
                            }
                        }
                    } else if (child instanceof WeaveVariableDirective directive) {
                        WeaveVariableDefinition variableDefinition = directive.getVariableDefinition();
                        if (variableDefinition != null) {
                            String fqn = variableDefinition.getName();
                            List<GlobalElementIdentifier> weaveIdentifiers = result.computeIfAbsent(fqn, k -> new ArrayList<>());
                            PsiElement nameIdentifier = variableDefinition.getNameIdentifier();
                            if (nameIdentifier != null) {
                                int scope =  directive.isPrivate() ? IdentifierScope.PRIVATE() :
                                            (directive.isInternal() ? IdentifierScope.INTERNAL() : IdentifierScope.PUBLIC());
                                TextRange textRange = nameIdentifier.getTextRange();
                                weaveIdentifiers.add(new GlobalElementIdentifier(textRange.getStartOffset(),
                                        textRange.getEndOffset(),
                                        variableDefinition.getName(),
                                        IdentifierKind.DEFINITION(),
                                        IdentifierType.VARIABLE(),
                                        qualifiedName,
                                        scope
                                ));
                            }
                        }
                    } else if (child instanceof WeaveTypeDirective directive) {
                        WeaveTypeDefinition typeDefinition = directive.getTypeDefinition();
                        if (typeDefinition != null) {
                            String fqn = typeDefinition.getName();
                            List<GlobalElementIdentifier> weaveIdentifiers = result.computeIfAbsent(fqn, k -> new ArrayList<>());
                            PsiElement nameIdentifier = typeDefinition.getNameIdentifier();
                            if (nameIdentifier != null) {
                                int scope =  directive.isPrivate() ? IdentifierScope.PRIVATE() :
                                            (directive.isInternal() ? IdentifierScope.INTERNAL() : IdentifierScope.PUBLIC());
                                TextRange textRange = nameIdentifier.getTextRange();
                                weaveIdentifiers.add(new GlobalElementIdentifier(textRange.getStartOffset(),
                                        textRange.getEndOffset(),
                                        typeDefinition.getName(),
                                        IdentifierKind.DEFINITION(),
                                        IdentifierType.VARIABLE(),
                                        qualifiedName,
                                        scope
                                ));
                            }
                        }
                    }
                }
            }
            return result;
        };
    }

    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public @NotNull DataExternalizer<List<GlobalElementIdentifier>> getValueExternalizer() {
        return new DataExternalizer<>() {
            @Override
            public void save(@NotNull DataOutput out, List<GlobalElementIdentifier> value) throws IOException {
                DataInputOutputUtilRt.writeSeq(out, value, entry -> serialize(entry, out));
            }

            @Override
            public List<GlobalElementIdentifier> read(@NotNull DataInput in) throws IOException {
                return DataInputOutputUtilRt.readSeq(in, () -> deserialize(in));
            }
        };
    }

    private GlobalElementIdentifier deserialize(DataInput in) throws IOException {
        return new GlobalElementIdentifier(
                DataInputOutputUtil.readINT(in),
                DataInputOutputUtil.readINT(in),
                IOUtil.readUTF(in),
                DataInputOutputUtil.readINT(in),
                DataInputOutputUtil.readINT(in),
                IOUtil.readUTF(in),
                DataInputOutputUtil.readINT(in)
        );
    }

    private void serialize(GlobalElementIdentifier entry, DataOutput out) throws IOException {
        DataInputOutputUtil.writeINT(out, entry.startLocation());
        DataInputOutputUtil.writeINT(out, entry.endLocation());
        IOUtil.writeUTF(out, entry.value());
        DataInputOutputUtil.writeINT(out, entry.idType());
        DataInputOutputUtil.writeINT(out, entry.kind());
        IOUtil.writeUTF(out, entry.moduleName());
        DataInputOutputUtil.writeINT(out, entry.scope());
    }


    @Override
    public int getVersion() {
        return 1; // Incremented for internal scope support
    }

    @Override
    public FileBasedIndex.@NotNull InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(WeaveFileType.getInstance());
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    public static class GlobalElementIdentifier {
        final int startLocation;
        final int endLocation;
        final String value;
        final int idType;
        final int kind;
        final String moduleName;
        final int scope;

        public GlobalElementIdentifier(int startLocation, int endLocation, String value, int idType, int kind, String moduleName, int scope) {
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.value = value;
            this.idType = idType;
            this.kind = kind;
            this.moduleName = moduleName;
            this.scope = scope;
        }

        public int startLocation() {
            return startLocation;
        }

        public int endLocation() {
            return endLocation;
        }

        public String value() {
            return value;
        }

        public int idType() {
            return idType;
        }

        public int kind() {
            return kind;
        }

        public String moduleName() {
            return moduleName;
        }

        public int scope() {
            return scope;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GlobalElementIdentifier that = (GlobalElementIdentifier) o;
            return startLocation == that.startLocation &&
                    endLocation == that.endLocation &&
                    idType == that.idType &&
                    kind == that.kind &&
                    Objects.equals(value, that.value) &&
                    Objects.equals(moduleName, that.moduleName) &&
                    scope == that.scope;
        }

        @Override
        public int hashCode() {
            return Objects.hash(startLocation, endLocation, value, idType, kind, moduleName, scope);
        }
    }
}


