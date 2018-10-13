package com.viching.generate.config;

import com.viching.generate.elements.java.CompilationUnit;

public interface JavaFormatter {

    String getFormattedContent(CompilationUnit compilationUnit);
}
