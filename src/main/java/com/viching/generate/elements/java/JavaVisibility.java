package com.viching.generate.elements.java;

/**
 * Typesafe enum of possible Java visibility settings.
 * 
 * @author Jeff Butler
 */
public enum JavaVisibility {
    PUBLIC("public "), //$NON-NLS-1$
    PRIVATE("private "), //$NON-NLS-1$
    PROTECTED("protected "), //$NON-NLS-1$
    DEFAULT(""); //$NON-NLS-1$

    private String value;

    private JavaVisibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
