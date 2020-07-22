package com.justin.whatshouldicallfreddy.repos;

import com.justin.whatshouldicallfreddy.models.DogName;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DogNameRepository extends JpaRepository<DogName, Long> {
  Long countByName(String name);
}