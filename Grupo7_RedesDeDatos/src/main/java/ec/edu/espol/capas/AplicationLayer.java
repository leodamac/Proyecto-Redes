package ec.edu.espol.capas;

public class AplicationLayer extends Layer {
    private final boolean connectionOriented;
    
    private TransporLayer transportLayer;

    public AplicationLayer(boolean connectionOriented) {
        super((short)5, "data");
        this.connectionOriented = connectionOriented;
    }
    
    public void conectTransportLayer(TransporLayer transportLayer){
        this.transportLayer = transportLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
    }
    
    @Override
    public char[] encapsulation(char[] data, boolean urgentFlag, char[] urgentData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public char[] encapsulation(char[] data) {
        if(this.connectionOriented){
            return data;
        }
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