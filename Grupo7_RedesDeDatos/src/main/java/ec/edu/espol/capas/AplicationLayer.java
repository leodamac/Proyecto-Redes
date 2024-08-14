package ec.edu.espol.capas;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.DataPool;

public class AplicationLayer extends Layer {
    private final boolean connectionOriented;
    private final int dataSize = 1024;
    private TransporLayer transportLayer;
    private DataPool<char[]> aplicationLayerPool;

    public AplicationLayer(boolean connectionOriented) {
        this.dataGram = "data";
        this.level = 4;
        this.connectionOriented = connectionOriented;
        this.aplicationLayerPool = new DataPool<>(20);
    }
    
    public void conectTransportLayer(TransporLayer transportLayer){
        this.transportLayer = transportLayer;
    }
    
    private FileReader openFile(String path){
        try (FileReader fr = new FileReader(path)) {
            return fr;
        } catch (IOException e) {
            return null;
        }
    }
    
    public boolean sendFile(String path){
        boolean fin = false;
        FileReader fr = openFile(path);
        int n = 0;
        boolean exito = false;
        if(fr != null){
            while(!fin){
                try {
                    char[] data = this.dividirData(fr, n);
                    if(data == null){
                        fin = true;
                    }else{
                        this.sendDataToTransportLayer(this.encapsulation(data));
                        n += this.dataSize;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            exito = true;
        }
        return exito;
    }
    
    private char[] dividirData(FileReader fr, int n) throws IOException{
        char[] buffer = new char[dataSize];
        int charLeidos;
        charLeidos = fr.read(buffer, n, dataSize);   
        if(charLeidos>0){
            return buffer;
        }
        return null;
    }
    
    public void sendDataToTransportLayer(char[] data){
        try {
            this.transportLayer.getTransportLayerPool().add(data);
        } catch (InterruptedException ex) {
            Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public char[] getDataFromTransportLayer(){
        try {
            return this.transportLayer.getTransportLayerPool().take();
        } catch (InterruptedException ex) {
            Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void sendData(char[] data, DataPool<char[]> pool) throws InterruptedException {
        pool.add(data);
    }

    public char[] receiveData(DataPool<char[]> pool) throws InterruptedException {
        char[] receivedMessage = pool.take();
        return receivedMessage;
    }

    public DataPool<char[]> getAplicationLayerPool() {
        return aplicationLayerPool;
    }
    
    @Override
    public char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] encapsulation(char[] data) {
        if(this.connectionOriented){
            return data;
        }
        return data;
    }

    @Override
    public char[] desencapsulation(char[] data) {
        return data;
    }

    @Override
    public char[] generateHeader(char[] data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] generateHeader(char[] data, char[] urgentData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] generateTrailer(char[] data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}