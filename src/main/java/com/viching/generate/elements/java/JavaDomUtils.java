package com.viching.generate.elements.java;

public class JavaDomUtils {
    /**
     * Calculates type names for writing into generated Java.  We try to
     * use short names wherever possible.  If the type requires an import,
     * but has not been imported, then we need to use the fully qualified
     * type name.
     * 
     * @param compilationUnit the compilation unit being written
     * @param fqjt the type in question
     */
    public static String calculateTypeName(CompilationUnit compilationUnit, FullyQualifiedJavaType fqjt) {

        if (fqjt.getTypeArguments().size() > 0) {
            return calculateParameterizedTypeName(compilationUnit, fqjt);
        }
        
        /*if (compilationUnit == null
                || typeDoesNotRequireImport(fqjt)
                || typeIsInSamePackage(compilationUnit, fqjt) 
                || typeIsAlreadyImported(compilationUnit, fqjt)) {
            return fqjt.getShortName();
        } else {
            return fqjt.getFullyQualifiedName();
        }*/
        
        return fqjt.getShortName();
    }

    private static String calculateParameterizedTypeName(CompilationUnit compilationUnit,
            FullyQualifiedJavaType fqjt) {
        String baseTypeName = calculateTypeName(compilationUnit,
                new FullyQualifiedJavaType(fqjt.getFullyQualifiedNameWithoutTypeParameters()));

        StringBuilder sb = new StringBuilder();
        sb.append(baseTypeName);
        sb.append('<');
        boolean comma = false;
        for (FullyQualifiedJavaType ft : fqjt.getTypeArguments()) {
            if (comma) {
                sb.append(", "); //$NON-NLS-1$
            } else {
                comma = true;
            }
            sb.append(calculateTypeName(compilationUnit, ft));
        }
        sb.append('>');

        return sb.toString();

    }
}
