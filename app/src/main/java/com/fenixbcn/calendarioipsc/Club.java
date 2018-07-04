package com.fenixbcn.calendarioipsc;

public class Club {

    String nombreclub;
    Boolean clubSelected;

    public Club (String tNombreclub, Boolean tClubSelected) {

        this.nombreclub = tNombreclub;
        this.clubSelected = tClubSelected;
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


