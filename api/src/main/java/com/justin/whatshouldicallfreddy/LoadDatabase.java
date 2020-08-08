package com.justin.whatshouldicallfreddy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.models.DogPicture;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;
import com.justin.whatshouldicallfreddy.repos.DogPictureRepository;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  private static void preloadDogNames(DogNameRepository repository) {
    log.info("Preloading dog names...");
    String[] names = {"Charlie", "Max", "Buddy", "Oscar", "Milo", "Archie", "Ollie", "Toby", "Jack", "Teddy"}; // Preload top 10 dog names
    for (String name : names) {
      log.info("Preloading " + repository.save(new DogName(name)));
    }
    log.info("Finished preloading dog names");
  }

  private static void preloadDogPictures(DogPictureRepository repository) {
    log.info("Preloading dog pictures...");
    String imgLocationPath = "api/src/main/resources/image/freddy/";
    // File[] images = (new File(imgLocationPath)).listFiles();
    // for (File f : images) {
    //   log.info("Preloading " + repository.save(new DogPicture("image/freddy/" + f.getName())));
    // }
    try {
      String jsonString = "";
      Scanner scn = new Scanner(new File(imgLocationPath + "images.json"));
      while (scn.hasNextLine()) {
        jsonString += scn.nextLine() + "\n";
      }
      scn.close();

      JSONParser parser = new JSONParser();
      JSONArray dogPictures = (JSONArray)parser.parse(jsonString);
  
      for (int i = 0; i < dogPictures.size(); i++) {
        JSONObject dogPicObj = (JSONObject)dogPictures.get(i);
        DogPicture dogPicture = new DogPicture("image/freddy/" + dogPicObj.get("fileName"), ((Long)dogPicObj.get("centerX")).intValue(), ((Long)dogPicObj.get("centerY")).intValue());
        if (dogPicture.getFileName().length() > 0) {
          log.info("Preloading " + repository.save(dogPicture));
        }
        else {
          log.warn("Could not load dog picture " + dogPicObj.get("fileName") + " (image not found)");
        }
      }
      log.info("Finished preloading dog pictures");
    }
    catch (FileNotFoundException e) {
      log.error("Dog pictures JSON file not found");
    }
    catch (ParseException e) {
      log.error("Dog pictures JSON file could not be parsed");
    }
  }

  @Bean
  CommandLineRunner initDogNameDatabase(DogNameRepository dogNameRepo, DogPictureRepository dogPictureRepo) {
    return args -> {
      log.info("Initializing API...");
      preloadDogNames(dogNameRepo);
      preloadDogPictures(dogPictureRepo);
      log.info("Completed initialization!");
    };
  }
}