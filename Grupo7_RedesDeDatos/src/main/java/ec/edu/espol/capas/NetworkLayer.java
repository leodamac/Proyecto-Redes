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
    
    public String getMAC(){
        return dataLinkLayer.getMAC();
    }
    
    public void setIP(String IP){
        this.IP = IP;
    }
    
    public String getIP(){
        return this.IP;
    }

    public List<String> getIpTable() {
        return ipTable;
    }
    
    public boolean isClosed(){
        return this.getTransportLayer().getAplicationLayer().isClosed();
    }
    
    public String generateIP(){
        String[] ip = this.IP.split("\\.");
        String newIP = ip[0] + "." + ip[1] + "." + ip[2] + "." + String.valueOf(this.ipTable.size() + 1);
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
        char[] header = generateHeader(data);
        char[] packet = new char[header.length + data.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(data, 0, packet, header.length, data.length);
        
        return packet;
    }

    @Override
    public char[] desencapsulation(char[] data) {
        // Calculamos la longitud del header:
        // 15 caracteres (source IP) + 15 caracteres (destination IP) + 1 (delimitador "|") + 1 (delimitador "|")
        int headerLength = 11 + 1 + 11; // 32 caracteres en total

        // Extraemos la parte de los datos excluyendo el header
        int dataLength = data.length - headerLength;
        char[] extractedData = new char[dataLength];

        System.arraycopy(data, headerLength, extractedData, 0, dataLength);

        return extractedData;
    }

    @Override
    public char[] generateHeader(char[] data) {
        String sourceIP = this.IP;
        String[] ip = this.IP.split("\\.");
        String destinationIP = ip[0] + "." + ip[1] + "." + ip[2] + ".1";
        int dataLength = data.length;

        // Convertimos la información del header en un String
        String headerString = sourceIP + "|" + destinationIP;

        // Convertimos el String del header a un array de caracteres
        char[] header = headerString.toCharArray();

        return header;
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
