package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Color;

@Repository 
public interface ColorRepository extends JpaRepository<Color, Long> { 

    Optional<Color> findByName(String name); 
}
