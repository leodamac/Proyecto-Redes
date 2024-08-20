package dispositivos;

import java.util.HashMap;

public class Host extends Dispositivo{
    Cable cable;
    
    public Host(String MAC) {
        super(MAC);
        procesos = new HashMap();
    }
    
    public void connectToCable(Cable cable){
        this.cable = cable;
        this.networkLayer.setIP(cable.getPhysicalLayer().getDataLinkLayer().generateIP());// Tarjeta de red del otro dispositivo conectado y le asigna IP
        this.dataLinkLayer.conectToNetworkLayer(networkLayer);
        this.dataLinkLayer.conectToPhysicalLayer(this.cable.getPhysicalLayer());
        this.networkLayer.conectToDataLinkLayer(dataLinkLayer);
        cable.setMAC(MAC);
    }
    

    

}
