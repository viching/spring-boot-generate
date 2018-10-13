package com.viching.generate.converter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.converter.GenerateMethod;
import com.viching.generate.converter.InjectionOrder;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class ConverterForMapper extends ConverterDriver{

	@Autowired
	private List<GenerateMethod> generateMethods;

	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                dbTable.getMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);
        
        if(generateMethods != null){
        	Collections.sort(generateMethods, new Comparator<GenerateMethod>() {
				@Override
				public int compare(GenerateMethod m1, GenerateMethod m2) {
					InjectionOrder order1 = m1.getClass().getAnnotation(InjectionOrder.class);
					InjectionOrder order2 = m2.getClass().getAnnotation(InjectionOrder.class);
		            if (order1 == null) {
		            	return 1;
		            }
		            if (order2 == null) {
		            	return -1;
		            }
		            return order1.value() - order2.value();
				}
			});
        }
        	
        for(GenerateMethod gm : generateMethods){
        	gm.addInterfaceElements(interfaze, dbTable);
        }
        
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(interfaze);

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
	}
	
	public List<CompilationUnit> getExtraCompilationUnits(DBTableJavaBean dbTable) {
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                dbTable.getExtMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));

        commentGenerator.addJavaFileComment(interfaze);

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                dbTable.getMapperType());
        interfaze.addSuperInterface(fqjt);
        interfaze.addImportedType(fqjt);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(interfaze);
        return answer;
    }

}
