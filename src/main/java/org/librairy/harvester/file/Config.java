package org.librairy.harvester.file;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.apache.camel.spring.SpringCamelContext;
import org.librairy.harvester.component.GenericFileProcessStrategy;
import org.librairy.harvester.component.strategy.GenericFileNoOpProcessStrategy;
import org.librairy.harvester.component.strategy.GenericFileRenameProcessStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by cbadenes on 01/12/15.
 */
@Configuration("org.librairy.harvester.file")
@ComponentScan({"org.librairy"})
@PropertySource({"classpath:harvester.properties","classpath:boot.properties"})
public class Config {


    @Autowired
    List<RouteBuilder> builders;

    @Autowired
    private Environment env;

    @Value("${harvester.input.folder}")
    String inputFolder;

    @Value("${librairy.eventbus.host}")
    private String host;

    @Value("${librairy.eventbus.port}")
    private String port;

    @Value("${librairy.eventbus.user}")
    private String user;

    @Value("${librairy.eventbus.password}")
    private String pwd;

    @Value("${librairy.eventbus.keyspace}")
    private String keyspace;

    @Value("${librairy.cassandra.contactpoints}")
    String cassandraHosts;

    @Value("${librairy.cassandra.port}")
    Integer cassandraPort;

    @Value("${librairy.cassandra.keyspace}")
    String cassandraKeyspace;

    @Bean
    public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {
        SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
        for(RouteBuilder builder : builders){
            camelContext.addRoutes(builder);
        }
        return camelContext;
    }

    //To resolve ${} in @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "fileStore")
    public FileIdempotentRepository getFileStore(){
        FileIdempotentRepository repository = new FileIdempotentRepository();
        repository.setFileStore(new File(inputFolder+File.separator+".fileStore.dat"));
        repository.setMaxFileStoreSize(Long.MAX_VALUE);
        repository.setCacheSize(Integer.MAX_VALUE);
        return repository;
    }


    @Bean(name="customConnectionFactory")
    public ConnectionFactory getCustomConnectionFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setAutomaticRecoveryEnabled(true);

        String uri = new StringBuilder().
                append("amqp://").append(user).append(":").append(pwd).
                append("@").append(host).append(":").append(port).
                append("/").append(keyspace).toString();

        connectionFactory.setUri(uri);
        return connectionFactory;
    }

    @Bean(name="customProcessStrategy")
    public GenericFileProcessStrategy getCustomProcessStrategy() {
        //return new GenericFileRenameProcessStrategy();
        return new GenericFileNoOpProcessStrategy();
    }

}
