package com.viching.generate.elements.java;

import java.util.ArrayList;
import java.util.List;

public class Parameter {
    private String name;
    private FullyQualifiedJavaType type;
    private boolean isVarargs;

    private List<String> annotations;

    public Parameter(FullyQualifiedJavaType type, String name, boolean isVarargs) {
        super();
        this.name = name;
        this.type = type;
        this.isVarargs = isVarargs;
        annotations = new ArrayList<String>();
    }

    public Parameter(FullyQualifiedJavaType type, String name) {
        this(type, name, false);
    }

    public Parameter(FullyQualifiedJavaType type, String name, String annotation) {
        this(type, name, false);
        addAnnotation(annotation);
    }

    public Parameter(FullyQualifiedJavaType type, String name, String annotation, boolean isVarargs) {
        this(type, name, isVarargs);
        addAnnotation(annotation);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the type.
     */
    public FullyQualifiedJavaType getType() {
        return type;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void addAnnotation(String annotation) {
        annotations.add(annotation);
    }

    public String getFormattedContent(CompilationUnit compilationUnit) {
        StringBuilder sb = new StringBuilder();

        for (String annotation : annotations) {
            sb.append(annotation);
            sb.append(' ');
        }

        //sb.append(JavaDomUtils.calculateTypeName(compilationUnit, type));
        sb.append(type.getShortName());

        sb.append(' ');
        if (isVarargs) {
            sb.append("... "); //$NON-NLS-1$
        }
        sb.append(name);

        return sb.toString();
    }

    @Override
    public String toString() {
        return getFormattedContent(null);
    }

    public boolean isVarargs() {
        return isVarargs;
    }
}
