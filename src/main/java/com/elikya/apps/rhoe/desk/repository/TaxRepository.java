/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.repository;

import com.elikya.apps.rhoe.desk.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Mafole Loemelah
 */
public interface TaxRepository extends JpaRepository<Tax, Integer> {

    @Query("SELECT T FROM Tax T WHERE T.id = (SELECT MAX(S.id) FROM Tax S)")
    public Tax queryLastInserted();
    
}
