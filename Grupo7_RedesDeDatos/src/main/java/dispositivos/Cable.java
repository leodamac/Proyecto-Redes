package dispositivos;

import ec.edu.espol.capas.PhysicalLayer;

public class Cable {
    
    private PhysicalLayer physicalLayer;
    public Cable() {
        this.physicalLayer = new PhysicalLayer();
    }

    public PhysicalLayer getPhysicalLayer() {
        return physicalLayer;
    }
    
}
