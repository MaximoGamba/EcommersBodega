package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Crianza;

@Repository 
public interface CrianzaRepository extends JpaRepository<Crianza, Long> { 

    Optional<Crianza> findByName(String name); 
}
