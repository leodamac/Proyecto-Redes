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
    String MAC;
    private List<Application> aplicaciones;
    Map<String, Thread> procesos;
    volatile boolean on = false;

    public Dispositivo(String MAC) {
        this.MAC = MAC;
        dataLinkLayer = new DataLinkLayer(MAC);
        networkLayer = new NetworkLayer();
        aplicaciones = new LinkedList();
        this.procesos = new HashMap<>();
        this.on = true;
    }

    public NetworkLayer getNetworkLayer() {
        return networkLayer;
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
    
    public void shutDown(){
        for(Application app: this.aplicaciones){
            app.close();
        }
        this.on = false;
    }
    
    public Application openApplication(String name, boolean sinPerdida){
        Application app = null;
        Thread t = null;
        switch(name){
                case "mail":
                    app = new Mail(sinPerdida);
                    t = new Thread(app);
                    procesos.put(name, t);
                    t.start();
                    addApp(app);
                    break;
                default:
                    app = new Radio();
                    t = new Thread(app);
                    procesos.put(name, t);
                    t.start();
                    addApp(app);
                    break;
        }
        app.getTransportLayer().connectToNetworkLayer(networkLayer);
        return app;
    }    
}
