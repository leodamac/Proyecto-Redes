/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dispositivos;

import application.Application;
import application.Mail;
import application.Radio;
import ec.edu.espol.capas.DataLinkLayer;
import ec.edu.espol.capas.NetworkLayer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Leo
 */
public abstract class  Dispositivo {
    DataLinkLayer dataLinkLayer; //Simula tarjeta de red
    NetworkLayer networkLayer;
    private String MAC;
    private List<Application> aplicaciones;
    Map<String, Thread> procesos;

    public Dispositivo(String MAC) {
        this.MAC = MAC;
        dataLinkLayer = new DataLinkLayer(MAC);
        networkLayer = new NetworkLayer();
        aplicaciones = new LinkedList();
        this.procesos = new HashMap<>();
    }
    
    public Thread getProceso(String name){
        return this.procesos.get(name);
    }
    
    public String getIp(){
        return networkLayer.getIP();
    }

    public String getMAC() {
        return MAC;
    }
    
    public String getTipo(){
        return this instanceof Host ? "pc" : "router";
    }
    
    public void addApp(Application app){
        this.aplicaciones.add(app);
    }
    
    public Application getApp(int indice){
        return this.aplicaciones.get(indice);
    }
    public Application openApplication(String name){
        Application app = null;
        Thread t;
        switch(name){
                case "mail":
                    app = new Mail();
                    t = new Thread(app);
                    procesos.put(name, t);
                    t.start();
                    addApp(app);
                    break;
                case "radio":
                    app = new Radio();
                    t = new Thread(app);
                    procesos.put(name, t);
                    t.start();
                    addApp(app);
                    break;
                default:
                    break;
        }
        app.getTransportLayer().connectToNetworkLayer(networkLayer);
        return app;
    }    
}
