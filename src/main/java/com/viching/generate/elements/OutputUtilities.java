package com.viching.generate.elements;

import java.util.Set;
import java.util.TreeSet;

import com.viching.generate.elements.java.FullyQualifiedJavaType;

/**
 * 
 * @author Administrator
 *
 */
public class OutputUtilities {

    /** The Constant lineSeparator. */
    private static final String lineSeparator;

    static {
        String ls = System.getProperty("line.separator"); //$NON-NLS-1$
        if (ls == null) {
            ls = "\n"; //$NON-NLS-1$
        }
        lineSeparator = ls;
    }

    /**
     * Utility class - no instances allowed.
     */
    private OutputUtilities() {
        super();
    }

    /**
     * Utility method that indents the buffer by the default amount for Java
     * (four spaces per indent level).
     * 
     * @param sb
     *            a StringBuilder to append to
     * @param indentLevel
     *            the required indent level
     */
    public static void javaIndent(StringBuilder sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("    "); //$NON-NLS-1$
        }
    }

    /**
     * Utility method that indents the buffer by the default amount for XML (two
     * spaces per indent level).
     * 
     * @param sb
     *            a StringBuilder to append to
     * @param indentLevel
     *            the required indent level
     */
    public static void xmlIndent(StringBuilder sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  "); //$NON-NLS-1$
        }
    }

    /**
     * Utility method. Adds a newline character to a StringBuilder.
     * 
     * @param sb
     *            the StringBuilder to be appended to
     */
    public static void newLine(StringBuilder sb) {
        sb.append(lineSeparator);
    }

    /**
     * returns a unique set of "import xxx;" Strings for the set of types.
     *
     * @param importedTypes
     *            the imported types
     * @return the sets the
     */
    public static Set<String> calculateImports(
            Set<FullyQualifiedJavaType> importedTypes) {
        StringBuilder sb = new StringBuilder();
        Set<String> importStrings = new TreeSet<String>();
        for (FullyQualifiedJavaType fqjt : importedTypes) {
            for (String importString : fqjt.getImportList()) {
                sb.setLength(0);
                sb.append("import "); //$NON-NLS-1$
                sb.append(importString);
                sb.append(';');
                importStrings.add(sb.toString());
            }
        }

        return importStrings;
    }
}
