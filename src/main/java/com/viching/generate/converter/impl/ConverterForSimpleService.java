package com.viching.generate.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.config.Engine;
import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class ConverterForSimpleService extends ConverterDriver {
	
	@Autowired
	private Engine engine;

	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getServiceType());
		Interface topInterface = new Interface(type);
		topInterface.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topInterface);
		
		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(
				dbTable.getBaseRecordType());
		topInterface.addImportedType(paramType);
		
		FullyQualifiedJavaType superIService = new FullyQualifiedJavaType(engine.getSuperIService());
		superIService.addTypeArgument(paramType);
		superIService.addTypeArgument(new FullyQualifiedJavaType(dbTable.getPrimaryKeyType()));
		
		topInterface.addSuperInterface(superIService);
		topInterface.addImportedType(superIService);

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(topInterface);
		
		List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }
        
		return answer;
	}

	public List<CompilationUnit> getExtraCompilationUnits(DBTableJavaBean dbTable) {

        FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType(dbTable.getServiceType());
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getServiceImplType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        topLevelClass.addSuperInterface(superInterface);
        topLevelClass.addImportedType(superInterface);
        topLevelClass.addAnnotation("@Service");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));

        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(dbTable.getBaseRecordType());
        topLevelClass.addImportedType(paramType);
        
        FullyQualifiedJavaType superService = new FullyQualifiedJavaType(engine.getSuperService());
		superService.addTypeArgument(paramType);
		superService.addTypeArgument(new FullyQualifiedJavaType(dbTable.getPrimaryKeyType()));
		topLevelClass.setSuperClass(superService);
		topLevelClass.addImportedType(superService);
		
		Field log = new Field();
        log.setVisibility(JavaVisibility.PRIVATE);
        log.setFinal(true);
        log.setName("logger");
        FullyQualifiedJavaType logType = new FullyQualifiedJavaType("org.slf4j.Logger");
        log.setType(logType);
        log.addAnnotation("@SuppressWarnings(\"unused\")");
        log.setInitializationString("LoggerFactory.getLogger(this.getClass())");
        topLevelClass.addField(log);
        FullyQualifiedJavaType logFactoryType = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        topLevelClass.addImportedType(logType);
        topLevelClass.addImportedType(logFactoryType);
        
        // 添加默认属性
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        FullyQualifiedJavaType mapper = new FullyQualifiedJavaType(dbTable.getExtMapperType());
        topLevelClass.addImportedType(mapper);
        field.setType(mapper);
        String mapperName = dbTable.getMapperName();
        field.setName(mapperName);
        topLevelClass.addImportedType(field.getType());
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);
        
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
        
        //checkDelEnable
        Method method = new Method("checkDelEnable");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(readOnly = true)");
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        method.addParameter(new Parameter(new FullyQualifiedJavaType(dbTable.getPrimaryKeyType()), "id"));
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine("return false;");
        method.addBodyLine("");
        topLevelClass.addMethod(method);
        
        //getNamespace
        method = new Method("getNamespace");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Override");
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.addBodyLine("");
        method.addBodyLine("return "+ mapper.getShortName() +".class.getName();");
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        
        return answer;
    }
}
