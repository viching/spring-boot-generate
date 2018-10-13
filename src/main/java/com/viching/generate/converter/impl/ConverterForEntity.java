package com.viching.generate.converter.impl;

import static com.viching.generate.db.util.JavaBeansUtil.getJavaBeansField;
import static com.viching.generate.db.util.JavaBeansUtil.getJavaBeansGetter;
import static com.viching.generate.db.util.JavaBeansUtil.getJavaBeansSetter;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.adapter.Adapter;
import com.viching.generate.config.Engine;
import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.elements.java.TopLevelEnumeration;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class ConverterForEntity extends ConverterDriver {
	
	@Autowired
	private Engine engine;

	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
				dbTable.getBaseRecordType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);

		if (dbTable.needGeneratePrimaryKey()) {
			FullyQualifiedJavaType superClass = getSuperClass(dbTable);
			if (superClass != null) {
				topLevelClass.setSuperClass(superClass);
				topLevelClass.addImportedType(superClass);
			}
		}
		commentGenerator.addModelClassComment(topLevelClass, dbTable);

		List<DBColumnJavaBean> introspectedColumns = getColumnsInThisClass(dbTable);

		addParameterizedConstructor(topLevelClass, dbTable.getAllColumns(),
				dbTable);

		addDefaultConstructor(topLevelClass, dbTable);

		for (DBColumnJavaBean introspectedColumn : introspectedColumns) {

			Field field = getJavaBeansField(introspectedColumn,
					commentGenerator, dbTable);
			topLevelClass.addField(field);
			topLevelClass.addImportedType(field.getType());

			Method method = getJavaBeansGetter(introspectedColumn,
					commentGenerator, dbTable);

			topLevelClass.addMethod(method);

			method = getJavaBeansSetter(introspectedColumn, commentGenerator,
					dbTable);
			topLevelClass.addMethod(method);
		}
		
		if(adapters != null && adapters.size() > 0){
			for (Adapter adapter : adapters) {
				adapter.adaptClass(topLevelClass, dbTable);
			}
		}

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(topLevelClass);
		
		List<CompilationUnit> extraCompilationUnits = getDTOCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }
        extraCompilationUnits = getSearchCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }
        
        extraCompilationUnits = getEnumCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }
		return answer;
	}
	
	private List<CompilationUnit> getEnumCompilationUnits(
			DBTableJavaBean dbTable) {
        //获取文件的全限定名
        FullyQualifiedJavaType type = null;
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        for (DBColumnJavaBean c : dbTable.getBaseColumns()) {
            if (c.getJdbcType() == Types.TINYINT && engine.isNeedEnum()) {
                type = c.getFullyQualifiedJavaType();
                getEnumCompilationUnitsEntity(answer, type, c);
                //ConfigXMLMapperGenerator.addEnumType(type.getFullyQualifiedName());
            }
        }
        return answer;
	}
	
	private void getEnumCompilationUnitsEntity(List<CompilationUnit> answer, FullyQualifiedJavaType type, DBColumnJavaBean c) {
        //如果不存在enum属性，则返回空数组
        if (null == type) {
            return;
        }
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        //生成java文件对象
        TopLevelEnumeration topLevelEnumeration = new TopLevelEnumeration(type);
        //java文件声明为public类
        topLevelEnumeration.setVisibility(JavaVisibility.PUBLIC);
        //实现接口
        FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(engine.getEnumRoot());
        importedTypes.add(interfaceType);
        //getCompilationUnits(answer, interfaceType);

        topLevelEnumeration.addSuperInterface(interfaceType);
        topLevelEnumeration.addImportedTypes(importedTypes);
        //设置annotation
        String comment = c.getRemarks();
        if(StringUtils.isNotBlank(comment)){
        	while(comment.endsWith(";")){
        		comment = comment.substring(0, comment.length() - 1);
        	}
            topLevelEnumeration.addEnumConstant(comment);
        }else{
            topLevelEnumeration.addEnumConstant("");
        }
        //添加默认构造函数
        Method method = new Method();
        //构造函数设置为private
        method.setVisibility(JavaVisibility.PRIVATE);
        //该方法声明为构造函数
        method.setConstructor(true);
        //设置名称
        method.setName(type.getShortName());
        //设置方法参数
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "code"));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "desc"));
        //为方法中添加操作
        method.addBodyLine("this.code = code;");
        method.addBodyLine("this.desc = desc;");

        topLevelEnumeration.addMethod(method);

        // 添加默认属性
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setName("code");
        topLevelEnumeration.addField(field);

        field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(FullyQualifiedJavaType.getStringInstance());
        field.setName("desc");
        topLevelEnumeration.addField(field);

        //设置getter
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("getCode");
        method.addBodyLine("return this.code;");
        topLevelEnumeration.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("getDesc");
        method.addBodyLine("return this.desc;");
        topLevelEnumeration.addMethod(method);
        answer.add(topLevelEnumeration);
    }

	private List<CompilationUnit> getDTOCompilationUnits(DBTableJavaBean dbTable) {
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                dbTable.getDtoType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);

		FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(
                dbTable.getBaseRecordType());
		if (superClass != null) {
			topLevelClass.setSuperClass(superClass);
			topLevelClass.addImportedType(superClass);
		}

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        
        if(adapters != null && adapters.size() > 0){
			for (Adapter adapter : adapters) {
				adapter.adaptClass(topLevelClass, dbTable);
			}
		}
        return answer;
    }
	
	private List<CompilationUnit> getSearchCompilationUnits(DBTableJavaBean dbTable) {
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                dbTable.getSearchType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);

		FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(
                dbTable.getBaseRecordType());
		if (superClass != null) {
			topLevelClass.setSuperClass(superClass);
			topLevelClass.addImportedType(superClass);
		}

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        
        if(adapters != null && adapters.size() > 0){
			for (Adapter adapter : adapters) {
				adapter.adaptClass(topLevelClass, dbTable);
			}
		}
        return answer;
    }
}
