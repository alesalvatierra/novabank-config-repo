package com.novabank.cliente.dto;

public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;

    //Constructor vacío necesario para Spring
    public ClienteDTO() {}

    //Constructor con todos los campos
    public ClienteDTO(Long id, String nombre, String apellidos, String dni, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
    }

    //Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
