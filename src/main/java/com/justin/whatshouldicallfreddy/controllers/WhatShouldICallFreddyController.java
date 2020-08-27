package com.justin.whatshouldicallfreddy.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import com.justin.whatshouldicallfreddy.LoadDatabase;
import com.justin.whatshouldicallfreddy.exceptions.DogNameExistsException;
import com.justin.whatshouldicallfreddy.exceptions.DogNameNotFoundException;
import com.justin.whatshouldicallfreddy.exceptions.DogPictureNotFoundException;
import com.justin.whatshouldicallfreddy.exceptions.NoDogNamesRemainingException;
import com.justin.whatshouldicallfreddy.exceptions.NoDogPicturesRemainingException;
import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.models.DogName.DogNameSorter;
import com.justin.whatshouldicallfreddy.models.DogPicture;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;
import com.justin.whatshouldicallfreddy.repos.DogPictureRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://www.whatshouldicallfreddy.com"}, allowCredentials = "true") // TODO Make this legit
@RestController
public class WhatShouldICallFreddyController {
  private final DogNameRepository dogNameRepository;
  private final DogPictureRepository dogPictureRepository;
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  public WhatShouldICallFreddyController(DogNameRepository dogNameRepository, DogPictureRepository dogPictureRepository) {
    this.dogNameRepository = dogNameRepository;
    this.dogPictureRepository = dogPictureRepository;
  }

  @GetMapping("/heartbeat")
  public Long heartbeat() {
    return Long.valueOf(1);
  }

  // Dog Names

  @RequestMapping(value={"/dognames", "/dognames/all"})
  public List<DogName> allDogNames() {
    List<DogName> dogNames = dogNameRepository.findAll();

    log.info("GET " + "/dognames " + "Retrieving " + dogNames.size() + " names");
    return dogNames;
  }

  @GetMapping("/dognames/one")
  public DogName oneDogName(@CookieValue(value="amesnay", required=false) String namesSeenString) {
    Long[] namesSeen = new Long[0];
    if (namesSeenString != null && namesSeenString.length() > 0) {
      String[] names = new String(Base64.getUrlDecoder().decode(namesSeenString)).split(",");
      namesSeen = new Long[names.length];
      for (int i = 0; i < names.length; i++) {
        namesSeen[i] = Long.valueOf(names[i]);
      }
    }
    Object[] dogNamesObjArr = dogNameRepository.getDogNamesNotInList(namesSeen).toArray();
    if (dogNamesObjArr.length > 0) {
      DogName[] dogNames = Arrays.copyOf(dogNamesObjArr, dogNamesObjArr.length, DogName[].class);

      Arrays.sort(dogNames, new DogNameSorter());

      int dogNamesRandMax = 3;
      if (dogNames.length < 3) {
        dogNamesRandMax = dogNames.length;
      }
      int index = (new Random()).nextInt(dogNamesRandMax);

      log.info("GET " + "/dognames/one " + "Retrieving " + dogNames[index]);
      return dogNames[index];
    }
    throw new NoDogNamesRemainingException();
  }

  @PostMapping("/dognames")
  public DogName newDogName(@RequestBody DogName newDogName) {
    if (dogNameRepository.countByName(newDogName.getName()) == 0) {
      DogName dogName = dogNameRepository.save(newDogName);
      log.info("POST " + "/dognames " + "Saving " + dogName);
      return dogName;
    }
    log.info("POST " + "/dognames " + "Dog name \"" + newDogName.getName() + "\" already exists");
    throw new DogNameExistsException(newDogName.getName());
  }

  // Single item

  @GetMapping("/dognames/{id}")
  public DogName dogNameWithId(@PathVariable Long id) {
    DogName dogName = dogNameRepository.findById(id).orElseThrow(() -> new DogNameNotFoundException(id));
    log.info("GET " + "/dognames/" + id + "/ " + "Retrieving " + dogName);
    return dogName;
  }

  // Increase votes

  @PostMapping("/dognames/vote/{id}/{vote}")
  public DogName dogNameVote(@PathVariable Long id, @PathVariable Boolean vote) {
    return dogNameRepository.findById(id).map(dn -> {
      log.info("POST " + "/dognames/vote/" + id + "/" + vote + "/ " + "Increasing " + (vote ? "yes" : "no") + " votes for " + dn);
      if (vote) {
        dn.incYesVotes();
      }
      else {
        dn.incNoVotes();
      }
      return dogNameRepository.save(dn);
    }).orElseThrow(() -> new DogNameNotFoundException(id));
  }

  // Dog Pictures

  private byte[] getBytesFromDogPicture(DogPicture dogPicture, String url) throws DogPictureNotFoundException {
    try {
      ClassPathResource imgFile = new ClassPathResource(dogPicture.getFileName());
      return StreamUtils.copyToByteArray(imgFile.getInputStream());
    }
    catch (IOException e) {
      log.warn("GET " + url + " " + "Error in creating image: {" + e.getMessage() + "}");
      throw new DogPictureNotFoundException(dogPicture.getId());
    }
  }

  // @GetMapping("/dogpictures")
  // public ResponseEntity<List<byte[]>> allDogPictures() {
  //   List<byte[]> dogPictures = dogPictureRepository.findAll().stream()
  //     .map(dogPicture -> getBytesFromDogPicture(dogPicture, "/dogpictures")).collect(Collectors.toList());

  //   log.info("GET " + "/dogpictures " + "Retrieving " + dogPictures.size() + " pictures");
  //   return ResponseEntity
  //     .ok()
  //     .contentType(MediaType.IMAGE_JPEG)
  //     .body(dogPictures);
  // }

  // Single item

  @RequestMapping(value = "/dogpictures/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getDogPicture(@PathVariable Long id) {
    DogPicture dogPicture = dogPictureRepository.findById(id).orElseThrow(() -> new DogPictureNotFoundException(id));
    byte[] bytes = getBytesFromDogPicture(dogPicture, "/dogpictures/" + id + "/");

    log.info("GET " + "/dogpictures/" + id + "/ " + "Retrieving " + dogPicture);
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(bytes);
  }

  @RequestMapping(value = "/dogpictures/random", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getRandomDogPicture() {
    DogPicture dogPicture = dogPictureRepository.getRandomDogPictures().get(0);
    byte[] bytes = getBytesFromDogPicture(dogPicture, "/dogpictures/random/");

    log.info("GET " + "/dogpictures/random/ " + "Retrieving random picture " + dogPicture);
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(bytes);
  }

  @RequestMapping(value = "/dogpictures/random/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getRandomDogPicture(@PathVariable Long id) {
    List<DogPicture> dogPictures = dogPictureRepository.getRandomDogPictures();
    DogPicture dogPicture = dogPictures.get(0);
    if (dogPicture.getId() == id) {
      dogPicture = dogPictures.get(1);
    }
    byte[] bytes = getBytesFromDogPicture(dogPicture, "/dogpictures/random/");

    log.info("GET " + "/dogpictures/random/" + id + "/ " + "Retrieving random picture with id that is not " + id + " " + dogPicture);
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(bytes);
  }

  @GetMapping("/dogpictures/info")
  public List<DogPicture> getDogPicturesInfo() {
    List<DogPicture> dogPictures = dogPictureRepository.findAll();

    log.info("GET " + "/dogpictures/info/ " + "Retrieving info for " + dogPictures.size() + " dog pictures");
    return dogPictures;
  }

  @GetMapping("/dogpictures/info/{id}")
  public DogPicture getDogPictureInfoForId(@PathVariable Long id) {
    DogPicture dogPicture = dogPictureRepository.findById(id).orElseThrow(() -> new DogPictureNotFoundException(id));

    log.info("GET " + "/dogpictures/info/" + id + "/ " + "Retrieving info for " + dogPicture);
    return dogPicture;
  }

  @GetMapping("/dogpictures/info/random")
  public DogPicture getRandomDogPictureInfo(@CookieValue(value="icspay", required=false) String picsSeenString) {
    Long[] picsSeen = new Long[0];
    if (picsSeenString != null && picsSeenString.length() > 0) {
      String[] pics = new String(Base64.getUrlDecoder().decode(picsSeenString)).split(",");
      picsSeen = new Long[pics.length];
      for (int i = 0; i < pics.length; i++) {
        picsSeen[i] = Long.valueOf(pics[i]);
      }
    }
    Object[] dogPicturesObjArr = dogPictureRepository.getRandomDogPicturesNotInList(picsSeen).toArray();
    if (dogPicturesObjArr.length > 0) {
      DogPicture[] dogPictures = Arrays.copyOf(dogPicturesObjArr, dogPicturesObjArr.length, DogPicture[].class);
      DogPicture dogPicture = dogPictures[0];

      log.info("GET " + "/dogpictures/info/random/ " + "Retrieving random info for " + dogPicture);
      return dogPicture;
    }
    throw new NoDogPicturesRemainingException();
  }

  @GetMapping("/dogpictures/info/random/{id}")
  public DogPicture getRandomDogPictureInfoWithoutId(@PathVariable Long id) {
    List<DogPicture> dogPictures = dogPictureRepository.getRandomDogPictures();
    DogPicture randomPicture = dogPictures.get(0);
    if (randomPicture.getId() == id) {
      randomPicture = dogPictures.get(1);
    }

    log.info("GET " + "/dogpictures/info/random/" + id + "/ " + "Retrieving random info for dog picture that does not have id " + id + ", retrieved " + randomPicture);
    return randomPicture;
  }
}