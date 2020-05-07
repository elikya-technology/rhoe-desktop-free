/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.repository;

import com.elikya.apps.rhoe.desk.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 *
 * @author Mafole Loemelah
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT T FROM Product T WHERE T.id = (SELECT MAX(S.id) FROM Product S)")
    Product getLastInserted();

    @Query("SELECT T FROM  Product T WHERE T.category.id = :id")
    List<Product> getFromCategory(@Param("id") int id);

    @Query("SELECT T FROM Product T WHERE T.provider.id = :id")
    List<Product> getFromProvider(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = :qty WHERE p.id = :id")
    void updateProductQty(@Param("id") int id, @Param("qty") int qty);
    
}
