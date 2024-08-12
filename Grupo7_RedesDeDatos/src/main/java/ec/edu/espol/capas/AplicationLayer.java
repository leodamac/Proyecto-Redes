
package ec.edu.espol.capas;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AplicationLayer extends Layer {
    private final boolean connectionOriented;
    private final int dataSize = 1024;
    private TransporLayer transportLayer;

    public AplicationLayer(boolean connectionOriented) {
        this.dataGram = "data";
        this.level = 4;
        this.connectionOriented = connectionOriented;
    }
    
    private void conectTransportLayer(TransporLayer transportLayer){
        this.transportLayer = transportLayer;
    }
    
    private FileReader openFile(String path){
        try (FileReader fr = new FileReader(path)) {
            return fr;
        } catch (IOException e) {
            return null;
        }
    }
    
    private char[] dividirData(FileReader fr, int n) throws IOException{
        char[] buffer = new char[dataSize];
        int charLeidos;
        charLeidos = fr.read(buffer, n, dataSize);   
        if(charLeidos>0){
            return buffer;
        }
        return null;
    }
    
    private boolean sendData(char[] data){
        if(this.transportLayer != null){
            return transportLayer.sendSegmentToNetworkLayer(data);
        }
        return false;
    }
    
    private char[] recibeData(){
        char[] data = new char[dataSize];
        if(this.transportLayer != null){
            return transportLayer.sendDataToAplicationLayer();
        }
        return null;
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