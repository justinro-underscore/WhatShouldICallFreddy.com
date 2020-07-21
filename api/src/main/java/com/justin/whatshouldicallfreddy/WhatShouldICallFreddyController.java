package com.justin.whatshouldicallfreddy;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import com.justin.whatshouldicallfreddy.assemblers.DogNameModelAssembler;
import com.justin.whatshouldicallfreddy.exceptions.DogNameNotFoundException;
import com.justin.whatshouldicallfreddy.models.DogName;
import com.justin.whatshouldicallfreddy.repos.DogNameRepository;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhatShouldICallFreddyController {
  private final DogNameRepository dogNameRepository;

  private final DogNameModelAssembler dogNameAssembler;

  public WhatShouldICallFreddyController(DogNameRepository dogNameRepository, DogNameModelAssembler dogNameAssembler) {
    this.dogNameRepository = dogNameRepository;
    this.dogNameAssembler = dogNameAssembler;
  }

  @GetMapping("/dognames")
  public CollectionModel<EntityModel<DogName>> all() {
    List<EntityModel<DogName>> dogNames = dogNameRepository.findAll().stream()
      .map(dogNameAssembler::toModel).collect(Collectors.toList());

    return CollectionModel.of(dogNames,
      linkTo(methodOn(WhatShouldICallFreddyController.class).all()).withSelfRel()
    );
  }

  @PostMapping("/dognames")
  public DogName newDogName(@RequestBody DogName newDogName) {
    return dogNameRepository.save(newDogName);
  }

  // Single item

  @GetMapping("/dognames/{id}")
  public EntityModel<DogName> one(@PathVariable Long id) {
    DogName dogName = dogNameRepository.findById(id).orElseThrow(() -> new DogNameNotFoundException(id));
    return EntityModel.of(dogName,
      linkTo(methodOn(WhatShouldICallFreddyController.class).one(dogName.getId())).withSelfRel(),
      linkTo(methodOn(WhatShouldICallFreddyController.class).dogNameVote(dogName.getId(), true)).withRel("dognames/vote"),
      linkTo(methodOn(WhatShouldICallFreddyController.class).all()).withRel("dognames")
    );
  }

  @PutMapping("/dognames/{id}")
  public DogName replaceDogName(@RequestBody DogName newDogName, @PathVariable Long id) {
    return dogNameRepository.findById(id).map(dogName -> {
      dogName.setName(newDogName.getName());
      dogName.setYesVotes(newDogName.getYesVotes());
      dogName.setNoVotes(newDogName.getNoVotes());
      return dogNameRepository.save(dogName);
    }).orElseGet(() -> {
      newDogName.setId(id);
      return dogNameRepository.save(newDogName);
    });
  }

  @DeleteMapping("/dognames/{id}")
  public void deleteDogName(@PathVariable Long id) {
    dogNameRepository.deleteById(id);
  }

  // Increase votes

  @PostMapping("/dognames/vote/{id}/{vote}")
  public EntityModel<DogName> dogNameVote(@PathVariable Long id, @PathVariable Boolean vote) {
    DogName dogName = dogNameRepository.findById(id).map(dn -> {
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
      linkTo(methodOn(WhatShouldICallFreddyController.class).one(dogName.getId())).withRel("dognames/" + id),
      linkTo(methodOn(WhatShouldICallFreddyController.class).all()).withRel("dognames")
    );
  }
}