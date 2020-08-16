package com.justin.whatshouldicallfreddy.repos;

import java.util.List;

import com.justin.whatshouldicallfreddy.models.DogName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DogNameRepository extends JpaRepository<DogName, Long> {
  Long countByName(String name);

  @Query(value="SELECT d FROM DogName d WHERE d.id NOT IN :namesSeen")
  List<DogName> getDogNamesNotInList(@Param("namesSeen") Long[] namesSeen);
}