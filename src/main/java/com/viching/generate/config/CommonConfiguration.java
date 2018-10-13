package com.viching.generate.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.viching.generate.config.impl.ICommentGeneratorImpl;
import com.viching.generate.config.impl.IConnectionImpl;
import com.viching.generate.config.impl.InvestigatorImpl;
import com.viching.generate.config.impl.JavaFormatterImpl;
import com.viching.generate.config.impl.JavaTypeResolverImpl;
import com.viching.generate.context.Context;
import com.viching.generate.converter.GenerateMethod;
import com.viching.generate.converter.GenerateProvider;
import com.viching.generate.converter.mapper.GenerateDeleteBatch;
import com.viching.generate.converter.mapper.GenerateDeleteByPrimaryKey;
import com.viching.generate.converter.mapper.GenerateInertBatch;
import com.viching.generate.converter.mapper.GenerateInsert;
import com.viching.generate.converter.mapper.GenerateInsertSelective;
import com.viching.generate.converter.mapper.GeneratePaging;
import com.viching.generate.converter.mapper.GenerateSelectByPrimaryKey;
import com.viching.generate.converter.mapper.GenerateSelectBySelective;
import com.viching.generate.converter.mapper.GenerateUpdateBatch;
import com.viching.generate.converter.mapper.GenerateUpdateByPrimaryKey;
import com.viching.generate.converter.mapper.GenerateUpdateByPrimaryKeySelective;
import com.viching.generate.converter.provider.PreCountBySelective;
import com.viching.generate.converter.provider.PreDeleteBySelective;
import com.viching.generate.converter.provider.PreInsertBatch;
import com.viching.generate.converter.provider.PreInsertSelective;
import com.viching.generate.converter.provider.PrePaging;
import com.viching.generate.converter.provider.PreSelectBySelective;
import com.viching.generate.converter.provider.PreUpdateBatch;
import com.viching.generate.converter.provider.PreUpdateByPrimarySelective;

@Configuration
@EnableAutoConfiguration
public class CommonConfiguration {

	// 配置文件
	@Bean
	@ConfigurationProperties(prefix = "generator")
	public Engine getEngine() {
		Engine config = new Engine();
		return config;
	}

	// 生成数据库联接生成对象
	@Bean
	public IConnection getIConnection() {
		return new IConnectionImpl();
	}

	// 生成context
	@Bean
	public Context getContext() {
		return new Context();
	}

	// 解析数据表
	@Bean
	public Investigator getInvestigator() {
		return new InvestigatorImpl();
	}

	// 数据类型解析器
	@Bean
	public JavaTypeResolver getJavaTypeResolver() {
		return new JavaTypeResolverImpl();
	}

	// 注释生成器
	@Bean
	public ICommentGenerator getICommentGenerator() {
		return new ICommentGeneratorImpl(false, false, true, true,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	// 格式化
	@Bean
	public JavaFormatter getJavaFormatter() {
		return new JavaFormatterImpl();
	}

	@Bean
	public List<GenerateMethod> getGenerateMethods() {
		List<GenerateMethod> list = new ArrayList<GenerateMethod>();
		list.add(new GenerateSelectByPrimaryKey());
		list.add(new GenerateSelectBySelective());
		list.add(new GeneratePaging());
		list.add(new GenerateInsert());
		list.add(new GenerateInsertSelective());
		list.add(new GenerateInertBatch());
		list.add(new GenerateUpdateByPrimaryKey());
		list.add(new GenerateUpdateByPrimaryKeySelective());
		list.add(new GenerateUpdateBatch());
		list.add(new GenerateDeleteByPrimaryKey());
		list.add(new GenerateDeleteBatch());
		return list;
	}

	@Bean
	public List<GenerateProvider> getGenerateProviders() {
		List<GenerateProvider> list = new ArrayList<GenerateProvider>();
		list.add(new PreInsertSelective());
		list.add(new PreInsertBatch());
		list.add(new PreUpdateByPrimarySelective());
		list.add(new PreUpdateBatch());
		list.add(new PreDeleteBySelective());
		list.add(new PreSelectBySelective());
		list.add(new PreCountBySelective());
		list.add(new PrePaging());
		return list;
	}
}
