package ec.edu.espol.capas;

import java.util.logging.Level;
import java.util.logging.Logger;
import pool.DataPool;

public class DataLinkLayer extends Layer{
    
    private DataPool<char[]> dataLinklLayerPool;
    private PhysicalLayer physicalLayer;
    private NetworkLayer networkLayer;
    private String MAC;
    private String IP;

    public DataLinkLayer(String MAC) {
        this.MAC = MAC;
        this.dataLinklLayerPool = new DataPool<>(20);
    }
    
    public void sendData(char[] data, DataPool<char[]> pool) throws InterruptedException {
        pool.add(data);
    }

    public char[] receiveData(DataPool<char[]> pool) throws InterruptedException {
        char[] receivedMessage = pool.take();
        return receivedMessage;
    }
    
    public boolean conectToPhysicalLayer(PhysicalLayer physicalLayer){
        if(physicalLayer == null){
            return false;
        }
        this.physicalLayer = physicalLayer;
        return true;
    }
    
    public boolean conectToNetworkLayer(NetworkLayer networkLayer){
        if(networkLayer == null){
            return false;
        }
        this.networkLayer = networkLayer;
        this.IP = this.networkLayer.getIP();
        return true;
    }
    
    public String generateIP(){
        return this.networkLayer.generateIP();
    }

    public DataPool<char[]> getDataLinklLayerPool() {
        return dataLinklLayerPool;
    }
    
    public void sendDataToPhysicalLayer(char[] data){
        try {
            this.physicalLayer.getPhysicalLayerPool().add(data);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataLinkLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendDataToNetworkLayer(char[] data){
        try {
            this.networkLayer.getNetworkLayerPool().add(data);
        } catch (InterruptedException ex) {
            Logger.getLogger(DataLinkLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public char[] receiveDataFromNetworkLayer(){
        try {
            return this.networkLayer.getNetworkLayerPool().take();
        } catch (InterruptedException ex) {
            Logger.getLogger(PhysicalLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public char[] receiveDataFromPhysicalLayer(){
        try {
            return this.physicalLayer.getPhysicalLayerPool().take();
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
