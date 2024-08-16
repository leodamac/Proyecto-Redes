package pool;

import ec.edu.espol.capas.Layer;
import java.util.Arrays;

public class Sender implements Runnable {
        Layer layer1;
        Layer layer2;
        
        public Sender(Layer layer1, Layer layer2){
            this.layer1 = layer1;
            this.layer2 = layer2;
        }
        
        @Override
        public void run() {
          try {
            while (true) {
                
                char[] data = recieve();
                if(Arrays.toString(data).equals("[-1]")){
                    break;
                }
                
                System.out.println("Layer 1: "+ layer1.getLevel() + " Layer 2: " + layer2.getLevel() + "\nProcessing data: " + Arrays.toString(data));
                if(this.layer1.getLevel() > this.layer2.getLevel()){
                    this.layer1.sendDataInferior(data);
                    System.out.println("Sending data to lower layer");
                    this.layer2.getPoolInSuperior().add(this.layer1.getPoolOutInferior().take());
                    System.out.println("Data added to superior layer pool");
                }else if(this.layer1.getLevel() == this.layer2.getLevel()){
                    System.out.println("Sending data to EQUAL layer");
                    this.layer1.sendDataInferior(data);
                    System.out.println("Data added to EQUAL layer pool");
                    this.layer2.getPoolInInferior().add(this.layer1.getPoolOutInferior().take());

                }else{
                    System.out.println("Sending data to higher layer");
                    this.layer1.sendDataSuperior(data);
                    System.out.println("Data added to inferior layer pool");
                    this.layer2.getPoolInInferior().add(this.layer1.getPoolOutSuperior().take());
                }
            }
          } catch (InterruptedException ex) {
          }
        }
        public char[] recieve() {
            try {
                if(this.layer1.getLevel() > this.layer2.getLevel()){
                    return this.layer1.receiveDataSuperior();
                }else if(this.layer1.getLevel() == this.layer2.getLevel()){
                    return this.layer1.receiveDataSuperior();
                }else{
                    return this.layer1.receiveDataInferior();
                }
            } catch (InterruptedException ex) {
                return null;
            }
        }
    }