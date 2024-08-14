package ec.edu.espol.capas;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.DataPool;

public class NetworkLayer extends Layer{
    private DataLinkLayer dataLinkLayer;
    private TransporLayer transportLayer;
    private DataPool<char[]> networkLayerPool;
    private String IP;
    private List<String> ipTable;
    
    public NetworkLayer() {
        this.networkLayerPool = new DataPool<>(20);
        this.ipTable = new LinkedList<>();
    }
    
    public void setIP(String IP){
        this.IP = IP;
    }
    
    public String getIP(){
        return this.IP;
    }
    
    public String generateIP(){
        String[] ip = this.IP.split("\\.");
        String newIP = ip[0] + "." + ip[1] + "." + ip[2] + "." + String.valueOf(this.ipTable.size() + 2);
        this.ipTable.add(newIP);
        return newIP;
    }
    
    public boolean conectToDataLinkLayer(DataLinkLayer dataLinkLayer){
        if(dataLinkLayer == null){
            return false;
        }
        this.dataLinkLayer = dataLinkLayer;
        return true;
    }
    
    public boolean conectTransportLayer(TransporLayer transportLayer){
        if(transportLayer == null){
            return false;
        }
        this.transportLayer = transportLayer;
        return true;
    }
    
    public void sendData(char[] data, DataPool<char[]> pool) throws InterruptedException {
        pool.add(data);
    }

    public char[] receiveData(DataPool<char[]> pool) throws InterruptedException {
        char[] receivedMessage = pool.take();
        return receivedMessage;
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

    public DataPool<char[]> getNetworkLayerPool() {
        return networkLayerPool;
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
