package ec.edu.espol.capas;

public class DataLinkLayer extends Layer{
    private PhysicalLayer physicalLayer;
    private NetworkLayer networkLayer;
    private String MAC;
    private String IP;

    public DataLinkLayer(String MAC) {
        super((short)2, "trama");
        this.MAC = MAC;
    }

    public PhysicalLayer getPhysicalLayer() {
        return physicalLayer;
    }

    public NetworkLayer getNetworkLayer() {
        return networkLayer;
    }

    public String getMAC() {
        return MAC;
    }

    public String getIP() {
        return IP;
    }
    
    
    public boolean conectToPhysicalLayer(PhysicalLayer physicalLayer){
        if(physicalLayer == null){
            return false;
        }
        this.physicalLayer = physicalLayer;
        return true;
    }
    
    public boolean isClosed(){
        return this.getNetworkLayer().getTransportLayer().getAplicationLayer().isClosed();
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

    @Override
    public char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] encapsulation(char[] data) {
        char[] header = generateHeader(data);
        // Combinamos el header y los datos en un solo array
        char[] packet = new char[header.length + data.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(data, 0, packet, header.length, data.length);
        return packet;
    }

    @Override
    public char[] desencapsulation(char[] data) {
        // Tamaño fijo del header: 6 (source MAC) + 6 (destination MAC) + longitud de datos (variable)
        int headerLength = 13; // 12 caracteres

        // Extraemos la parte de los datos excluyendo el header
        int dataLength = data.length - headerLength;
        char[] extractedData = new char[dataLength];

        System.arraycopy(data, headerLength, extractedData, 0, dataLength);

        return extractedData;
    }

    @Override
    public char[] generateHeader(char[] data) {
        String sourceMAC = this.MAC;
        String destinationMAC = this.physicalLayer.getDataLinkLayer().MAC;
        int dataLength = data.length;

        // Convertimos la información del header en un String
        String headerString = sourceMAC + "|" + destinationMAC;
        
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
