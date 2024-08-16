package dispositivos;

import application.Application;
import application.Mail;
import application.Radio;
import ec.edu.espol.capas.*;

public class Host{
    Cable cable;
    
    private final DataLinkLayer dataLinkLayer; //Simula tarjeta de red
    private final NetworkLayer networkLayer;
    
    public Host(String MAC) {
        dataLinkLayer = new DataLinkLayer(MAC);
        networkLayer = new NetworkLayer();
    }
    
    public void connectToCable(Cable cable){
        this.cable = cable;
        this.networkLayer.setIP(cable.getPhysicalLayer().getDataLinkLayer().generateIP());// Tarjeta de red del otro dispositivo conectado y le asigna IP
        this.dataLinkLayer.conectToNetworkLayer(networkLayer);
        this.dataLinkLayer.conectToPhysicalLayer(this.cable.getPhysicalLayer());
        this.networkLayer.conectToDataLinkLayer(dataLinkLayer);
    }
    
    public Application openApplication(String name){
        Application app = null;
        switch(name){
                case "mail":
                    app = new Mail();
                    break;
                case "radio":
                    app = new Radio();
                    break;
                default:
                    break;
        }
        app.getTransportLayer().connectToNetworkLayer(networkLayer);
        return app;
    }    
}
