/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;

import lombok.*;

/**
 *
 * @author Mafole Loemelah
 */
@Entity
@Getter @Setter @Builder
@Table(name = "products_logs")
@ToString
@AllArgsConstructor @NoArgsConstructor
public class ProductLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "product")
    private Product product;
    @Column(nullable = false, name = "log_date")
    private LocalDate logDate;
    @Column(nullable = false, name = "log_time")
    private LocalTime logTime;
    @Column(nullable = false, name = "stock_qty")
    private int stockQty;
    @Column(nullable = false, name = "unit_price")
    private BigDecimal unitPrice;
    @Column(nullable = false, name = "action_qty")
    private int actionQty;
    @Column(nullable = false, length = 50, name = "log_action")
    private String logAction;
    @Column(nullable = false, length = 50, name = "actual_currency")
    private String actualCurrency;
    @Column(nullable = false, name = "currency_rate")
    private double currencyRate;
    @Column
    private String reason;
    @Transient
    private BigDecimal totalPrice;

}
