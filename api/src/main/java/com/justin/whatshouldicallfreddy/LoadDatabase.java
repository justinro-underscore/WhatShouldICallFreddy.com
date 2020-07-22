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
      log.info("Preloading " + repository.save(new DogName("Charlie")));
      log.info("Preloading " + repository.save(new DogName("Max")));
      log.info("Preloading " + repository.save(new DogName("Buddy")));
      log.info("Preloading " + repository.save(new DogName("Oscar")));
      log.info("Preloading " + repository.save(new DogName("Milo")));
      log.info("Preloading " + repository.save(new DogName("Archie")));
      log.info("Preloading " + repository.save(new DogName("Ollie")));
      log.info("Preloading " + repository.save(new DogName("Toby")));
      log.info("Preloading " + repository.save(new DogName("Jack")));
      log.info("Preloading " + repository.save(new DogName("Teddy")));
    };
  }
}