package com.justin.whatshouldicallfreddy.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.justin.whatshouldicallfreddy.models.DogPicture;
import com.justin.whatshouldicallfreddy.repos.DogPictureRepository;

import org.springframework.stereotype.Service;

@Service
public class DogPictureRepositoryService {
  DogPictureRepository dogPictureRepository;

  public DogPictureRepositoryService(DogPictureRepository dogPictureRepository) {
    this.dogPictureRepository = dogPictureRepository;
  }

  public Optional<DogPicture> findById(Long id) {
    return (dogPictureRepository.findById(id).map(pic -> pic.getFileName() != null ? pic : null));
  }

  public List<DogPicture> findAll() {
    return dogPictureRepository.findAll().stream().filter(b -> b.getFileName() != null).collect(Collectors.toList());
  }

  public List<DogPicture> getRandomDogPictures() {
    return dogPictureRepository.getRandomDogPictures();
  }

  public List<DogPicture> getRandomDogPicturesNotInList(Long[] picsSeen) {
    return dogPictureRepository.getRandomDogPicturesNotInList(picsSeen);
  }
}