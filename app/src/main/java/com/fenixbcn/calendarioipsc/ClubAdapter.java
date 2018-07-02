package com.fenixbcn.calendarioipsc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class ClubAdapter extends ArrayAdapter {

    private Context mContext;
    private List<Club> mListaClubs;

    public ClubAdapter(@NonNull Context context, List<Club> listaClubs) {
        super(context, 0, listaClubs);
        this.mContext = context;
        this.mListaClubs = listaClubs;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String TAG = "Calendario Ipsc";
        View vista = convertView;

        if (vista == null) {

            vista = LayoutInflater.from(mContext).inflate(R.layout.single_club, parent, false);

        }

        Club clubActual = mListaClubs.get(position);

        CheckBox cbClub = (CheckBox) vista.findViewById(R.id.cbNombreClub);
        cbClub.setOnCheckedChangeListener((ListaClubsActivity) mContext); // evento que controla el cambio de estado del checkbox
        cbClub.setText(clubActual.getNombreclub());
        cbClub.setChecked(clubActual.isClubSelected());

        return vista;
    }
}
