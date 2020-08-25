package com.justin.whatshouldicallfreddy.repos;

import java.util.List;

import com.justin.whatshouldicallfreddy.models.DogPicture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DogPictureRepository extends JpaRepository<DogPicture, Long> {
  @Query(value="SELECT d FROM DogPicture d order by function('RAND')")
  List<DogPicture> getRandomDogPictures();

  @Query(value="SELECT d FROM DogPicture d WHERE d.id NOT IN :picsSeen order by function('RAND')")
  List<DogPicture> getRandomDogPicturesNotInList(@Param("picsSeen") Long[] picsSeen);
}