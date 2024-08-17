package ec.edu.espol.capas;

import java.util.LinkedList;
import java.util.List;
import pool.DataPool;

public class NetworkLayer extends Layer{
    private DataLinkLayer dataLinkLayer;
    private TransporLayer transportLayer;
    private String IP;
    private List<String> ipTable;
    
    public NetworkLayer() {
        super((short)3, "paquete");
        this.ipTable = new LinkedList<>();
    }

    public DataLinkLayer getDataLinkLayer() {
        return dataLinkLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
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

    @Override
    public char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] encapsulation(char[] data) {
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
