package com.fenixbcn.calendarioipsc;

public class Evento {

    String titulo, fechaInico, fechaFin;

    public Evento (String tTitulo, String tFechaInicio, String tFechaFin) {

        this.titulo = tTitulo;
        this.fechaInico = tFechaInicio;
        this.fechaFin = tFechaFin;

    }

    public String getTitulo() {
        return titulo;
    }

    public String getFechaInico() {
        return fechaInico;
    }

    public String getFechaFin() {
        return fechaFin;
    }
}
