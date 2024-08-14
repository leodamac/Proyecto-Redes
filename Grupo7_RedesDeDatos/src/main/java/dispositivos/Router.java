/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dispositivos;

import ec.edu.espol.capas.DataLinkLayer;
import ec.edu.espol.capas.NetworkLayer;
import java.util.LinkedList;
import java.util.List;

public class Router implements Runnable{
    NetworkLayer networkLayer;
    DataLinkLayer dataLinkLayer; //Simula tarjeta de red
    List<Cable> cables;
    

    public Router(String MAC) {  
        this.dataLinkLayer = new DataLinkLayer(MAC);
        this.networkLayer = new NetworkLayer();
        this.networkLayer.setIP("192.168.1.1");
        this.dataLinkLayer.conectToNetworkLayer(networkLayer);
        this.networkLayer.conectToDataLinkLayer(dataLinkLayer);
        this.cables = new LinkedList();
    }
    
    public void conectCable(Cable cable){
        this.cables.add(cable);
        cable.getPhysicalLayer().conectToDataLinkLayer(this.dataLinkLayer);
        this.dataLinkLayer.conectToPhysicalLayer(cable.getPhysicalLayer());
    }
    
    public void disconnectCable(Cable cable){
        this.cables.remove(cable);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
