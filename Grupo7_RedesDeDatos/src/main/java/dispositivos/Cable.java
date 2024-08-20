package dispositivos;

import ec.edu.espol.capas.PhysicalLayer;

public class Cable {
    
    private PhysicalLayer physicalLayer;
    private String MAC;
    
    public Cable() {
        this.physicalLayer = new PhysicalLayer();
    }

    public PhysicalLayer getPhysicalLayer() {
        return physicalLayer;
    }
    
    public void setMAC(String MAC){
        this.MAC = MAC;
    }

    public String getMAC() {
        return MAC;
    }
    
    
    
    
}
