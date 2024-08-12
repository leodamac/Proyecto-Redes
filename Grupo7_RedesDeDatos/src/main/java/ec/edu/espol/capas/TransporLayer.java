package ec.edu.espol.capas;

import extras.Utilidades;
import java.util.Arrays;

public class TransporLayer extends Layer{
    private final boolean connectionOriented;
    private final int sizeBitsPort = 16;
    private final static int nBitsSegments = 32;
    private char[] sourcePort = new char[sizeBitsPort]; //sourcePort
    private char[] destinationPort = new char[sizeBitsPort]; //destinationPort
    private static char[] sequenceNumber = Utilidades.toArrayBinarie(0 ,nBitsSegments); //Used to identify the lost segments and maintain the sequencing in transmission.
    private static char[] acknowledgmentNumber = Utilidades.toArrayBinarie(0 ,nBitsSegments);; //Used to send a verification of received segments and to ask for the next segments
    private final int checksumSize = 16;
    
    public TransporLayer(boolean connectionOriented, int sourcePort, int destinationPort){
        this.dataGram = "segment";
        this.level = 3;
        this.connectionOriented = connectionOriented;
        this.sourcePort = Utilidades.toArrayBinarie(sourcePort, this.sizeBitsPort);
        this.destinationPort = Utilidades.toArrayBinarie(destinationPort, this.sizeBitsPort);
    }
    
    public boolean sendSegmentToNetworkLayer(char[] segment){
        return true;
    }
    
    public boolean recibeFrameForNetworkLayer(char[] frame){
        return true;
    }
    
    public char[] sendDataToAplicationLayer(){
        char[] data = new char[1024];
        return data;
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
    public char[] desencapsulation(char[] data) {
        int offset = 0;
        
        char[] extractedSourcePort = Arrays.copyOfRange(data, offset, offset + sizeBitsPort);
        offset += sizeBitsPort;

        char[] extractedDestinationPort = Arrays.copyOfRange(data, offset, offset + sizeBitsPort);
        offset += sizeBitsPort;

        if (this.connectionOriented) {
            char[] extractedSequenceNumber = Arrays.copyOfRange(data, offset, offset + nBitsSegments);
            offset += nBitsSegments;

            char[] extractedAcknowledgmentNumber = Arrays.copyOfRange(data, offset, offset + nBitsSegments);
            offset += nBitsSegments;

            char[] urgentPointer = Arrays.copyOfRange(data, offset, offset + 16);
            offset += 16;
        }

        int headerLength = Utilidades.toIntFromBinary(Arrays.copyOfRange(data, offset, offset + 16));
        offset += 16;

        // Obtener el checksum
        char[] extractedChecksum = Arrays.copyOfRange(data, offset, offset + checksumSize);
        offset += checksumSize;

        // Verificar el checksum
        char[] headerData = Arrays.copyOfRange(data, 0, offset);
        char[] extractedData = Arrays.copyOfRange(data, offset, data.length);
        char[] calculatedChecksum = doCheckSum(extractedData);

        if (!Arrays.equals(extractedChecksum, calculatedChecksum)) {
            throw new IllegalArgumentException("Checksum mismatch: data may be corrupted");
        }

        // Retornar los datos sin el encabezado
        return extractedData;
    }

    @Override
    public char[] generateHeader(char [] data, char[] urgentData) {
        int size = this.sourcePort.length + this.destinationPort.length;
        char[] header = new char[size];
        int n = 0;
        System.arraycopy(sourcePort, 0, header, n, sourcePort.length);
        n+= sourcePort.length;
        System.arraycopy(destinationPort, 0, header, n, destinationPort.length);
        n+= destinationPort.length;
        if(this.connectionOriented){
            Utilidades.addBitToArrayBinarie(TransporLayer.sequenceNumber); //Used to identify the lost segments and maintain the sequencing in transmission.
            char[] urgent = new char[16];
            if(urgentData != null){
                urgent = urgentData;
            }else{
                Arrays.fill(urgent, '0');
            }
            System.arraycopy(TransporLayer.sequenceNumber, 0, header, n, TransporLayer.sequenceNumber.length);
            n+= TransporLayer.sequenceNumber.length;
            System.arraycopy(TransporLayer.acknowledgmentNumber, 0, header, n, TransporLayer.acknowledgmentNumber.length);
            n+= TransporLayer.acknowledgmentNumber.length;
            System.arraycopy(urgent, 0, header, n, urgent.length);
            n+= urgent.length;
            size += sequenceNumber.length + acknowledgmentNumber.length + urgent.length;
        }
        char[] headerLength = Utilidades.toArrayBinarie((int)Math.ceil(Utilidades.log2(size)), size);
        System.arraycopy(headerLength, 0, header, n, headerLength.length);
        n+= headerLength.length;
        char[] checksum = this.doCheckSum(data);  
        System.arraycopy(checksum, 0, header, n, checksum.length);
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
