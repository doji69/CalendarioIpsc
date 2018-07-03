package com.fenixbcn.calendarioipsc;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Funciones {

    static public List<String> initIdCalendars(List<String> lIdCalendars) {

        lIdCalendars.add("ngjagh7og8ij1qicffe17ubtcc@group.calendar.google.com"); // Barcelona
        lIdCalendars.add("dne67tuddd0jrn2182igm783sc@group.calendar.google.com"); // Granollers
        //lIdCalendars.add("ilv3lk9c0fqnnodmi1t71ocv3g@group.calendar.google.com"); // Granollers privado
        lIdCalendars.add("kak1rooupa6ru9kt6vki5gmrs0@group.calendar.google.com"); // Jordi Tarragó (Tarragona)
        lIdCalendars.add("k0pmhg0b0i8l574n34c9im5r1s@group.calendar.google.com"); // Lleida
        lIdCalendars.add("e20gtq6h142m0vt4olgpmagvj0@group.calendar.google.com"); // Mataro
        lIdCalendars.add("nicucek3ijoo2dk21ucsthb2m0@group.calendar.google.com"); // Montsia
        lIdCalendars.add("epmhu8fts0ai17ot9fvmlr2r80@group.calendar.google.com"); // Osona
        lIdCalendars.add("3pplpsjb0rte6upt0ecielvrec@group.calendar.google.com"); // Platja d'Aro
        lIdCalendars.add("pngv5u4uit1opct9r4c7d74ofs@group.calendar.google.com"); // Sabadell
        lIdCalendars.add("2t466nuslhmr90u7odfc5bn1is@group.calendar.google.com"); // Terrassa
        //lIdCalendars.add("9slr1m12oodn74flqppp9nllng@group.calendar.google.com"); // Terrassa privado
        lIdCalendars.add("uo34u3j4mqd00e3h5g1kcm1928@group.calendar.google.com"); // Vilassar
        lIdCalendars.add("j36gq85ai9q4bp6325le90eig0@group.calendar.google.com"); // Agustina de Aragón Zaragoza
        lIdCalendars.add("ert4hkolipo06154v6p7k0c7co@group.calendar.google.com"); // Federacion Tiro
        lIdCalendars.add("uda111se8tkr02mg0e9hojjr1g@group.calendar.google.com"); // Igualada

        return lIdCalendars;
    }

    /**
     * de la lista de eventos recuperada de google calendar filtra aquellos que coinciden con la fecha selecionada y los devuelve en otra lista
     *
     * @param lCadenaEventos
     * @param selectedDate
     * @return
     */

    static public List<String> getDateEvents(List<String> lCadenaEventos, Date selectedDate) {

        String TAG = "Calendario Ipsc";
        List<String> lCadenaEventosSel = new ArrayList<String>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicial = null; // fecha en la que empieza el evento
        Date fechaFinal = null; // fecha en la que termina el evento

        for (int i = 0; i < lCadenaEventos.size(); i++) {

            String eventoTirada = lCadenaEventos.get(i);
            String[] vEventoTirada = eventoTirada.split(" - ");

            try {
                if (vEventoTirada.length == 5) {
                    fechaInicial = dateFormat.parse(vEventoTirada[3]);
                    fechaFinal = dateFormat.parse(vEventoTirada[4]);
                } else if (vEventoTirada.length == 4) {
                    fechaInicial = dateFormat.parse(vEventoTirada[2]);
                    fechaFinal = dateFormat.parse(vEventoTirada[3]);
                } else if (vEventoTirada.length == 3) {
                    fechaInicial = dateFormat.parse(vEventoTirada[1]);
                    fechaFinal = dateFormat.parse(vEventoTirada[2]);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (fechaInicial.getTime() == selectedDate.getTime()) {

                //Log.d(TAG, "las fechas son iguale: " + fechaInicial + "-" + selectedDate);
                lCadenaEventosSel.add(lCadenaEventos.get(i));

            } else if ((fechaInicial.getTime() < selectedDate.getTime()) && (fechaFinal.getTime() > selectedDate.getTime())) {

                //Log.d(TAG, "la fecha seleccionada " + selectedDate + " esta entre: " + fechaInicial + "-" + fechaFinal);
                lCadenaEventosSel.add(lCadenaEventos.get(i));
            } else if (fechaFinal.getTime() == selectedDate.getTime()) {

                //Log.d(TAG, "las fechas son iguale: " + fechaFinal + "-" + selectedDate);
                lCadenaEventosSel.add(lCadenaEventos.get(i));
            }

        }

        return lCadenaEventosSel;
    }

    static public String setDateTimeFormat(String dateTime) {

        Locale spanish = new Locale("es", "ES");
        SimpleDateFormat inputDateFormat = null;
        SimpleDateFormat outputDateFormat = null;
        String formattedDateTime = "";
        Date date = null;

        if (dateTime.length() == 10) {
            inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            outputDateFormat = new SimpleDateFormat("dd MMM yyyy");
        } else {
            inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'.000'z", spanish);
            outputDateFormat = new SimpleDateFormat("dd MMM yyyy k:mm ");
        }

        try {
            date = inputDateFormat.parse(dateTime);
            formattedDateTime = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDateTime;
    }

    static public List<String> orderEventsByDate(List<String> lCadenaEventos) {

        String TAG = "Calendario Ipsc";
        List<String> lCadenaEventosOrdered = new ArrayList<String>();
        List<NumDiasPosicion> lNumdias = new ArrayList<NumDiasPosicion>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicioOrden = null; // fecha inicio del orden
        Date fechaInicial = null; // fecha en la que empieza el evento

        int diasInicio = 0; // guardamos la diferencia de dias entre la fecha inicio del orden y la de inicio del evento

        // Obtenemos la fecha inicio del orden creada en un string
        Calendar calendar = Calendar.getInstance(); // necesitamos crear la instacia de Calendar para luego obtener el año
        int year = calendar.get(Calendar.YEAR);
        year -= 1; // quiero ordenar desde un año antes a actual

        String sFirstDay = year + "-01-01";

        try {
            fechaInicioOrden = dateFormat.parse(sFirstDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // fin Obtenemos la fecha inicio del orden creada en un string


        /* recorrido por la lista de eventos para saber obtener la fecha de inicio del evento y restar
        con la fecha de inico del orden. Guardamos la diferencia de dias en una lista de interegers.
        El indice de la lista sera el indice para saber que evento es*/
        for (int i = 0; i < lCadenaEventos.size(); i++) {
            String eventoTirada = lCadenaEventos.get(i);
            String[] vEventoTirada = eventoTirada.split(" - ");

            try {
                if (vEventoTirada.length == 5) {
                    fechaInicial = dateFormat.parse(vEventoTirada[3]);

                } else if (vEventoTirada.length == 4) {
                    fechaInicial = dateFormat.parse(vEventoTirada[2]);

                } else if (vEventoTirada.length == 3) {
                    fechaInicial = dateFormat.parse(vEventoTirada[1]);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            diasInicio = (int) ((fechaInicial.getTime() - fechaInicioOrden.getTime()) / 86400000);

            lNumdias.add(new NumDiasPosicion(diasInicio, i));
        }

        /* ordena la lista de menor a major por el campo numdias de la clase NumDiasPosicion*/
        Collections.sort(lNumdias, new Comparator<NumDiasPosicion>() {
            @Override
            public int compare(NumDiasPosicion numDiasPosicion, NumDiasPosicion t1) {
                return numDiasPosicion.getNumDias() - t1.getNumDias();
            }
        });
        /* fin ordena la lista de menor a major por el campo numdias de la clase NumDiasPosicion */

        for (int j = 0; j < lNumdias.size(); j++) {

            lCadenaEventosOrdered.add(lCadenaEventos.get(lNumdias.get(j).getPosicionEvento()));
        }

        return lCadenaEventosOrdered;
    }

    static public List<String> orderEventsByDateWithHeader(List<String> lCadenaEventosOrdered) {
        String TAG = "Calendario Ipsc";
        List<String> lCadenaEventosOrderedHeader = new ArrayList<String>();

        SimpleDateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatOutput = new SimpleDateFormat("dd MMM yyyy");
        Date fechaInicial1 = null; // fecha inicio del orden
        Date fechaInicial2 = null; // fecha en la que empieza el evento

        String sFechaInicial1 = null;
        String sFechaInicial2 = "2017-01-01";

        try {
            fechaInicial2 = dateFormatInput.parse(sFechaInicial2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* recorrido por la lista de eventos para saber obtener la fecha de inicio del evento y restar
        con la fecha de inico del orden. Guardamos la diferencia de dias en una lista de interegers.
        El indice de la lista sera el indice para saber que evento es*/
        for (int i = 0; i < lCadenaEventosOrdered.size(); i++) {
            String eventoTirada = lCadenaEventosOrdered.get(i);
            String[] vEventoTirada = eventoTirada.split(" - ");

            try {
                if (vEventoTirada.length == 5) {
                    fechaInicial1 = dateFormatInput.parse(vEventoTirada[3]);

                } else if (vEventoTirada.length == 4) {
                    fechaInicial1 = dateFormatInput.parse(vEventoTirada[2]);

                } else if (vEventoTirada.length == 3) {
                    fechaInicial1 = dateFormatInput.parse(vEventoTirada[1]);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (fechaInicial1.getTime() != fechaInicial2.getTime()) {

                sFechaInicial1 = dateFormatOutput.format(fechaInicial1);
                lCadenaEventosOrderedHeader.add(sFechaInicial1);
                lCadenaEventosOrderedHeader.add(lCadenaEventosOrdered.get(i));
                fechaInicial2 = fechaInicial1;

            } else {

                lCadenaEventosOrderedHeader.add(lCadenaEventosOrdered.get(i));
            }
        }
        return lCadenaEventosOrderedHeader;
    }

    static public LatLng getLocation(String selectedTitulo) {

        Boolean nombreClubExists;
        String[] nombresClubs = {"Barcelona", "Granollers", "Igualada", "Jordi Tarragó", "Lleida", "Mataró", "Montsià",
                "Montsia", "Osona", "Platja d'Aro", "R.T.A.A.", "Sabadell", "Terrassa", "Vilassar", "Hontanares de Eresma",
                "As Pontes", "Huesca", "Valdemoro"};
        LatLng latPosition = null;

        for (int i = 0; i < nombresClubs.length; i++) {

            nombreClubExists = selectedTitulo.contains(nombresClubs[i]);
            if (nombreClubExists == true) {

                switch (nombresClubs[i]) {
                    case "Barcelona":
                        latPosition = new LatLng(41.3695149, 2.1701805);
                        break;
                    case "Granollers":
                        latPosition = new LatLng(41.6173887, 2.2704919);
                        break;
                    case "Igualada":
                        latPosition = new LatLng(41.5833731, 1.6758845);
                        break;
                    case "Jordi Tarragó":
                        latPosition = new LatLng(41.1633502, 1.2416613);
                        break;
                    case "Lleida":
                        latPosition = new LatLng(41.6034722, 0.6058056);
                        break;
                    case "Mataró":
                        latPosition = new LatLng(41.576215, 2.420951);
                        break;
                    case "Montsià":
                        latPosition = new LatLng(40.685412, 0.543492);
                        break;
                    case "Montsia":
                        latPosition = new LatLng(40.685412, 0.543492);
                        break;
                    case "Osona":
                        latPosition = new LatLng(41.973305, 2.271611);
                        break;
                    case "Platja d'Aro":
                        latPosition = new LatLng(41.8080069, 3.0285842);
                        break;
                    case "R.T.A.A.":
                        latPosition = new LatLng(41.461502, -0.704428);
                        break;
                    case "Sabadell":
                        latPosition = new LatLng(41.5215577, 2.0990669);
                        break;
                    case "Terrassa":
                        latPosition = new LatLng(41.59458, 2.03766);
                        break;
                    case "Vilassar":
                        latPosition = new LatLng(41.50611, 2.38046);
                        break;
                    case "Hontanares de Eresma":
                        latPosition = new LatLng(40.9965688, -4.1976809);
                        break;
                    case "As Pontes":
                        latPosition = new LatLng(43.4100612, -7.8611117);
                        break;
                    case "Huesca":
                        latPosition = new LatLng(42.1717392, -0.4046484);
                        break;
                    case "Valdemoro":
                        latPosition = new LatLng(40.1751297, -3.6524834);
                        break;
                    default:
                        latPosition = null;

                }
            }
        }

        return latPosition;
    }

    static public String getCalendarId(String nombreClub) {

        String calendarId = "";

        switch (nombreClub) {

            case "Granollers":
                calendarId = "dne67tuddd0jrn2182igm783sc@group.calendar.google.com";
                break;
            case "Barcelona":
                calendarId = "ngjagh7og8ij1qicffe17ubtcc@group.calendar.google.com";
                break;
            case "Igualada":
                calendarId = "uda111se8tkr02mg0e9hojjr1g@group.calendar.google.com";
                break;
            case "Jordi Tarragó":
                calendarId = "kak1rooupa6ru9kt6vki5gmrs0@group.calendar.google.com";
                break;
            case "Lleida":
                calendarId = "k0pmhg0b0i8l574n34c9im5r1s@group.calendar.google.com";
                break;
            case "Mataró":
                calendarId = "e20gtq6h142m0vt4olgpmagvj0@group.calendar.google.com";
                break;
            case "Montsià":
                calendarId = "nicucek3ijoo2dk21ucsthb2m0@group.calendar.google.com";
                break;
            case "Osona":
                calendarId = "epmhu8fts0ai17ot9fvmlr2r80@group.calendar.google.com";
                break;
            case "Platja d'Aro":
                calendarId = "3pplpsjb0rte6upt0ecielvrec@group.calendar.google.com";
                break;
            case "R.T.A.A.":
                calendarId = "j36gq85ai9q4bp6325le90eig0@group.calendar.google.com";
                break;
            case "Sabadell":
                calendarId = "pngv5u4uit1opct9r4c7d74ofs@group.calendar.google.com";
                break;
            case "Terrassa":
                calendarId = "2t466nuslhmr90u7odfc5bn1is@group.calendar.google.com";
                break;
            case "Vilassar":
                calendarId = "uo34u3j4mqd00e3h5g1kcm1928@group.calendar.google.com";
                break;
            case "RFEDETO":
                calendarId = "ert4hkolipo06154v6p7k0c7co@group.calendar.google.com";
                break;
        }

        return calendarId;
    }

    static public String getNombreClubByCalendarId(String idCalendar) {

        String nombreClub = "";

        switch (idCalendar) {

            case "dne67tuddd0jrn2182igm783sc@group.calendar.google.com":
                nombreClub = "Granollers";
                break;
            case "ngjagh7og8ij1qicffe17ubtcc@group.calendar.google.com":
                nombreClub = "Barcelona";
                break;
            case "uda111se8tkr02mg0e9hojjr1g@group.calendar.google.com":
                nombreClub = "Igualada";
                break;
            case "kak1rooupa6ru9kt6vki5gmrs0@group.calendar.google.com":
                nombreClub = "Jordi Tarragó";
                break;
            case "k0pmhg0b0i8l574n34c9im5r1s@group.calendar.google.com":
                nombreClub = "Lleida";
                break;
            case "e20gtq6h142m0vt4olgpmagvj0@group.calendar.google.com":
                nombreClub = "Mataró";
                break;
            case "nicucek3ijoo2dk21ucsthb2m0@group.calendar.google.com":
                nombreClub = "Montsià";
                break;
            case "epmhu8fts0ai17ot9fvmlr2r80@group.calendar.google.com":
                nombreClub = "Osona";
                break;
            case "3pplpsjb0rte6upt0ecielvrec@group.calendar.google.com":
                nombreClub = "Platja d'Aro";
                break;
            case "j36gq85ai9q4bp6325le90eig0@group.calendar.google.com":
                nombreClub = "R.T.A.A.";
                break;
            case "pngv5u4uit1opct9r4c7d74ofs@group.calendar.google.com":
                nombreClub = "Sabadell";
                break;
            case "2t466nuslhmr90u7odfc5bn1is@group.calendar.google.com":
                nombreClub = "Terrassa";
                break;
            case "uo34u3j4mqd00e3h5g1kcm1928@group.calendar.google.com":
                nombreClub = "Vilassar";
                break;
            case "ert4hkolipo06154v6p7k0c7co@group.calendar.google.com":
                nombreClub = "RFEDETO";
                break;
        }

        return nombreClub;
    }

}
