package com.galvanize.repositories;

import com.galvanize.entities.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step, Long> {
}