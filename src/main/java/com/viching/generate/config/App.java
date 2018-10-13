package com.viching.generate.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;

@Component
public class App{

	@Autowired
	private Engine engine;
	
	@Autowired
	protected ICommentGenerator commentGenerator;
	

	public List<CompilationUnit> getCompilationUnits() {
		
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(engine.getGroupId()+".App");
		TopLevelClass config = new TopLevelClass(type);

		config.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(config);
		config.addAnnotation("@SpringBootApplication");
		config.addAnnotation("@EnableAutoConfiguration");

		Method main = new Method("main");
		main.setVisibility(JavaVisibility.PUBLIC);
		main.setStatic(true);
		Parameter parameter = new Parameter(new FullyQualifiedJavaType("java.lang.String[]"), "args");
		main.addParameter(parameter);
		main.addBodyLine("SpringApplication.run(App.class, args);");
		config.addMethod(main);
		
		config.addImportedType("org.springframework.boot.SpringApplication");
		config.addImportedType("org.springframework.boot.autoconfigure.EnableAutoConfiguration");
		config.addImportedType("org.springframework.boot.autoconfigure.SpringBootApplication");
			
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(config);
		answer.addAll(getTestCompilationUnits());
		return answer;
	}
	
public List<CompilationUnit> getTestCompilationUnits() {
		
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(engine.getGroupId()+".AppTest");
		TopLevelClass config = new TopLevelClass(type);

		config.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(config);
		
		config.addAnnotation("@RunWith(SpringRunner.class)");
		config.addAnnotation("@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)");

		Method test = new Method("test");
		test.addAnnotation("@Test");
		test.setVisibility(JavaVisibility.PUBLIC);
		test.addBodyLine("// TODO ----");
		config.addMethod(test);
		
		config.addImportedType("org.junit.Test");
		config.addImportedType("org.junit.runner.RunWith");
		config.addImportedType("org.springframework.beans.factory.annotation.Autowired");
		config.addImportedType("org.springframework.boot.test.context.SpringBootTest");
		config.addImportedType("org.springframework.test.context.junit4.SpringRunner");
			
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(config);
		return answer;
	}
}
