package com.viching.generate.elements.java;

import java.util.List;
import java.util.Set;

/**
 * 生成：classes, interfaces, and enums
 * @author Administrator
 *
 */
public interface CompilationUnit {

    String getFormattedContent();

    Set<FullyQualifiedJavaType> getImportedTypes();

    Set<String> getStaticImports();

    FullyQualifiedJavaType getSuperClass();

    boolean isJavaInterface();

    boolean isJavaEnumeration();

    Set<FullyQualifiedJavaType> getSuperInterfaceTypes();

    FullyQualifiedJavaType getType();

    void addImportedType(FullyQualifiedJavaType importedType);

    void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes);

    void addStaticImport(String staticImport);

    void addStaticImports(Set<String> staticImports);

    /**
     * Comments will be written at the top of the file as is, we do not append any start or end comment characters.
     * 
     * <p>Note that in the Eclipse plugin, file comments will not be merged.
     *
     * @param commentLine
     *            the comment line
     */
    void addFileCommentLine(String commentLine);

    List<String> getFileCommentLines();
}
