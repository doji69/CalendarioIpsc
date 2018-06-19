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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventoAdapter extends BaseAdapter {

    private Context mContext;
    private List<Object> lListaEventos;
    String nombreClub;

    private static final int eventoItem = 0;
    private static final int headerItem = 1;
    private LayoutInflater layoutInflater;

    public EventoAdapter(@NonNull Context context, @NonNull List<Object> listaEventos) {

        this.lListaEventos = listaEventos;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getItemViewType(int position) {
        if (lListaEventos.get(position) instanceof Evento) {
            return eventoItem;
        } else {
            return headerItem;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return lListaEventos.size();
    }

    @Override
    public Object getItem(int i) {
        return lListaEventos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String TAG = "Calendario Ipsc";
        View vista = convertView;

        if (vista == null) {
            switch (getItemViewType(position)) {
                case eventoItem:
                    vista = layoutInflater.inflate(R.layout.single_evento, null);
                    break;
                case headerItem:
                    vista = layoutInflater.inflate(R.layout.listview_evento_header, null);
                    break;
            }
        }

        switch (getItemViewType(position)) {
            case eventoItem:

                TextView tvTitulo = (TextView) vista.findViewById(R.id.tvTitulo);
                TextView tvFechaInicio = (TextView) vista.findViewById(R.id.tvFechaInicio);
                TextView tvFechaFin = (TextView) vista.findViewById(R.id.tvFechaFin);
                ImageView ivLogoClub = (ImageView) vista.findViewById(R.id.ivLogoClub);

                tvTitulo.setText(((Evento)lListaEventos.get(position)).getTitulo());
                tvFechaInicio.setText(((Evento)lListaEventos.get(position)).getFechaInico());
                tvFechaFin.setText(((Evento)lListaEventos.get(position)).getFechaFin());

                nombreClub = ((Evento)lListaEventos.get(position)).getNombreClub(((Evento)lListaEventos.get(position)).getTitulo());

                ivLogoClub.setImageResource(((Evento)lListaEventos.get(position)).getIconClub(nombreClub));

                break;
            case headerItem:

                TextView tvTituloHeader = (TextView) vista.findViewById(R.id.tvTituloHeader);
                tvTituloHeader.setText((String)lListaEventos.get(position));

                break;
        }

        return vista;
    }
}
