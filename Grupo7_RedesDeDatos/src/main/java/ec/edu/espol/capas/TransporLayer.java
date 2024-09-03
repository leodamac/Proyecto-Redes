package ec.edu.espol.capas;

import extras.Utilidades;
import java.util.Arrays;
import java.util.PriorityQueue;

public class TransporLayer extends Layer{
    private final boolean connectionOriented;
    private final int sizeBitsPort = 16;
    private final static int nBitsSegments = 32;
    private int sourcePort; //sourcePort
    private int destinationPort; //destinationPort
    private int sequenceNumber; //Used to identify the lost segments and maintain the sequencing in transmission.
    private int acknowledgmentNumber; //Used to send a verification of received segments and to ask for the next segments
    private PriorityQueue<char[]> segmentPoolIn;
    private PriorityQueue<char[]> segmentPoolOut;
    private NetworkLayer networkLayer;
    private AplicationLayer aplicationLayer;
    
    
    public TransporLayer(boolean connectionOriented){
        super((short)4, "segmento");
        this.connectionOriented = connectionOriented;
        if(this.connectionOriented){
           segmentPoolIn = new PriorityQueue();
           segmentPoolOut = new PriorityQueue();
        }
    }
    
    public boolean isClosed(){
        return this.getAplicationLayer().isClosed();
    }
    
    public NetworkLayer getNetworkLayer(){
        return this.networkLayer;
    }
    
    public AplicationLayer getAplicationLayer(){
        return this.aplicationLayer;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }
    
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }
    
    public boolean connectToNetworkLayer(NetworkLayer networkLayer){
        if(networkLayer == null){
            return false;
        }
        this.networkLayer = networkLayer;
        return true;
    }
    
    public boolean connectToAplicationLayer(AplicationLayer aplicationLayer){
        if(aplicationLayer == null){
            return false;
        }
        this.aplicationLayer = aplicationLayer;
        return true;
    }

    @Override
    public char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData) {
        char[] header;
        if(urgentFlag){
            header = this.generateHeader(data, urgentData);
        }else{
            header = this.generateHeader(data);
        }
        char[] cap = new char[data.length + header.length];
        System.arraycopy(header, 0, cap, 0, header.length);
        System.arraycopy(data, 0, cap, header.length, data.length);
        
        return cap;
    }
    
    @Override
    public char[] encapsulation(char[] data) {
        return encapsulation(data, false, null);
    }

    @Override
    public char[] desencapsulation(char[] encapsulatedData) {
        String d = new String(encapsulatedData);
        String[] data = d.split("\\|");
        String r = "";
        String header = "";
        String e = "";
        for(int i = 0; i<data.length; i++){
            if(i<6){
                header += data[i] + "|";
            }else if(i==6){
                e = data[i];
            }else{
                r += data[i] + "|";
            }
        }
        r = r.substring(0, r.length()-1);
        int checksum = this.doChecksum((header+r).toCharArray());
        int checksumRecibido;
        try {
            checksumRecibido = Integer.parseInt(e);
        } catch (NumberFormatException ex) {
            System.out.println(ex);
            checksumRecibido = 0;
        }
        
        if(checksumRecibido != checksum){
            System.out.println("Fallo el CHECKSUM en TRANSPORTLAYER\n" + "Checksum Recibido: " + checksumRecibido + " - Checksum Calculado: " + checksum);
        }else{
            System.out.println("Paso el CHECKSUM en TRANSPORTLAYER\n" + "Checksum Recibido: " + checksumRecibido + " - Checksum Calculado: " + checksum);
        }
        return r.toCharArray();
    }

    @Override
    public char[] generateHeader(char [] data, char[] urgentData) {
        String header = this.sourcePort + "|" + this.destinationPort;
        if(this.connectionOriented){
            header += "|" + this.sequenceNumber + "|";
            if(urgentData != null){
                header += urgentData;
            }
            header += "|" + sequenceNumber + "|" + acknowledgmentNumber;
        }
        
        int checksum = doChecksum((header + "|" +new String(data)).toCharArray());  
        header += "|" + checksum + "|";
        return header.toCharArray();
    }
    
    @Override
    public char[] generateHeader(char[] data) {
        return generateHeader(data, null);
    }

    @Override
    public char[] generateTrailer(char[] data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
