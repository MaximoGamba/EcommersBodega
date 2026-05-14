package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Cepa;

@Repository 
public interface CepaRepository extends JpaRepository<Cepa, Long> { 

    Optional<Cepa> findByName(String name); 
}
