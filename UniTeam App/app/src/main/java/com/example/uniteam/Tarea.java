package com.example.uniteam;

public class Tarea {
    private final long id;
    private final long usuarioId;
    private final String creadorNombre;
    private final String titulo;
    private final String materia;
    private final String descripcion;
    private final String importancia;
    private final boolean completada;

    public Tarea(long id, long usuarioId, String creadorNombre, String titulo,
                 String materia, String descripcion, String importancia, boolean completada) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.creadorNombre = creadorNombre;
        this.titulo = titulo;
        this.materia = materia;
        this.descripcion = descripcion;
        this.importancia = importancia;
        this.completada = completada;
    }

    public long getId() { return id; }
    public long getUsuarioId() { return usuarioId; }
    public String getCreadorNombre() { return creadorNombre; }
    public String getTitulo() { return titulo; }
    public String getMateria() { return materia; }
    public String getDescripcion() { return descripcion; }
    public String getImportancia() { return importancia; }
    public boolean isCompletada() { return completada; }
}