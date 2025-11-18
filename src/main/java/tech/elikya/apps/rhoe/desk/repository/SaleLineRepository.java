/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.repository;

import tech.elikya.apps.rhoe.desk.entity.SaleLine;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 *
 * @author Mafole Loemelah
 */
public interface SaleLineRepository extends JpaRepository<SaleLine, Integer> {

    @Query("SELECT S FROM SaleLine S WHERE S.sale.id = :id")
    List<SaleLine> queryBySaleId(@Param("id") int id);
    
    @Query("SELECT S FROM SaleLine S WHERE S.sale.id in :ids")
    List<SaleLine> queryBySales(@Param("ids") List<Integer> ids);
    
    @Query("SELECT S FROM SaleLine S WHERE S.id = (SELECT MAX(T.id) FROM SaleLine T)")
    SaleLine queryLastInserted();
    
    @Query("SELECT S FROM SaleLine S WHERE S.product.id = :id AND S.sale.saleDate BETWEEN :from AND :to")
    List<SaleLine> queryByProduct(@Param("id") int id, @Param("from") LocalDate from, @Param("to") LocalDate to);
    
    @Query("SELECT S FROM SaleLine S WHERE S.product.id = :id")
    List<SaleLine> queryByProduct(@Param("id") int id);

    @Query("SELECT s FROM SaleLine s WHERE s.product.id IN :ids")
    List<SaleLine> queryFromProductsIds(@Param("ids") Set<Integer> ids);

    @Query("SELECT MAX(s.id) FROM SaleLine s WHERE s.product.id IN :ids")
    Integer getMaxOfProductsIds(@Param("ids") Set<Integer> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM SaleLine s WHERE s.sale.id IN :ids")
    void deleteFromSalesIds(@Param("ids") List<Integer> ids);
    
}
