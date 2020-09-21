package com.justin.whatshouldicallfreddy.controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.justin.whatshouldicallfreddy.LoadDatabase;
import com.justin.whatshouldicallfreddy.exceptions.DogNameNotFoundException;
import com.justin.whatshouldicallfreddy.exceptions.DogPictureAlreadyExistsException;
import com.justin.whatshouldicallfreddy.exceptions.DogPictureNotFoundException;
import com.justin.whatshouldicallfreddy.exceptions.InvalidSecurityCredentialsException;
import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.models.DogPicture;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;
import com.justin.whatshouldicallfreddy.repos.DogPictureRepository;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/admin/api")
@CrossOrigin(origins = { "http://localhost:3000", "http://www.whatshouldicallfreddy.com" }, allowCredentials = "true")
@RestController
public class WhatShouldICallFreddyAdminController {
  private final DogNameRepository dogNameRepository;
  private final DogPictureRepository dogPictureRepository;
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  private String securityToken;

  public WhatShouldICallFreddyAdminController(DogNameRepository dogNameRepository,
      DogPictureRepository dogPictureRepository) {
    this.dogNameRepository = dogNameRepository;
    this.dogPictureRepository = dogPictureRepository;

    securityToken = System.getenv("security_token");
    if (securityToken != null) {
      return;
    }

    try {
      InputStream fis = WhatShouldICallFreddyAdminController.class.getResourceAsStream("/security/security_token.txt");
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);
      securityToken = br.readLine();
      br.close();
      isr.close();
      fis.close();
    } catch (Exception ex) {
      log.error("Security token could not be loaded: " + ex.getMessage());
    }
  }

  private void verifySecurityToken(String token, String caller) throws InvalidSecurityCredentialsException {
    if (securityToken != null) {
      if (securityToken.equals(token)) {
        return;
      }
    }
    log.warn(String.format("%s Security credentials invalid", caller));
    throw new InvalidSecurityCredentialsException();
  }

  @PostMapping("/{token}/dogpictures/info")
  public Long addNewDogPictureInfo(@PathVariable String token, @RequestBody DogPicture dogPicture) {
    verifySecurityToken(token, "POST " + "/dogpictures/picture/info");
    DogPicture newDogPicture = new DogPicture(dogPicture.getNormalizedCenterX(), dogPicture.getNormalizedCenterY());
    dogPictureRepository.save(newDogPicture);
    log.info("POST " + "/dogpictures/info/ " + "Added new dog picture info only: " + newDogPicture);
    return newDogPicture.getId();
  }

  @PostMapping("/{token}/dogpictures/picture/{id}")
  public Boolean addNewDogPicturePicture(@PathVariable String token, @PathVariable Long id,
      @RequestParam("file") MultipartFile newPicture) {
    verifySecurityToken(token, "POST " + "/dogpictures/picture/" + id + "/");

    DogPicture dogPicture = dogPictureRepository.findById(id).orElseThrow(() -> new DogPictureNotFoundException(id));
    if (dogPicture.getFileName() != null) {
      log.warn("POST " + "/dogpictures/picture/" + id + "/ " + "Picture already exists!");
      throw new DogPictureAlreadyExistsException(id);
    }

    if (newPicture.isEmpty()) {
      log.warn("POST " + "/dogpictures/picture/" + id + "/ " + "No picture provided!");
      return false;
    }

    try {
      byte[] bytes = newPicture.getBytes();
      Path homePath = Paths.get("src/src/main/resources/image/freddy/");
      int numPictures = homePath.toFile().listFiles().length; // NOTE: This also includes the json file
      Path newPicturePath = homePath.resolve("Freddy" + numPictures
          + newPicture.getOriginalFilename().substring(newPicture.getOriginalFilename().lastIndexOf(".")));
      Files.write(newPicturePath, bytes);

      final String IMG_LOCATION_PATH = "/image/freddy/";
      String jsonString = "";
      InputStream fis = WhatShouldICallFreddyAdminController.class
          .getResourceAsStream(IMG_LOCATION_PATH + "images.json");
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);
      String line = null;
      do {
        line = br.readLine();
        if (line != null) {
          jsonString += line + "\n";
        }
      } while (line != null);
      br.close();
      isr.close();

      JSONParser parser = new JSONParser();
      JSONArray dogPictures = (JSONArray) parser.parse(jsonString);
      dogPictures.add(parser.parse("{" +
        "\"fileName\": \"" + newPicturePath.getFileName().toString() + "\"," +
        "\"centerX\": " + dogPicture.getNormalizedCenterX() + "," +
        "\"centerY\": " + dogPicture.getNormalizedCenterY() +
      "}"));
      Files.write(homePath.resolve("images.json"), dogPictures.toString().getBytes());

      InputStream in = new ByteArrayInputStream(bytes);
      BufferedImage originalImage = ImageIO.read(in);
      int height = originalImage.getHeight();
      int width = originalImage.getWidth();
      log.error("Height: " + height + " Width: " + width);
      dogPicture.setNormalizedValues(width, height, dogPicture.getNormalizedCenterX(), dogPicture.getNormalizedCenterY());
      dogPicture.setFileName(IMG_LOCATION_PATH + newPicturePath.getFileName().toString());
      // dogPicture.setNewDogPicture(IMG_LOCATION_PATH + newPicturePath.getFileName().toString());
      dogPictureRepository.save(dogPicture);
      log.info("POST " + "/dogpictures/picture/" + id + "/ " + "Picture successfully set!");
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    log.warn("POST " + "/dogpictures/picture/" + id + "/ " + "Picture was unable to be set");
    return false;
  }

  @PutMapping("/{token}/dognames/{id}")
  public DogName replaceDogName(@PathVariable String token, @RequestBody DogName newDogName, @PathVariable Long id) {
    verifySecurityToken(token, "PUT " + "/dognames/" + id + "/");
    return dogNameRepository.findById(id).map(dogName -> {
      log.info("PUT " + "/dognames/" + id + "/ " + "Replacing " + dogName + " with " + newDogName);
      dogName.updateDogName(newDogName);
      return dogNameRepository.save(dogName);
    }).orElseThrow(() -> new DogNameNotFoundException(id));
  }

  @DeleteMapping("/{token}/dognames/{id}")
  public void deleteDogName(@PathVariable String token, @PathVariable Long id) {
    verifySecurityToken(token, "DELETE " + "/dognames/" + id + "/");
    log.info("DELETE " + "/dognames/" + id + "/ " + "Deleting dog name with id " + id);
    if (dogNameRepository.findById(id).isPresent()) {
      dogNameRepository.deleteById(id);
      return;
    }
    throw new DogNameNotFoundException(id);
  }
}