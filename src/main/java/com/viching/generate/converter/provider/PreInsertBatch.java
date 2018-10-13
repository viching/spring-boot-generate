package com.viching.generate.converter.provider;

import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.viching.generate.converter.GenerateMethod;
import com.viching.generate.converter.GenerateProvider;
import com.viching.generate.db.FormattingUtilities;
import com.viching.generate.db.util.JavaBeansUtil;
import com.viching.generate.db.util.StringUtility;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class PreInsertBatch extends GenerateProvider {

	public PreInsertBatch() {
		super();
	}

	@Override
    public void addClassElements(TopLevelClass topLevelClass, DBTableJavaBean dbTable) {
        Set<String> staticImports = new TreeSet<String>();
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

        importedTypes.add(NEW_BUILDER_IMPORT);

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(dbTable.getBaseRecordType());
        importedTypes.add(fqjt);
        FullyQualifiedJavaType outer = FullyQualifiedJavaType.getNewListInstance();
        outer.addTypeArgument(fqjt);
        importedTypes.add(outer);

        Method method = new Method(GenerateMethod.METHOD_INSERT_BATCH);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        //method.addParameter(new Parameter(outer, "records"));
        Parameter parameter = new Parameter(outer, "records");
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("@Param(\"");
        sb.append("list");
        sb.append("\")");
        parameter.addAnnotation(sb.toString());
        method.addParameter(parameter);
        
        method.addBodyLine("StringBuffer sb = new StringBuffer(\"\");"); 
        method.addBodyLine(""); 
        
        method.addBodyLine("for(int i = 0; i< records.size(); i++){");
        method.addBodyLine(""); 
        method.addBodyLine(String.format("%s record = records.get(i);", fqjt.getShortName()));
        method.addBodyLine("");
        
        addOne(method, dbTable);
        
        method.addBodyLine("}"); 
        method.addBodyLine(""); 

        method.addBodyLine(""); 
        method.addBodyLine("return sb.toString();"); 
        
        topLevelClass.addStaticImports(staticImports);
        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
        
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
    }
	
	public void addOne(Method method, DBTableJavaBean dbTable){
		method.addBodyLine("SQL sql = new SQL();"); 
		method.addBodyLine(String.format("%sINSERT_INTO(\"%s\");", 
                builderPrefix,
                StringUtility.escapeStringForJava(dbTable.getTableName())));

        for (DBColumnJavaBean dbColumn : dbTable.getAllColumns()) {
            
            method.addBodyLine(""); 
            if (!dbColumn.getFullyQualifiedJavaType().isPrimitive()
                    && !dbColumn.isSequenceColumn()) {
                method.addBodyLine(String.format("if (record.%s() != null) {", 
                		JavaBeansUtil.getGetterMethodName(dbColumn.getJavaProperty(),
                                dbColumn.getFullyQualifiedJavaType())));
            }
            method.addBodyLine(String.format("%sVALUES(\"%s\", \"%s\");", 
                    builderPrefix,
                    StringUtility.escapeStringForJava(FormattingUtilities.getEscapedColumnName(dbColumn)),
                    FormattingUtilities.getParameterClause(dbColumn,"list[\"+i+\"].")));

            if (!dbColumn.getFullyQualifiedJavaType().isPrimitive()
                    && !dbColumn.isSequenceColumn()) {
                method.addBodyLine("}"); 
            }
        }
        method.addBodyLine("sb.append(sql.toString());"); 
        method.addBodyLine("sb.append(\";\");"); 
	}

}
