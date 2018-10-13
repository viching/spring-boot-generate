package com.viching.generate.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.converter.GenerateProvider;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class ConverterForProvider extends ConverterDriver {
	
	@Autowired
	private List<GenerateProvider> generateProviders;
	
	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getProviderType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);
        
        for(GenerateProvider gp : generateProviders){
        	gp.addClassElements(topLevelClass, dbTable);
        }
        
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        if (topLevelClass.getMethods().size() > 0) {
        	answer.add(topLevelClass);
        }

        return answer;
	}

}
