package com.galvanize.repositories;

import com.galvanize.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientsRepository extends JpaRepository<Ingredient, Long> {
    Ingredient findByName(String name);
}