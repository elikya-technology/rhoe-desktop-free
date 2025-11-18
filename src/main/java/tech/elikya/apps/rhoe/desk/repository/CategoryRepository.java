/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.repository;

import tech.elikya.apps.rhoe.desk.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Mafole Loemelah
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
