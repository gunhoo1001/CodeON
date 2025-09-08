package com.spring.app.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration  // Spring 컨테이너가 처리해주는 클래스로서, 클래스내에 하나 이상의 @Bean 메소드를 선언만 해주면 런타임시 해당 빈에 대해 정의되어진 대로 요청을 처리해준다.
@EnableTransactionManagement // 스프링 부트에서 Transaction 처리를 위한 용도
public class Datasource_mymvcuser_Configuration {

	@Value("${mybatis.mapper-locations}")  // *.yml 파일에 있는 설정값을 가지고 온 것으로서 mapper 파일의 위치를 알려주는 것이다.
    private String mapperLocations;
	
 //	@Bean(name = "dataSource")  와  @Bean @Qualifier("dataSource") 은 같은 것이다.  
	@Bean
 	@Qualifier("dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
 // @Primary
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
   /*
     @ConfigurationProperties은 *.properties , *.yml 파일에 있는 property를 자바 클래스에 값을 가져와서 사용할 수 있게 해주는 어노테이션이다. 
     
     @ConfigurationProperties를 사용하기 위해서는 
     build.gradle 에서
     annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor:3.5.3'
     를 추가해야 한다.
   */
	
	
 // @Bean(name = "sqlSessionFactory") 또는 
    @Bean
    @Qualifier("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception{ 
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml")); 
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));

        return sqlSessionFactoryBean.getObject();

    }

 // @Bean(name = "sqlsession") 또는 
    @Bean
    @Qualifier("sqlsession")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    /*
       @Primary 는 처음 스프링 구동 시 기본으로 사용할 Bean을 설정하는 것이다.
    
       @Autowired
       private SqlSessionTemplate abc;
       -- 위처럼 별도 Bean 설정없이 @Autowired로 연결한 경우, abc 에는 @Primary로 설정한 SqlSessionTemplate 빈이 주입된다.
    
       @Autowired
	   @Qualifier("sqlsession")
       private SqlSessionTemplate abc;
       -- 위처럼 @Qualifier("sqlsession")을 해주면 빈 이름이 sqlsession 인 SqlSessionTemplate 객체가 abc 에 주입된다. 
    */
    
    @Bean
    public PlatformTransactionManager transactionManager_mymvc_user() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }
    
 /*
	다음으로 transaction을 실행시키려고 하는 위치에서 다음의 예제와 같이 구현하시면 됩니다.
	@Service
	public class SampleService {
	
	    @Autowired
	    private SampleDao dao;
	    
	    @Transactional(value="transactionManager_mymvc_user")
	    @Override
	    public boolean insertSample(Sample sample) {
	        int returnId = dao.deleteSample(sample);
	        if (returnId > 0) {
	            dao.insertSample(sample);
	            return true;
	        }
	        return false;
	    }
	} 
	
	여기서 application에 선언한 transaction manager가 복수일 경우 target으로 하는 transaction manager명을 명시해야 합니다. 
	그렇지 않으면 다음과 같은 exception이 발생합니다.
	NoUniqueBeanDefinitionException
	No qualifying bean of type 'org.springframework.transaction.TransactionManager'
	
	org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'org.springframework.transaction.TransactionManager' available: expected single matching bean but found 2: transactionManager_hr,transactionManager_mymvc_user
 */
	
}
