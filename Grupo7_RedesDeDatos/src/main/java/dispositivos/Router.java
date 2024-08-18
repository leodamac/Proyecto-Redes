/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dispositivos;

import java.util.LinkedList;
import java.util.List;
import pool.Sender;

public class Router extends Dispositivo implements Runnable{
    List<Cable> cables;
    
    public Router(String MAC) {  
        super(MAC);
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
        Sender senderPhy1ToPhy2 = new Sender(this.cables.get(0).getPhysicalLayer(),this.cables.get(1).getPhysicalLayer());
        Sender senderPhy2ToPhy1 = new Sender(this.cables.get(1).getPhysicalLayer(),this.cables.get(0).getPhysicalLayer());
        
        new Thread(senderPhy1ToPhy2).start();
        new Thread(senderPhy2ToPhy1).start();
    }
    
    
    
}
