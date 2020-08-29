package com.justin.whatshouldicallfreddy.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.justin.whatshouldicallfreddy.LoadDatabase;
import com.justin.whatshouldicallfreddy.exceptions.DogNameNotFoundException;
import com.justin.whatshouldicallfreddy.exceptions.InvalidSecurityCredentialsException;
import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/admin/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://www.whatshouldicallfreddy.com"}, allowCredentials = "true") // TODO Make this legit
@RestController
public class WhatShouldICallFreddyAdminController {
  private final DogNameRepository dogNameRepository;
  // private final DogPictureRepository dogPictureRepository;
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  private String securityToken;

  public WhatShouldICallFreddyAdminController(DogNameRepository dogNameRepository) {
    this.dogNameRepository = dogNameRepository;

    try {
      InputStream fis = WhatShouldICallFreddyAdminController.class.getResourceAsStream("/security/security_token.txt");
      InputStreamReader isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);
      securityToken = br.readLine();
      br.close();
      isr.close();
      fis.close();
    }
    catch (Exception ex) {
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

  // @PostMapping("/{token}/dogpictures")
  // public DogPicture addDogPicture(@PathVariable String token, @RequestBody DogPicture newDogPicture) {
  //   verifySecurityToken(token, "POST " + "/dogpictures/");

  //   // return dogNameRepository.findById(id).map(dn -> {
  //   //   log.info("POST " + "/dognames/vote/" + id + "/" + vote + "/ " + "Increasing " + (vote ? "yes" : "no") + " votes for " + dn);
  //   //   if (vote) {
  //   //     dn.incYesVotes();
  //   //   }
  //   //   else {
  //   //     dn.incNoVotes();
  //   //   }
  //   //   return dogNameRepository.save(dn);
  //   // }).orElseThrow(() -> new DogNameNotFoundException(id));
  // }


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