package application;

import ec.edu.espol.capas.AplicationLayer;
import ec.edu.espol.capas.TransporLayer;
import java.util.Scanner;
import pool.DataPool;

public class Application implements Runnable{
    private AplicationLayer applicationLayer;
    private TransporLayer transportLayer;
    public static String path;
    
    DataPool<String> dataPool;

    public Application(boolean connectionOriented) {
        this.applicationLayer = new AplicationLayer(connectionOriented);
        this.transportLayer = new TransporLayer(connectionOriented);
        this.transportLayer.setSourcePort(8080);
        this.applicationLayer.conectTransportLayer(transportLayer);
        this.transportLayer.connectToAplicationLayer(applicationLayer);
        this.dataPool = new DataPool<>(20);
    }

    public AplicationLayer getApplicationLayer() {
        return applicationLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
    }
    
    public boolean sendFile(String path){
        return this.applicationLayer.sendFile(path);
    }
    
    public char[] recieveFile(){
        return this.transportLayer.receiveDataFromNetworkLayer();
    }

    @Override
    public void run() {
        Sender sender = new Sender(dataPool);
        Reciever reciever = new Reciever(dataPool);
        new Thread(sender).start();
        new Thread(reciever).start();
    }
    
    class Sender implements Runnable {
        private final DataPool pool;

        Sender(DataPool q){
            this.pool = q;
        }
        @Override
        public void run() {
          try {
            while (true) {
                String comando = produce();
                if(comando.equals("-1")){
                    break;
                }
                pool.add(comando);
            }
          } catch (InterruptedException ex) {
          }
        }
        public String produce() {
                Scanner sc = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Ingrese el nombre del archivo: ");
                return sc.nextLine();
        }
    }
    class Reciever implements Runnable {
        private final DataPool<String> pool;

        Reciever(DataPool q){
            this.pool = q;
        }
        @Override
        public void run() {
          try {
            while (true) {
                String data = pool.take();
                if(data.equals("-1")){
                    break;
                }
                this.process(data);
            }
          } catch (InterruptedException ex) {
          }
        }
        public void process(String data) {
                System.out.println("Data recibida: " + data);
                Application.path = data;
                sendFile(Application.path);
        }
    }
}
