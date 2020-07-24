package com.justin.whatshouldicallfreddy.repos;

import java.util.List;

import com.justin.whatshouldicallfreddy.models.DogPicture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DogPictureRepository extends JpaRepository<DogPicture, Long> {
  @Query(value="SELECT d FROM DogPicture d order by function('RAND')")
  List<DogPicture> getRandomDogPictures();

  @Query(value="SELECT id FROM DogPicture d order by function('RAND')")
  List<Long> getRandomDogPictureIDs();
}