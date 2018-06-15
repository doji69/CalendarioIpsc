package com.fenixbcn.calendarioipsc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
