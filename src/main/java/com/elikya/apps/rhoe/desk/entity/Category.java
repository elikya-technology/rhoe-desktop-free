/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Mafole Loemelah
 */
@Entity
@Getter @Setter @Builder
@Table(name = "categories")
@NoArgsConstructor @AllArgsConstructor
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 100, nullable = false, unique = true)
    private String label;
    @Column(length = 50, nullable = false)
    private String number;
    @Column(length = 200)
    private String description;
    @Transient
    private int productsNumber;
    @Transient
    private int productsQty;
    @Transient
    private BigDecimal productsStockPrice;
    @Transient
    private List<Product> products;

    public static Category update(Category category, String label, String number, String description) {
        category.setLabel(label);
        category.setNumber(number);
        category.setDescription(description);
        return category;
    }

    @Override
    public String toString() {return label;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Category other = (Category) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
}
