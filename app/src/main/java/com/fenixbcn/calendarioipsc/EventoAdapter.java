package com.fenixbcn.calendarioipsc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventoAdapter extends ArrayAdapter {

    private Context mContext;
    private List<Evento> lListaEventos = new ArrayList<>();

    public EventoAdapter(@NonNull Context context, @NonNull ArrayList<Evento> listaEventos) {
        super(context, 0, listaEventos);

        mContext = context;
        lListaEventos = listaEventos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String TAG = "Calendario Ipsc";
        View vista= convertView;
        String nombreClub;

        if (vista == null) {

            vista = LayoutInflater.from(mContext).inflate(R.layout.single_evento,parent,false);
        }

        Evento eventoActual = lListaEventos.get(position);

        TextView tvTitulo = (TextView) vista.findViewById(R.id.tvTitulo);
        tvTitulo.setText(eventoActual.getTitulo());

        TextView tvFechaInicio = (TextView) vista.findViewById(R.id.tvFechaInicio);
        tvFechaInicio.setText(eventoActual.getFechaInico());

        TextView tvFechaFin = (TextView) vista.findViewById(R.id.tvFechaFin);
        tvFechaFin.setText(eventoActual.getFechaFin());

        nombreClub = eventoActual.getNombreClub(eventoActual.getTitulo());

        ImageView ivLogoClub = (ImageView) vista.findViewById(R.id.ivLogoClub);
        ivLogoClub.setImageResource(eventoActual.getIconClub(nombreClub));

        //Log.d(TAG, "el nombre del club es: " + eventoActual.getLogoClub(eventoActual.getTitulo()));

        return vista;
    }
}
