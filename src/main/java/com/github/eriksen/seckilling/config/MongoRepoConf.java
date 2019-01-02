package com.github.eriksen.seckilling.config;

import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoRepoConf
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.github.eriksen.seckilling.repository", mongoTemplateRef = "seckillDbTemp")
public class MongoRepoConf {

  @Bean("seckillConf")
  @ConfigurationProperties(prefix = "spring.data.mongodb.seckill")
  public MongoProperties seckillConf() {
    return new MongoProperties();
  }

  @Bean
  public MongoDbFactory seckillDbFactory(@Qualifier("seckillConf") MongoProperties mongoProperties) {
    String uri = "mongodb://";

    if (seckillConf().getUsername() != null && seckillConf().getPassword() != null) {
      uri += seckillConf().getUsername() + ':' + seckillConf().getPassword().toString() + '@';
    }
    uri += seckillConf().getHost() + ':' + seckillConf().getPort() + '/' + seckillConf().getDatabase();

    return new SimpleMongoDbFactory(new MongoClientURI(uri));
  }

  @Bean("seckillDbTemp")
  public MongoTemplate authdbTemplate() {
    return new MongoTemplate(seckillDbFactory(seckillConf()));
  }
}