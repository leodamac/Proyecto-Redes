package com.mycompany.decision;

import dispositivos.Dispositivo;

public class Escenario {
    private Dispositivo dispositivo;
            
    public Escenario(Dispositivo dispositivo){
        this.dispositivo= dispositivo;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    
    public String getTipo(){
        return dispositivo.getTipo();
    }
}
