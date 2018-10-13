package com.viching.generate.config.impl;

import com.viching.generate.config.JavaFormatter;
import com.viching.generate.elements.java.CompilationUnit;

public class JavaFormatterImpl implements JavaFormatter {

    @Override
    public String getFormattedContent(CompilationUnit compilationUnit) {
        return compilationUnit.getFormattedContent();
    }
}
