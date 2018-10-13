package com.viching.generate.converter.impl;

import static com.viching.generate.db.util.JavaBeansUtil.getJavaBeansField;
import static com.viching.generate.db.util.JavaBeansUtil.getJavaBeansGetter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.viching.generate.adapter.Adapter;
import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class ConverterForPrimariKey extends ConverterDriver{
	
	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {
		if(!dbTable.needGeneratePrimaryKey()){
			return null;
		}
        TopLevelClass topLevelClass = new TopLevelClass(dbTable
                .getPrimaryKeyType());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        addParameterizedConstructor(topLevelClass, dbTable.getPrimaryKeyColumns(), dbTable);
        addDefaultConstructor(topLevelClass, dbTable);

        commentGenerator.addModelClassComment(topLevelClass, dbTable);

        for (DBColumnJavaBean dbColumn : dbTable.getPrimaryKeyColumns()) {

            Field field = getJavaBeansField(dbColumn, commentGenerator, dbTable);
            topLevelClass.addField(field);
            topLevelClass.addImportedType(field.getType());

            Method method = getJavaBeansGetter(dbColumn, commentGenerator, dbTable);
            topLevelClass.addMethod(method);
        }
        
        if(adapters != null && adapters.size() > 0){
			for (Adapter adapter : adapters) {
				adapter.adaptClass(topLevelClass, dbTable);
			}
		}

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        return answer;
	}

}
