package ec.edu.espol.grupo7_redesdedatos;

import application.Application;
import dispositivos.Cable;
import dispositivos.Host;
import dispositivos.Router;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws IOException {
        
        Host PC1 = new Host("ABCDEF");
        Host PC2 = new Host("12AF53");
        Router router = new Router("A6548E");
        Cable cable1 = new Cable();
        Cable cable2 = new Cable();
        
        router.conectCable(cable1);
        router.conectCable(cable2);
        
        PC1.connectToCable(cable1);
        PC2.connectToCable(cable2);
        
        
        Application appPC1 = PC1.openApplication("mail");
        Application appPC2 = PC2.openApplication("mail");
        
        appPC1.sendFile("input.txt");
        
        new Thread(appPC1).start();
        new Thread(router).start();
        new Thread(appPC2).start();
    }
    
}
