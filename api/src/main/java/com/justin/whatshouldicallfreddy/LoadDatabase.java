package com.justin.whatshouldicallfreddy;

import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  @Bean
  CommandLineRunner initDatabase(DogNameRepository repository) {
    return args -> {
      log.info("Preloading " + repository.save(new DogName("Freddy")));
      log.info("Preloading " + repository.save(new DogName("Fred")));
      log.info("Preloading " + repository.save(new DogName("Ferdinator 5000")));
      log.info("Preloading " + repository.save(new DogName("Freds")));
      log.info("Preloading " + repository.save(new DogName("Ferds")));
      log.info("Preloading " + repository.save(new DogName("Fredster")));
    };
  }
}