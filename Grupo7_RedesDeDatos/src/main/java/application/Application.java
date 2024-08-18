package application;

import ec.edu.espol.capas.AplicationLayer;
import ec.edu.espol.capas.TransporLayer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.DataPool;
import pool.Sender;

public class Application implements Runnable{
    private AplicationLayer applicationLayer;
    private TransporLayer transportLayer;
    private final int dataSize = 128;
    public static String path;
//    private int contador;
    public DataPool<String> pool;
    public PriorityQueue<String> mensajesAlFinalDeTodo = new PriorityQueue<>((o1, o2) -> {
        int id1 = Integer.parseInt(o1.split("\\|")[0]);
        int id2 = Integer.parseInt(o2.split("\\|")[0]);
        return id1 - id2;
    });
    
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
//                    this.contador++;
                    char[] data = this.dividirData(fr, i);
                    //char[] data = {'h','o','l','a'};
                    if(data == null){
                        fin = true;
                        fr.close();
                    }else{
                        segmentos.add(data);
                        System.out.println(++i);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            exito = true;
        }
//        String mensaje = new String(segmentos.get(segmentos.size()-1));
//        String[] mensajitos = mensaje.split("|");
//        int tamañoNum = mensajitos[0].length(); 
//        segmentos.get(segmentos.size()-1)[tamañoNum+2] = '2';
        
        this.applicationLayer.getPoolInSuperior().addAll(segmentos);
        return exito;
    }
    
    private char[] dividirData(FileReader fr, int indice) throws IOException{
        char[] buffer = new char[dataSize];
        String mensaje = indice + "|";
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
            do{
//                while(contador!=0){
                    try {
                        String mensaje = new String(this.applicationLayer.getPoolInInferior().take());
//                        System.out.println(mensaje);
//                        String[] mensajitos = mensaje.split("|");
//                        int posicion = Integer.parseInt(mensajitos[0]); 
                        this.mensajesAlFinalDeTodo.offer(mensaje);
//                        this.contador--;
                    } catch (InterruptedException ex) {
                        
                        Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                }
            } while(Thread.currentThread().isInterrupted());
//            for(String mens : mensajesAlFinalDeTodo){
//                System.out.println(mens.split("\\|")[1]);
//            }
            
        }).start();
//        new Thread(() -> {
//            // Crear el FileWriter para escribir en el archivo
//            FileWriter writer = null;
//            try {
//                writer = new FileWriter("mensaje.txt", true); // true para agregar al final del archivo
//                while (!mensajesAlFinalDeTodo.isEmpty()) {
//                    this.applicationLayer.getPoolInInferior();
//                    // Toma un mensaje del pool y lo convierte a String
//                    
//                    // Escribir el contenido del mensaje en el archivo
//                    writer.write(mensaje);
//                }
//            } catch (IOException e) {
//                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, "Ocurrió un error al escribir en el archivo", e);
//            }
//        }).start();
    }
}
