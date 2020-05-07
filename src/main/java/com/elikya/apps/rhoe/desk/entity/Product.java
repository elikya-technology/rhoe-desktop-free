/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
@NoArgsConstructor @AllArgsConstructor
@Table(name = "products", uniqueConstraints 
        = {@UniqueConstraint(columnNames = {"label", "serial_number"})})
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 100, nullable = false, name = "label")
    private String label;
    @Column(length = 100)
    private String number;
    @Column(length = 100, name = "serial_number")
    private String serialNumber;
    @Column(length = 150)
    private String barCode;
    @ManyToOne(optional = false)
    @JoinColumn(name = "provider")
    private Provider provider;
    @ManyToOne(optional = false)
    @JoinColumn(name = "category")
    private Category category;
    @Column(nullable = false) 
    private int maximumQuantity;
    @Column(nullable = false)
    private int minimumQuantity;
    @Column(nullable = false)
    private int stockQuantity;
    @Column(nullable = false)
    private BigDecimal unitPrice;
    @Transient
    private BigDecimal unitPriceTax;
    @Transient
    private BigDecimal convertedUnitPriceTax;
    @Transient
    private BigDecimal convertedUnitPrice;
    @Transient
    private BigDecimal stockPrice;
    @Transient
    private BigDecimal stockPriceTax;

    public static Product update(Product product, String label, String number, String serialNumber,
            String barCode, Provider provider, Category category, int maximumQuantity,
            int minimumQuantity, int stockQuantity, BigDecimal unitPrice) {
        product.setLabel(label);
        product.setNumber(number);
        product.setBarCode(barCode);
        product.setProvider(provider);
        product.setCategory(category);
        product.setMaximumQuantity(maximumQuantity);
        product.setMinimumQuantity(minimumQuantity);
        product.setStockQuantity(stockQuantity);
        product.setUnitPrice(unitPrice);
        product.setSerialNumber(serialNumber);
        return product;
    }
    
    public void decreaseQuantity(int value) {this.stockQuantity -= value;}
    
    public void increaseQuantity(int value) {this.stockQuantity += value;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
