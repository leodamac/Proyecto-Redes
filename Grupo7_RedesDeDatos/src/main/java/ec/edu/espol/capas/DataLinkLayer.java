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
