package com.uade.tpo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Wine;

@Repository 
public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> { 
}
