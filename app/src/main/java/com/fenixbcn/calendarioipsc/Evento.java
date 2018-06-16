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

    public String getNombreClub (String titulo) {

        Boolean logoClubExists;
        String [] nombresClubs = {"Barcelona", "Granollers", "Jordi Tarragó","Lleida","Mataró","Montsia",
                "Osona","Platja d'Aro","Sabadell","Terrassa","Vilassar","R.T.A.A.","RFEDETO"};
        String nombreClub="";

        for (int i = 0; i < nombresClubs.length; i++) {

            logoClubExists = titulo.contains(nombresClubs[i]);
            if (logoClubExists==true) {

                nombreClub = nombresClubs[i];
            }
        }

        return nombreClub;
    }

    public int getIconClub (String nombreClub) {

        int iconClub=0;

        switch (nombreClub) {

            case "Granollers":
                iconClub = R.mipmap.club_granollers_ico;
                break;
            case "Barcelona":
                iconClub = R.mipmap.club_barcelona_ico;
                break;
            case "Jordi Tarragó":
                iconClub = R.mipmap.club_jordi_arrago_ico;
                break;
            case "Lleida":
                iconClub = R.mipmap.club_lleida_ico;
                break;
            case "Mataró":
                iconClub = R.mipmap.club_mataro_ico;
                break;
            case "Montsia":
                iconClub = R.mipmap.club_montsia_ico;
                break;
            case "Osona":
                iconClub = R.mipmap.club_osona_ico;
                break;
            case "Terrassa":
                iconClub = R.mipmap.club_terrassa_ico;
                break;
            case "Vilassar":
                iconClub = R.mipmap.club_vilassar_ico;
                break;
            case "RFEDETO":
                iconClub = R.mipmap.federacion_tiro_olimpico_ico;
                break;
            default:
                iconClub = R.mipmap.ic_launcher;
        }

        return iconClub;
    }


}
