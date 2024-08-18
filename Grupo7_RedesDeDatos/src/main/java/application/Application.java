package application;

import ec.edu.espol.capas.AplicationLayer;
import ec.edu.espol.capas.TransporLayer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import pool.DataPool;
import pool.Sender;

public class Application implements Runnable{
    private AplicationLayer applicationLayer;
    private TransporLayer transportLayer;
    private final int dataSize = 128;
    public static String path;
    private int datosEnviados;
    private int datosRecibidos;
//    private int contador;
    public DataPool<String> pool;
    public PriorityQueue<String> mensajesAlFinalDeTodo = new PriorityQueue<>((o1, o2) -> {
        int id1 = Integer.parseInt(o1.split("\\|")[0]);
        int id2 = Integer.parseInt(o2.split("\\|")[0]);
        return id1 - id2;
    });
    private Thread t = null;
    public String dataF = "";
    
    public Application(boolean connectionOriented) {
        this.applicationLayer = new AplicationLayer(connectionOriented);
        this.transportLayer = new TransporLayer(connectionOriented);
        this.transportLayer.setSourcePort(8080);
        this.transportLayer.setDestinationPort(10);
        this.applicationLayer.conectTransportLayer(transportLayer);
        this.transportLayer.connectToAplicationLayer(applicationLayer);
        this.pool = new DataPool<>(20);
        this.datosEnviados = 0;
        this.datosRecibidos = 0;
    }

    public Thread getT() {
        return t;
    }

    public String getData(){
        try {
            return this.pool.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public int getDatosEnviados() {
        return datosEnviados;
    }

    public int getDatosRecibidos() {
        return datosRecibidos;
    }

    public AplicationLayer getApplicationLayer() {
        return applicationLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
    }

    public boolean sendFile(String path) throws IOException{
        SendFile sf = new SendFile(path);
        new Thread(sf).start();
        return true;
    }
    
    public Thread receiveDataFile(TextArea textArea) {
        ReceiveFile rf = new ReceiveFile(textArea);
        Thread t = new Thread(rf);
        t.start();
        return t;
    }

    @Override
    public void run() {
        Sender senderAppToTrans = new Sender(applicationLayer,transportLayer);
        Sender senderTransToApp = new Sender(transportLayer, applicationLayer);

        Sender senderTransToNetw = new Sender(transportLayer, transportLayer.getNetworkLayer());
        Sender senderNetToTrans = new Sender(transportLayer.getNetworkLayer(),transportLayer);

        Sender senderNetToDat = new Sender(transportLayer.getNetworkLayer(),transportLayer.getNetworkLayer().getDataLinkLayer());
        Sender senderDatToNet = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer(),transportLayer.getNetworkLayer());

        Sender senderDatToPhy = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer(),transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer());
        Sender senderPhyToDat = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(),transportLayer.getNetworkLayer().getDataLinkLayer());

        new Thread(senderAppToTrans).start();
        new Thread(senderTransToApp).start();
        new Thread(senderTransToNetw).start();
        new Thread(senderNetToTrans).start();
        new Thread(senderNetToDat).start();
        new Thread(senderDatToNet).start();
        new Thread(senderDatToPhy).start();
        new Thread(senderPhyToDat).start();
    }
    
    class ReceiveFile implements Runnable {
        private TextArea textArea;

        public ReceiveFile(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void run() {
            boolean fin = false;
            while (!fin) {
                try {
                    String mensajeFull = new String(applicationLayer.getPoolInInferior().take());
                    if (mensajeFull.equals("f")) {
                        fin = true;
                    } else {
                        mensajesAlFinalDeTodo.offer(mensajeFull);
                    }
                } catch (InterruptedException ex) {
                    fin = true;
                }
            }

            while (!mensajesAlFinalDeTodo.isEmpty()) {
                String mensaje = mensajesAlFinalDeTodo.poll();
                String contenido = mensaje.split("\\|")[1];
                Platform.runLater(() -> {
                    textArea.appendText(contenido);
                });
                datosRecibidos += contenido.length();
            }
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
        boolean exito = false;
        if(fr != null){
            int i = 0;
            datosEnviados = 0;
            while(!fin){
                try {
                    char[] data = this.dividirData(fr, i);
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

        for(char[] s: segmentos){
            try {
                applicationLayer.getPoolInSuperior().add(s);
            } catch (InterruptedException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        char[] f = new char[1];
        f[0] = 'f';
        try {
            applicationLayer.getPoolInSuperior().add(f);
        } catch (InterruptedException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return exito;
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
            datosEnviados += charLeidos;
            char[] cr = ("|"+String.valueOf(charLeidos)).toCharArray();
            char[] mensajeRetorno = new char[buffer.length + cr.length + mensaje.length()];
            System.arraycopy(mensaje.toCharArray(), 0, mensajeRetorno, 0, mensaje.toCharArray().length);
            System.arraycopy(buffer, 0, mensajeRetorno, mensaje.toCharArray().length, buffer.length);
            System.arraycopy(cr, 0, mensajeRetorno, mensaje.toCharArray().length + buffer.length, cr.length);
            return mensajeRetorno;
        }
        return null;
    }
        
    }
}
