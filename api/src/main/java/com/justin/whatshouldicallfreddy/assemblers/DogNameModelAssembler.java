package com.justin.whatshouldicallfreddy.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.justin.whatshouldicallfreddy.WhatShouldICallFreddyController;
import com.justin.whatshouldicallfreddy.models.DogName;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DogNameModelAssembler implements RepresentationModelAssembler<DogName, EntityModel<DogName>> {
  @Override
  public EntityModel<DogName> toModel(DogName dogName) {
    return EntityModel.of(dogName,
      linkTo(methodOn(WhatShouldICallFreddyController.class).one(dogName.getId())).withSelfRel(),
      linkTo(methodOn(WhatShouldICallFreddyController.class).all()).withRel("dognames")
    );
  }
}