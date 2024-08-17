package ec.edu.espol.capas;

import extras.Utilidades;
import java.util.Arrays;
import java.util.PriorityQueue;

public class TransporLayer extends Layer{
    private final boolean connectionOriented;
    private final int sizeBitsPort = 16;
    private final static int nBitsSegments = 32;
    private char[] sourcePort = new char[sizeBitsPort]; //sourcePort
    private char[] destinationPort = new char[sizeBitsPort]; //destinationPort
    private char[] sequenceNumber = Utilidades.toArrayBinarie(0 ,nBitsSegments); //Used to identify the lost segments and maintain the sequencing in transmission.
    private char[] acknowledgmentNumber = Utilidades.toArrayBinarie(0 ,nBitsSegments); //Used to send a verification of received segments and to ask for the next segments
    private final int checksumSize = 16;
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
    
    public NetworkLayer getNetworkLayer(){
        return this.networkLayer;
    }
    
    public AplicationLayer getAplicationLayer(){
        return this.aplicationLayer;
    }

    public char[] getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(char[] sourcePort) {
        this.sourcePort = sourcePort;
    }
    
    public void setSourcePort(int sourcePort) {
        this.sourcePort = Utilidades.toArrayBinarie(sourcePort, sizeBitsPort);
    }

    public char[] getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(char[] destinationPort) {
        this.destinationPort = destinationPort;
    }
    
        public void setDestinationPort(int destinationPort) {
        this.destinationPort = Utilidades.toArrayBinarie(destinationPort, sizeBitsPort);
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
    public void sendDataInferior(char[] data) throws InterruptedException {
        poolOutInferior.add(this.encapsulation(data));
    }
    
    @Override
    public void sendDataSuperior(char[] data) throws InterruptedException {
        poolOutSuperior.add(this.desencapsulation(data));
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
        // Extraer el tamaño del headerLength en bits
        int n = this.sourcePort.length + this.destinationPort.length;
        if (this.connectionOriented) {
            n += this.sequenceNumber.length;
            n += this.acknowledgmentNumber.length;
            n += 16; // Tamaño del campo "urgentData"
        }
        // Determinar el tamaño del headerLength
        int headerLengthBits = (int) Math.ceil(Utilidades.log2(n + this.checksumSize));
        char[] headerLength = Utilidades.toArrayBinarie(headerLengthBits, Integer.toBinaryString(headerLengthBits).length());
        n += headerLength.length;

        // Verificar checksum
        char[] header = new char[n];
        System.arraycopy(encapsulatedData, 0, header, 0, n);
        
        char[] data = new char[encapsulatedData.length-header.length-this.checksumSize];
        System.arraycopy(encapsulatedData, header.length+this.checksumSize, data, 0, data.length);
        
        char[] dataWithHeader = new char[data.length+header.length];
        System.arraycopy(header, 0, dataWithHeader, 0, header.length);
        System.arraycopy(data, 0, dataWithHeader, header.length,data.length);
        
        char[] calculatedChecksum = this.doCheckSum(dataWithHeader);
        
        // El checksum empieza donde acaba el header
        int checksumStart = header.length;

        char[] receivedChecksum = new char[this.checksumSize];
        System.arraycopy(encapsulatedData, checksumStart, receivedChecksum, 0, this.checksumSize);
   
        // Verificar si los checksums coinciden
        if (!Arrays.equals(calculatedChecksum, receivedChecksum)) {
            return null;
        }
        return data;
    }

    @Override
    public char[] generateHeader(char [] data, char[] urgentData) {
        int size = this.sourcePort.length + this.destinationPort.length;
        char[] header;
        int n = 0;
        header = new char[size];
        System.arraycopy(sourcePort, 0, header, n, sourcePort.length);
        n+= sourcePort.length;
        System.arraycopy(destinationPort, 0, header, n, destinationPort.length);
        n+= destinationPort.length;
        if(this.connectionOriented){
            Utilidades.addBitToArrayBinarie(this.sequenceNumber); //Used to identify the lost segments and maintain the sequencing in transmission.
            char[] urgent = new char[16];
            if(urgentData != null){
                urgent = urgentData;
            }else{
                Arrays.fill(urgent, '0');
            }
            size += sequenceNumber.length + acknowledgmentNumber.length + urgent.length;
            char[] temp = new char[size];
            System.arraycopy(header, 0, temp, 0, header.length);
            header = new char[size];
            System.arraycopy(temp, 0, header, 0, size);
            
            System.arraycopy(this.sequenceNumber, 0, header, n, this.sequenceNumber.length);
            n+= this.sequenceNumber.length;
            System.arraycopy(this.acknowledgmentNumber, 0, header, n, this.acknowledgmentNumber.length);
            n+= this.acknowledgmentNumber.length;
            System.arraycopy(urgent, 0, header, n, urgent.length);
            n+= urgent.length;
        }
        
        int headerLengthBits = (int)Math.ceil(Utilidades.log2(size+ this.checksumSize));
        char[] headerLength = Utilidades.toArrayBinarie(headerLengthBits, Integer.toBinaryString(headerLengthBits).length());
        
        size += this.checksumSize + headerLength.length;
        char[] temp = new char[size];
        System.arraycopy(header, 0, temp, 0, header.length);
        header = new char[size];
        System.arraycopy(temp, 0, header, 0, header.length);
        
        System.arraycopy(headerLength, 0, header, n, headerLength.length);
        n+= headerLength.length;
        char[] c = new char[data.length + header.length];

        System.arraycopy(header,0,c,0,header.length);
        System.arraycopy(data,0,c,header.length,data.length);
        
        char[] checksum = this.doCheckSum(c);  
        System.arraycopy(checksum, 0, header, n, this.checksumSize);
        n+= this.checksumSize;
        return header;
    }
    
    @Override
    public char[] generateHeader(char[] data) {
        return generateHeader(data, null);
    }

    @Override
    public char[] generateTrailer(char[] data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public char[] doCheckSum(char[] data) {
        int checksum = 0;
        for (int i = 0; i < data.length; i += 2) {
            int highByte = data[i] << 8; // Desplazar el byte alto
            int lowByte = (i + 1 < data.length) ? data[i + 1] : 0; // Obtener el byte bajo
            checksum += (highByte | lowByte); // Sumar los dos bytes
            checksum = (checksum & 0xFFFF) + (checksum >> 16); // Manejar el acarreo
        }
        checksum = ~checksum & 0xFFFF; // Complemento a uno
        return Utilidades.toArrayBinarie(checksum, this.checksumSize); // Retornar el checksum en forma binaria
    } 
}
