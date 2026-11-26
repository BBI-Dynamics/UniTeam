package com.example.uniteam;

public class Usuario {
    private final long id;
    private final String nombre;
    private final String email;
    private final String fotoPath; // ruta del archivo de la foto (puede ser null)

    public Usuario(long id, String nombre, String email, String fotoPath) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fotoPath = fotoPath;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getFotoPath() { return fotoPath; }
}
