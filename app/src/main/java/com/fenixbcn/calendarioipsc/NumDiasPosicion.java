package com.fenixbcn.calendarioipsc;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class NumDiasPosicion{

    int numDias, posicionEvento;

    public NumDiasPosicion(int numDias, int posicionEvento) {
        this.numDias = numDias;
        this.posicionEvento = posicionEvento;
    }

    public int getNumDias() {
        return numDias;
    }

    public int getPosicionEvento() {
        return posicionEvento;
    }

}
