package com.viching.generate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.viching.generate.context.Context;

@SpringBootApplication
public class Application implements CommandLineRunner{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private Context context;
	
    public static void main(String[] args) throws Exception {

        SpringApplication app = new SpringApplication(Application.class);
        //关闭spring的logo
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        
    }
    
    /**
	 * spring boot 构建可执行非web jar 实现CommandLineRunner接口并实现run方法, 这是重点。
	 */
	@Override
	public void run(String... args) {
		logger.debug("开始生成……");
		
		context.openTheDoor();	
		
		logger.debug("生成结束……");
	}
}