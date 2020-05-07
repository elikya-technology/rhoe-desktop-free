/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
@Table(name = "sales")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Sale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 50, unique = true, nullable = false)
    private String number;
    @Column(nullable = false)
    private LocalDate saleDate;
    @Column(nullable = false)
    private LocalTime saleTime;
    @Column(nullable = false)
    private BigDecimal totalPrice;
    @Column(nullable = false)
    private BigDecimal taxedPrice;
    @Column(nullable = false)
    private BigDecimal moneyReceived;
    @Column(nullable = false, length = 20)
    private String currency;
    @Column(nullable = false)
    private double rate;
    @Transient
    private List<SaleLine> lines;

    public static Sale update(Sale sale, BigDecimal totalPrice, BigDecimal taxedPrice
            , String actionCurrency, double currencyRate) {
        sale.setTaxedPrice(taxedPrice);
        sale.setTotalPrice(totalPrice);
        sale.setCurrency(actionCurrency);
        sale.setRate(currencyRate);
        return sale;
    }
    
}
