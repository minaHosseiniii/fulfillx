package com.fulfillx.customer.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    public Customer(
            String firstName,
            String lastName,
            String email,
            String phone
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.status = CustomerStatus.ACTIVE;
    }

    public void update(
            String firstName,
            String lastName,
            String email,
            String phone
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public void activate() {
        if (this.status == CustomerStatus.ACTIVE) {
            return;
        }
        this.status = CustomerStatus.ACTIVE;
    }

    public void deactivate() {
        if (this.status == CustomerStatus.INACTIVE) {
            return;
        }
        this.status = CustomerStatus.INACTIVE;
    }
}
