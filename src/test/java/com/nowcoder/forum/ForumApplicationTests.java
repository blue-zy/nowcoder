package com.nowcoder.forum;

import com.nowcoder.forum.dao.ADao;
import com.nowcoder.forum.service.AService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import javax.crypto.interfaces.PBEKey;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = ForumApplication.class)
class ForumApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);

		ADao aDao = applicationContext.getBean(ADao.class);
		System.out.println(aDao.select());

		aDao = applicationContext.getBean("aHibernate", ADao.class);
		System.out.println(aDao.select());
	}

	@Test
	public void testBeanManagement(){
		AService aService = applicationContext.getBean(AService.class);
		System.out.println(aService);

		aService = applicationContext.getBean(AService.class);
		System.out.println(aService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	@Qualifier("aHibernate")
	private ADao aDao;

	@Test
	public void testDI(){
		System.out.println(aDao.select());
	}
}
