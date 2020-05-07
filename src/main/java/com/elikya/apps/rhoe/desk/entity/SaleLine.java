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
@Table(name = "sale_lines")
@NoArgsConstructor @AllArgsConstructor
public class SaleLine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "product")
    private Product product;
    @ManyToOne(optional = false)
    @JoinColumn(name = "sale")
    private Sale sale;
    private int quantity;
    private BigDecimal price;
    @Transient
    private BigDecimal unitPrice;

    public SaleLine(Product product, Sale sale, int quantity, BigDecimal price) {
        this.product = product;
        this.sale = sale;
        this.quantity = quantity;
        this.price = price;
    }
    
    public void incrementQuantity(int value) {
        this.quantity += value;
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SaleLine other = (SaleLine) obj;
        if (this.quantity != other.quantity) return false;
        if (!Objects.equals(this.id, other.id)) return false;
        if (!Objects.equals(this.product.getId(), other.product.getId())) return false;
        return Objects.equals(this.sale.getId(), other.sale.getId());
    }
    
}
