package application;

import ec.edu.espol.capas.AplicationLayer;
import ec.edu.espol.capas.TransporLayer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.DataPool;
import pool.Sender;

public class Application implements Runnable{
    private AplicationLayer applicationLayer;
    private TransporLayer transportLayer;
    private final int dataSize = 128;
    public static String path;
    private DataPool<char[]> pool;
    
    public Application(boolean connectionOriented) {
        this.applicationLayer = new AplicationLayer(connectionOriented);
        this.transportLayer = new TransporLayer(connectionOriented);
        this.transportLayer.setSourcePort(8080);
        this.transportLayer.setDestinationPort(10);
        this.applicationLayer.conectTransportLayer(transportLayer);
        this.transportLayer.connectToAplicationLayer(applicationLayer);
        this.pool = new DataPool<>(20);
    }

    public AplicationLayer getApplicationLayer() {
        return applicationLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
    }
    
        private FileReader openFile(String path){
        try{
            return new FileReader(path);
        } catch (IOException e) {
            return null;
        }
    }
    
    public boolean sendFile(String path) throws IOException{
        ArrayList<char[]> segmentos = new ArrayList<>();
        boolean fin = false;
        FileReader fr = openFile(path);
        boolean exito = false;
        if(fr != null){
            int i = 0;
            while(!fin){
                try {
                    char[] data;
                    if(i == 0){
                        data = this.dividirData(fr, i, 1);
                    } else{
                        data = this.dividirData(fr, i, 0);
                    }
                    //char[] data = {'h','o','l','a'};
                    if(data == null){
                        fin = true;
                        fr.close();
                    }else{
                        segmentos.add(data);
//                        this.applicationLayer.getPoolInSuperior().add(data);
                        System.out.println(++i);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            exito = true;
        }
//        segmentos.get(segmentos.size()-1);
        this.applicationLayer.getPoolInSuperior().addAll(segmentos);
        return exito;
    }
    
    private char[] dividirData(FileReader fr, int indice, int flag) throws IOException{
        char[] buffer = new char[dataSize];
        String mensaje = indice + "|" + flag + "|";
        //char[] buffer = {'h','o','l','a'};
        int charLeidos = 0;
        int x = 1;
        while(charLeidos<this.dataSize && x!=-1){
            x = fr.read();
            if(x != -1){
                buffer[charLeidos] = (char)x; 
                charLeidos++;
            }
        }
        //charLeidos = fr.read(buffer, n, dataSize);   
        if(charLeidos>0){
            char[] mensajeRetorno = new char[buffer.length + mensaje.length()];
            System.arraycopy(mensaje.toCharArray(), 0, mensajeRetorno, 0, mensaje.toCharArray().length);
            System.arraycopy(buffer, 0, mensajeRetorno, mensaje.toCharArray().length, buffer.length);
            return mensajeRetorno;
        }
        return null;
    }
    
    public char[] recieveFile(){
        return null;
    }

    @Override
    public void run() {
        
        Sender senderAppToTrans = new Sender(this.applicationLayer,this.transportLayer);
        Sender senderTransToApp = new Sender(this.transportLayer, this.applicationLayer);
        
        Sender senderTransToNetw = new Sender(this.transportLayer, this.transportLayer.getNetworkLayer());
        Sender senderNetToTrans = new Sender(this.transportLayer.getNetworkLayer(),this.transportLayer);
        
        Sender senderNetToDat = new Sender(this.transportLayer.getNetworkLayer(),this.transportLayer.getNetworkLayer().getDataLinkLayer());
        Sender senderDatToNet = new Sender(this.transportLayer.getNetworkLayer().getDataLinkLayer(),this.transportLayer.getNetworkLayer());
        
        Sender senderDatToPhy = new Sender(this.transportLayer.getNetworkLayer().getDataLinkLayer(),this.transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer());
        Sender senderPhyToDat = new Sender(this.transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(),this.transportLayer.getNetworkLayer().getDataLinkLayer());
        
        new Thread(senderAppToTrans).start();
        new Thread(senderTransToApp).start();
        new Thread(senderTransToNetw).start();
        new Thread(senderNetToTrans).start();
        new Thread(senderNetToDat).start();
        new Thread(senderDatToNet).start();
        new Thread(senderDatToPhy).start();
        new Thread(senderPhyToDat).start();
        new Thread(()->{
            while(true){
                try {
                    this.pool.add(this.applicationLayer.getPoolInInferior().take());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            }).start();
        new Thread(() -> {
            // Crear el FileWriter para escribir en el archivo
            FileWriter writer = null;
            try {
                writer = new FileWriter("mensaje.txt", true); // true para agregar al final del archivo
                while (true) {
                    try {
                        // Toma un mensaje del pool y lo convierte a String
                        String mensaje = new String(this.pool.take());
                        System.out.println(mensaje);
                        // Escribir el contenido del mensaje en el archivo
                        writer.write(mensaje);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException e) {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, "Ocurrió un error al escribir en el archivo", e);
            }
        }).start();
    }
}
