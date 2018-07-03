package com.fenixbcn.calendarioipsc;

public class Club {

    String nombreclub;
    Boolean clubSelected = true;

    public Club (String tNombreclub) {

        this.nombreclub = tNombreclub;
    }

    public String getNombreclub() {
        return nombreclub;
    }

    public Boolean isClubSelected() {
        return clubSelected;
    }

    public void setSelected (Boolean clubSelected) {
        this.clubSelected = clubSelected;
    }
}


