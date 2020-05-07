/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.repository;

import com.elikya.apps.rhoe.desk.entity.Sale;
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
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    @Query("SELECT T FROM Sale T WHERE T.id = (SELECT MAX(S.id) FROM Sale S)")
    Sale getLastInserted();
    
    @Query("SELECT T FROM Sale T WHERE T.saleDate BETWEEN :from AND :to")
    List<Sale> getByPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT T FROM Sale T WHERE T.id IN :ids")
    List<Sale> getFromIds(@Param("ids") List<Integer> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM Sale s WHERE s.id IN :ids")
    void deleteFromIds(@Param("ids") List<Integer> ids);
    
}
