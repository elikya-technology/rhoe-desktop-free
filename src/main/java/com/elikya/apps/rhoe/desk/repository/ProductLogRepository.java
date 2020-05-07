/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.repository;

import com.elikya.apps.rhoe.desk.entity.ProductLog;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 *
 * @author Mafole Loemelah
 */
public interface ProductLogRepository extends JpaRepository<ProductLog, Integer>{

    @Query("SELECT p FROM ProductLog p WHERE p.product.id = :id AND p.logDate BETWEEN :from AND :to")
    List<ProductLog> queryByProduct(@Param("id") int id, @Param("from") LocalDate from, @Param("to") LocalDate to);
    
    @Query("SELECT p FROM ProductLog p WHERE p.product.id = :id")
    List<ProductLog> queryByProduct(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductLog p WHERE p.product.id IN :ids")
    void deleteByProductsIds(@Param("ids") List<Integer> ids);

    @Query("SELECT p FROM ProductLog p WHERE p.logDate BETWEEN :from AND :to AND p.product.id IN :ids")
    List<ProductLog> findByProductsIdsBetweenDates(@Param("ids") List<Integer> ids, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT p FROM ProductLog p WHERE p.product.id IN :ids")
    List<ProductLog> findByProductsIds(@Param("ids") List<Integer> ids);

}
