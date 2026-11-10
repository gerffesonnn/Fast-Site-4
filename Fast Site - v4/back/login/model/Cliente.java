package com.login.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;
    private String cpf;
    private String address;

    @Column(length = 1000)
    private String observation;

    @Column(name = "address_number", nullable = true)
    private String addressNumber;

    @Column(nullable = true)
    private String cep;

    public Cliente() {}

    public Cliente(String name, String phone, String email, String cpf, String address, String observation) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cpf = cpf;
        this.address = address;
        this.observation = observation;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public String getAddressNumber() { return addressNumber; }
    public void setAddressNumber(String addressNumber) { this.addressNumber = addressNumber; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
}