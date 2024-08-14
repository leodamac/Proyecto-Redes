package ec.edu.espol.capas;

import java.util.logging.Level;
import java.util.logging.Logger;
import pool.DataPool;

public class PhysicalLayer extends Layer{
    
    private DataLinkLayer dataLinkLayer;
    private DataPool<char[]> physicalLayerPool;

    public PhysicalLayer() {
        this.physicalLayerPool = new DataPool<>(20);
    }
    
    public boolean conectToDataLinkLayer(DataLinkLayer dataLinkLayer){
        if(dataLinkLayer == null){
            return false;
        }
        this.dataLinkLayer = dataLinkLayer;
        return true;
    }
    
    public DataLinkLayer getDataLinkLayer(){
        return this.dataLinkLayer;
    }
    
    public void sendData(char[] data, DataPool<char[]> pool) throws InterruptedException {
        pool.add(data);
    }

    public char[] receiveData(DataPool<char[]> pool) throws InterruptedException {
        char[] receivedMessage = pool.take();
        return receivedMessage;
    }

    public DataPool<char[]> getPhysicalLayerPool() {
        return physicalLayerPool;
    }
    
    public void sendDataToDataLinkLayer(char[] data){
        try {
            this.dataLinkLayer.getDataLinklLayerPool().add(data);
        } catch (InterruptedException ex) {
            Logger.getLogger(PhysicalLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public char[] receiveDataFromDataLinkLayer(){
        try {
            return this.dataLinkLayer.getDataLinklLayerPool().take();
        } catch (InterruptedException ex) {
            Logger.getLogger(PhysicalLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] encapsulation(char[] data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] desencapsulation(char[] data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
