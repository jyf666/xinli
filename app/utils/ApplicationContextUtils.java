package utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import service.CacheService;

public class ApplicationContextUtils {
	private static ApplicationContext applicationContext;

	public static void init(){
		applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
		SystemConstant.startup_date = applicationContext.getStartupDate();

		CacheService cacheService = applicationContext.getBean("cacheService", CacheService.class);
		cacheService.initCache();
	}
	
	public static <T> T getBean(Class<T> clazz){
		return applicationContext.getBean(clazz);
	}
	
	public static <T> T getBean(String name, Class<T> clazz){
		return applicationContext.getBean(name, clazz);
	}
	
	public static <T> T getBean(String name){
		return (T)applicationContext.getBean(name);
	}
	
	public static Resource getResource(String location){
		return applicationContext.getResource(location);
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
