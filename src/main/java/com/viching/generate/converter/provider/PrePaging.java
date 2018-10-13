package com.viching.generate.converter.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.viching.generate.converter.GenerateMethod;
import com.viching.generate.converter.GenerateProvider;
import com.viching.generate.db.util.StringUtility;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class PrePaging extends GenerateProvider {

	public PrePaging() {
		super();
	}

	@Override
	public void addClassElements(TopLevelClass topLevelClass, DBTableJavaBean dbTable) {

		Set<String> staticImports = new TreeSet<String>();
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

		importedTypes.add(NEW_BUILDER_IMPORT);

		FullyQualifiedJavaType inner = new FullyQualifiedJavaType(dbTable.getBaseRecordType());
		importedTypes.add(inner);
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(getPagingType("Paging"));
        importedTypes.add(fqjt);

        
        Method method = new Method(GenerateMethod.METHOD_SELECT_PAGING);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(fqjt, "paging"));

        FullyQualifiedJavaType util = new FullyQualifiedJavaType(getPagingType("QueryUtil"));
        importedTypes.add(util);
        method.addBodyLine("");
        method.addBodyLine(String.format("return QueryUtil.getLine(paging.getParams(), \"%s\", \"params.\");", StringUtility.escapeStringForJava(dbTable.getTableName())));

		topLevelClass.addStaticImports(staticImports);
		topLevelClass.addImportedTypes(importedTypes);
		topLevelClass.addMethod(method);
	
	}
	
	
	 public String getPagingType(String className) {
	    	String type = "com.viching.bootstrap";
	        String strs[] = type.split("\\.");
	        List<String> list = new ArrayList<String>();
	        list.addAll(Arrays.asList(strs));
	        list.add("plugin");
	        list.add(className);
	        return StringUtils.join(list, ".");
	    }
}
