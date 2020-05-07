/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "providers")
@NoArgsConstructor @AllArgsConstructor
public class Provider implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 200, nullable = false, unique = true)
    private String label;
    @Column(length = 200)
    private String address;
    @Column(length = 200)
    private String email;
    @Column(length = 50)
    private String phone;
    @Column(length = 255)
    private String description;

    public static Provider update(Provider provider, String label, String email, 
            String address, String phone, String description) {
        provider.setLabel(label);
        provider.setEmail(email);
        provider.setPhone(phone);
        provider.setDescription(description);
        provider.setAddress(address);
        return provider;
    }

    public void empty() {
        this.id = 0;
        this.label = "";
        this.address = "";
        this.email = "";
        this.phone = "";
        this.description = "";
    }

    public boolean isEmpty() {
        return this.id == 0 && this.label.isEmpty()
                && this.address.isEmpty() && this.email.isEmpty()
                && this.phone.isEmpty() && this.description.isEmpty();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Provider other = (Provider) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public String toString() {return this.label;}

}
