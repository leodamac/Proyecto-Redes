package ec.edu.espol.capas;

import java.util.Arrays;
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
        char[] trailer = generateTrailer(packet);
        char[] encapsulado = new char[packet.length+trailer.length];
        System.arraycopy(packet, 0, encapsulado, 0, packet.length);
        System.arraycopy(trailer, 0, encapsulado, packet.length, trailer.length);
        return encapsulado;
    }

    @Override
    public char[] desencapsulation(char[] data) {
        // Calculamos la longitud del header:
        // 11 caracteres (source IP) + 11 caracteres (destination IP) + 1 (delimitador "|") + 1 (delimitador "|")
        int headerLength = 11 + 1 + 11; // 32 caracteres en total
        char[] trailer = getTrailer(data);
        // Extraemos la parte de los datos excluyendo el header
        int dataLength = (data.length - headerLength) - trailer.length-1;
        char[] extractedData = new char[dataLength];
        System.arraycopy(data, headerLength, extractedData, 0, dataLength);
        
        char[] checksumData = new char[data.length-trailer.length-1];
        System.arraycopy(data, 0, checksumData, 0, data.length-trailer.length-1);
        int checksumRecibido;
        int checksum = this.doChecksum(checksumData);
        try {
            checksumRecibido = Integer.parseInt(new String(trailer));
        } catch (NumberFormatException e) {
            checksumRecibido = 0;
        }
        
        if(checksumRecibido != checksum){
            System.out.println("Fallo el CHECKSUM en NETWORKLAYER\n" + "Checksum Recibido: " + checksumRecibido + " - Checksum Calculado: " + checksum);
        }else{
            System.out.println("Paso el CHECKSUM en NETWORKLAYER\n" + "Checksum Recibido: " + checksumRecibido + " - Checksum Calculado: " + checksum);
        }

        return extractedData;
    }

    @Override
    public char[] generateHeader(char[] data) {
        String sourceIP = this.IP;
        String[] ip = this.IP.split("\\.");
        String destinationIP = ip[0] + "." + ip[1] + "." + ip[2] + ".1";
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
        int valor = doChecksum(data);
        char[] trailer = ("|"+ String.valueOf(valor)).toCharArray();
        return trailer;
    }
    
    private char[] getTrailer(char[] data){
        System.out.println("GET TRAILER" + Arrays.toString(data));
        String numero = "";
        for(int i = data.length-1; i>0 && data[i] != '|'; i--){
            numero = data[i] + numero;
        }
        char[] trailer =  numero.toCharArray();
        if(trailer.length == data.length-1){
             trailer = "0".toCharArray();
        }
        return trailer;
    }
    
}
