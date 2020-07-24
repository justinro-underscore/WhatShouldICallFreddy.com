package com.justin.whatshouldicallfreddy;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.justin.whatshouldicallfreddy.assemblers.DogNameModelAssembler;
import com.justin.whatshouldicallfreddy.exceptions.DogNameExistsException;
import com.justin.whatshouldicallfreddy.exceptions.DogNameNotFoundException;
import com.justin.whatshouldicallfreddy.exceptions.DogPictureNotFoundException;
import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.models.DogPicture;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;
import com.justin.whatshouldicallfreddy.repos.DogPictureRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
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

@CrossOrigin
@RestController
public class WhatShouldICallFreddyController {
  private final DogNameRepository dogNameRepository;
  private final DogNameModelAssembler dogNameAssembler;
  private final DogPictureRepository dogPictureRepository;
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  public WhatShouldICallFreddyController(DogNameRepository dogNameRepository, DogNameModelAssembler dogNameAssembler, DogPictureRepository dogPictureRepository) {
    this.dogNameRepository = dogNameRepository;
    this.dogNameAssembler = dogNameAssembler;
    this.dogPictureRepository = dogPictureRepository;
  }

  // Dog Names

  @GetMapping("/dognames")
  public CollectionModel<EntityModel<DogName>> allDogNames() {
    List<EntityModel<DogName>> dogNames = dogNameRepository.findAll().stream()
      .map(dogNameAssembler::toModel).collect(Collectors.toList());

    log.info("GET " + "/dognames " + "Retrieving " + dogNames.size() + " names");
    return CollectionModel.of(dogNames,
      linkTo(methodOn(WhatShouldICallFreddyController.class).allDogNames()).withSelfRel()
    );
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
  public EntityModel<DogName> oneDogName(@PathVariable Long id) {
    DogName dogName = dogNameRepository.findById(id).orElseThrow(() -> new DogNameNotFoundException(id));
    log.info("GET " + "/dognames/" + id + "/ " + "Retrieving " + dogName);
    return EntityModel.of(dogName,
      linkTo(methodOn(WhatShouldICallFreddyController.class).oneDogName(dogName.getId())).withSelfRel(),
      linkTo(methodOn(WhatShouldICallFreddyController.class).dogNameVote(dogName.getId(), true)).withRel("dognames/vote"),
      linkTo(methodOn(WhatShouldICallFreddyController.class).allDogNames()).withRel("dognames")
    );
  }

  @PutMapping("/dognames/{id}")
  public DogName replaceDogName(@RequestBody DogName newDogName, @PathVariable Long id) {
    return dogNameRepository.findById(id).map(dogName -> {
      log.info("PUT " + "/dognames/" + id + "/ " + "Replacing " + dogName + " with " + newDogName);
      dogName.setName(newDogName.getName());
      dogName.setYesVotes(newDogName.getYesVotes());
      dogName.setNoVotes(newDogName.getNoVotes());
      return dogNameRepository.save(dogName);
    }).orElseGet(() -> {
      newDogName.setId(id);
      log.info("PUT " + "/dognames/" + id + "/ " + "Dog not found, creating " + newDogName);
      return dogNameRepository.save(newDogName);
    });
  }

  @DeleteMapping("/dognames/{id}")
  public void deleteDogName(@PathVariable Long id) {
    log.info("DELETE " + "/dognames/" + id + "/ " + "Deleting dog name with id " + id);
    dogNameRepository.deleteById(id);
  }

  // Increase votes

  @PostMapping("/dognames/vote/{id}/{vote}")
  public EntityModel<DogName> dogNameVote(@PathVariable Long id, @PathVariable Boolean vote) {
    DogName dogName = dogNameRepository.findById(id).map(dn -> {
      log.info("POST " + "/dognames/vote/" + id + "/" + vote + "/ " + "Increasing " + (vote ? "yes" : "no") + " votes for " + dn);
      if (vote) {
        dn.incYesVotes();
      }
      else {
        dn.incNoVotes();
      }
      return dogNameRepository.save(dn);
    }).orElseThrow(() -> new DogNameNotFoundException(id));
    return EntityModel.of(dogName,
      linkTo(methodOn(WhatShouldICallFreddyController.class).dogNameVote(dogName.getId(), vote)).withSelfRel(),
      linkTo(methodOn(WhatShouldICallFreddyController.class).oneDogName(dogName.getId())).withRel("dognames/" + id),
      linkTo(methodOn(WhatShouldICallFreddyController.class).allDogNames()).withRel("dognames")
    );
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

  @GetMapping("/dogpictures/randomid")
  public Long getRandomDogPictureID() {
    List<Long> dogPictureIDs = dogPictureRepository.getRandomDogPictureIDs();

    log.info("GET " + "/dogpictures/randomid/ " + "Retrieving random ID " + dogPictureIDs.get(0));
    return dogPictureIDs.get(0);
  }

  @GetMapping("/dogpictures/randomid/{id}")
  public Long getRandomDogPictureID(@PathVariable Long id) {
    List<Long> dogPictureIDs = dogPictureRepository.getRandomDogPictureIDs();
    Long randomID = dogPictureIDs.get(0);
    if (randomID == id) {
      randomID = dogPictureIDs.get(1);
    }

    log.info("GET " + "/dogpictures/randomid/" + id + "/ " + "Retrieving random ID that is not " + id + ", retrieved " + dogPictureIDs.get(0));
    return randomID;
  }
}