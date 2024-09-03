package application;

import ec.edu.espol.capas.AplicationLayer;
import ec.edu.espol.capas.TransporLayer;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pool.Sender;

public class Application implements Runnable{
    private AplicationLayer applicationLayer;
    private TransporLayer transportLayer;
    private final int dataSize = 128;
    public static String path;
    private volatile int datosEnviados;
    private volatile int datosRecibidos;
    private volatile int dCorrupccion;
    private volatile int dPerdida;

    public PriorityQueue<String> mensajesAlFinalDeTodo = new PriorityQueue<>((o1, o2) -> {
        int id1 = -1;
        try{
            id1 = Integer.parseInt(o1.split("\\|")[0]);
        } catch (NumberFormatException ex){
        }

        int id2 = -1;
        try{
            id2 = Integer.parseInt(o2.split("\\|")[0]);
        } catch (NumberFormatException ex){
        }
        return id1 - id2;
    });
    
    private Thread recibidorThread = null;
    private Thread enviadorThread = null;
    public String dataF = "";
    private volatile boolean close = false;
    private volatile boolean sendData = false;
    private volatile boolean sinPerdida = true;
    private Semaphore mutex = new Semaphore(1);
    private Semaphore mutex2 = new Semaphore(1);
    
    public Application(boolean connectionOriented, boolean sinPerdida) {
        this.applicationLayer = new AplicationLayer(connectionOriented);
        this.transportLayer = new TransporLayer(connectionOriented);
        this.transportLayer.setSourcePort(8080);
        this.transportLayer.setDestinationPort(10);
        this.applicationLayer.conectTransportLayer(transportLayer);
        this.transportLayer.connectToAplicationLayer(applicationLayer);
        this.datosEnviados = 0;
        this.datosRecibidos = 0;
        this.sinPerdida = sinPerdida;
        this.dCorrupccion = 0;
        this.dPerdida = 0;
    }
    
    public Application(boolean connectionOriented) {
        this(connectionOriented, false);
    }
   
    public void close(){
        close = true;
        this.applicationLayer.close();
    }
    
    public boolean isClosed(){
        return this.close;
    }

    public int getDatosEnviados() {
        return datosEnviados;
    }

    public int getDatosRecibidos() {
        return datosRecibidos;
    }

    public int getdCorrupccion() {
        return dCorrupccion;
    }

    public int getdPerdida() {
        return dPerdida;
    }

    public AplicationLayer getApplicationLayer() {
        return applicationLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
    }

    public void sendFile(String path) throws IOException{
        sendData = true;
        SendFile sf = new SendFile(path);
        enviadorThread = new Thread(sf);
        enviadorThread.start();
    }
    
    public void receiveDataFile(TextArea textArea, Label datosRecibidos) {
        ReceiveFile rf = new ReceiveFile(textArea, datosRecibidos);
        recibidorThread = new Thread(rf);
        recibidorThread.start();
    }

    public Thread getRecibidorThread() {
        return recibidorThread;
    }

    public Thread getEnviadorThread() {
        return enviadorThread;
    }
    
    @Override
    public void run() {
        
        Sender senderAppToTrans = new Sender(applicationLayer,transportLayer, sinPerdida);
        Sender senderTransToApp = new Sender(transportLayer, applicationLayer, sinPerdida);

        Sender senderTransToNetw = new Sender(transportLayer, transportLayer.getNetworkLayer(), sinPerdida);
        Sender senderNetToTrans = new Sender(transportLayer.getNetworkLayer(),transportLayer, sinPerdida);

        Sender senderNetToDat = new Sender(transportLayer.getNetworkLayer(),transportLayer.getNetworkLayer().getDataLinkLayer(), sinPerdida);
        Sender senderDatToNet = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer(),transportLayer.getNetworkLayer(), sinPerdida);

        Sender senderDatToPhy = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer(),transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(), sinPerdida);
        Sender senderPhyToDat = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(),transportLayer.getNetworkLayer().getDataLinkLayer(), sinPerdida);
        /*
        Sender s1 = new Sender(applicationLayer,transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(), sinPerdida);
        Sender s2 = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(), applicationLayer);
        new Thread(s1).start();
        new Thread(s2).start();
        
        new Thread(()->{
            while(!isClosed()){
            }
            s1.finish();
            s2.finish();
        }).start();
        */
        new Thread(senderAppToTrans).start();
        new Thread(senderTransToApp).start();
        new Thread(senderTransToNetw).start();
        new Thread(senderNetToTrans).start();
        new Thread(senderNetToDat).start();
        new Thread(senderDatToNet).start();
        new Thread(senderDatToPhy).start();
        new Thread(senderPhyToDat).start();
        new Thread(()->{
            while(!isClosed()){
                
            }
            senderAppToTrans.finish();
            senderTransToApp.finish();
            senderTransToNetw.finish();
            senderNetToTrans.finish();
            senderNetToDat.finish();
            senderDatToNet.finish();
            senderDatToPhy.finish();
            senderPhyToDat.finish();
            
            this.dCorrupccion += senderAppToTrans.getDatosCorruptos();
            this.dCorrupccion += senderTransToApp.getDatosCorruptos();
            this.dCorrupccion += senderTransToNetw.getDatosCorruptos();
            this.dCorrupccion += senderNetToTrans.getDatosCorruptos();
            this.dCorrupccion += senderNetToDat.getDatosCorruptos();
            this.dCorrupccion += senderDatToNet.getDatosCorruptos();
            this.dCorrupccion += senderDatToPhy.getDatosCorruptos();
            this.dCorrupccion += senderPhyToDat.getDatosCorruptos();
            
            this.dPerdida += senderAppToTrans.getDatosPerdidos();
            this.dPerdida += senderTransToApp.getDatosPerdidos();
            this.dPerdida += senderTransToNetw.getDatosPerdidos();
            this.dPerdida += senderNetToTrans.getDatosPerdidos();
            this.dPerdida += senderNetToDat.getDatosPerdidos();
            this.dPerdida += senderDatToNet.getDatosPerdidos();
            this.dPerdida += senderDatToPhy.getDatosPerdidos();
            this.dPerdida += senderPhyToDat.getDatosPerdidos();
            
            System.out.println(dPerdida + " " + dCorrupccion);
        }).start();
    }
    
    
    class ReceiveFile implements Runnable {
        private TextArea textArea;
        private Label labelDatosRecibidos;
        public ReceiveFile(TextArea textArea, Label datosRecibidos) {
            this.textArea = textArea;
            this.labelDatosRecibidos = datosRecibidos;
        }

        @Override
        public void run() {
            //datosRecibidos = 0;
            boolean fin = false;
            int indiceFinal = -1;
            boolean salioF = false;
            int llegados = 0;
            textArea.clear();
            while (!fin) {
                try {
                    char[] seg = applicationLayer.getPoolInInferior().take();
                    llegados++;
                    if(seg != null){
                        String mensajeFull = new String(seg);
                        System.out.println("\n***" + mensajeFull + "***\n" + mensajesAlFinalDeTodo.size());
                        if (seg.length == 1 && seg[0] == 'f') {
                            salioF = true;
                        }else if(!mensajeFull.contains("|")){
                            //Si nunca contiene indice|texto|caracteres valido, como es en el caso de un dato perdido [0, 0, 0, 0, ... , 0] el try catch evitara que se haga alguna accion
                            try{
                                indiceFinal = Integer.parseInt(mensajeFull);
                            }catch(Exception e){
                                System.out.println(e);
                            }
                        } else {
                            mensajesAlFinalDeTodo.offer(mensajeFull);
                        }
                        System.out.println("\n***" + indiceFinal + "=" + mensajesAlFinalDeTodo.size() + "***\n" );
                        if(salioF && indiceFinal == mensajesAlFinalDeTodo.size()){
                            fin = true;
                        }
                    }else{
                        boolean tomo = false;
                        int tiempo = 1;
                        int tiempoFinal = 5;
                        
                        while(!tomo && tiempo < tiempoFinal && !close){
                            if(sendData){
                                tiempoFinal = 8;
                                sendData = false;
                            }
                            System.out.println("Esperando " + tiempo + " segundos");
                            seg = applicationLayer.getPoolInInferior().take(tiempo++);
                            if(seg == null){
                                if(llegados == indiceFinal){
                                    fin = true;
                                }
                            }else{
                                tomo = true;
                                String mensajeFull = new String(seg);
                                System.out.println("\n***" + mensajeFull + "***\n" + mensajesAlFinalDeTodo.size());
                                if (seg.length == 1 && seg[0] == 'f') {
                                    salioF = true;
                                }else if(!mensajeFull.contains("|")){
                                    try{
                                        indiceFinal = Integer.parseInt(mensajeFull);
                                    }catch (NumberFormatException e){
                                        System.out.println(e);
                                    }
                                    
                                } else {
                                    mensajesAlFinalDeTodo.offer(mensajeFull);
                                }
                                System.out.println("\n***" + indiceFinal + "=" + mensajesAlFinalDeTodo.size() + "***\n" );
                                if(salioF && indiceFinal == mensajesAlFinalDeTodo.size()){
                                    fin = true;
                                }
                            }
                        }
                        if(!tomo){
                            fin = true;
                        }
                    }
                    
                    if(close){
                        fin = true;
                    }
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                    fin = true;
                }
            }
            if(!close){
                while (!mensajesAlFinalDeTodo.isEmpty()) {
                    String mensaje = mensajesAlFinalDeTodo.poll();
                    String contenido = mensaje.split("\\|")[1];
                    System.out.println(contenido);
                    try {
                        mutex2.acquire();
                        datosRecibidos += contenido.length();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                    }finally {
                        mutex2.release();
                    }                    
                    Platform.runLater(() -> {
                        textArea.appendText(contenido);
                        labelDatosRecibidos.setText("Datoss recibidos: " + datosRecibidos + "\nPaquetes Recibidos: " + (datosRecibidos/dataSize));
                    });
                }
            }
            
            System.out.println("Paró de recibir datos.");
        }
    }
    
    
    class SendFile implements Runnable{
        private String path;
        private SendFile(String path){
            this.path = path;
        }
        
        @Override
        public void run() {
            try {
                sendFile();
            } catch (IOException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private FileReader openFile(String path){
        try{
            return new FileReader(path);
        } catch (IOException e) {
            return null;
        }
    }
    
    public boolean sendFile() throws IOException{
        ArrayList<char[]> segmentos = new ArrayList<>();
        boolean fin = false;
        FileReader fr = openFile(path);
        int i = 0;
        if(fr != null){
            while(!fin){
                try {
                    char[] data = this.dividirData(fr, i);
                    if(data == null){
                        fin = true;
                        fr.close();
                    }else{
                        System.out.println(i++);
                        segmentos.add(data);
                        
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }else{
            return false;
        }
        System.out.println("segmentosss");
        for(char[] s: segmentos){
            try {
                System.out.println("***" + Arrays.toString(s) + "***\n");
                if(!applicationLayer.isClosed()){
                    applicationLayer.getPoolInSuperior().add(s);
                }else{
                    return false;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!applicationLayer.isClosed()){
            String fString = "f";
            try {
                applicationLayer.getPoolInSuperior().add(fString.toCharArray());
                fString = "" + i;
                applicationLayer.getPoolInSuperior().add(fString.toCharArray());
            } catch (InterruptedException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return true;
    }
    
    private char[] dividirData(FileReader fr, int indice) throws IOException{
        char[] buffer = new char[dataSize];
        String mensaje = indice + "|";
        int charLeidos = 0;
        int x = 1;
        while(charLeidos<dataSize && x!=-1){
            x = fr.read();
            if(x != -1){
                buffer[charLeidos] = (char)x; 
                charLeidos++;
            }
        }
        if(charLeidos>0){
            String payLoad = new String(buffer);
            String m = mensaje + payLoad + "|"+String.valueOf(charLeidos);
            try {
                mutex.acquire();
                datosEnviados += payLoad.length();
            } catch (InterruptedException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }finally {
                mutex.release();
            }
            return m.toCharArray();
        }
        return null;
    }
        
    }
}
