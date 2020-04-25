/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mafole Loemelah
 */
@Entity
@Table(name = "tax")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Tax implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 100, unique = true, nullable = false)
    private String name;
    private BigDecimal percent;
    private BigDecimal cost;
    @Column(length = 100)
    private String description;
    @Transient
    private BigDecimal convertedCost;

    public Tax(String name, BigDecimal percent, BigDecimal cost, String description) {
        this.name = name;
        this.percent = percent;
        this.cost = cost;
        this.description = description;
    }
    
    public static Tax update(Tax tax, String name, BigDecimal percent, BigDecimal cost, String description) {
        tax.name = name;
        tax.cost = cost;
        tax.percent = percent;
        tax.description = description;
        return tax;
    }
}
