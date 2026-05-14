package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Elaboracion;

@Repository 
public interface ElaboracionRepository extends JpaRepository<Elaboracion, Long> { 

    Optional<Elaboracion> findByName(String name); 
}
