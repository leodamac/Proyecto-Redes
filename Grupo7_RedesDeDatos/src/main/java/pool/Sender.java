package pool;

import ec.edu.espol.capas.Layer;
import java.util.Arrays;
import java.util.Random;

public class Sender implements Runnable {
        private static final double PROBABILIDAD_PERDIDA = 0.01;
        private static final double PROBABILIDAD_CORRUPCION = 0.05;
        Layer layer1;
        Layer layer2;
        private volatile boolean end = false;
        private int i = 0;
        static int counter = 0;
        private volatile boolean sinPerdida = true;
        public Sender(Layer layer1, Layer layer2, boolean sinPerdida){
            this.layer1 = layer1;
            this.layer2 = layer2;
            this.i = ++counter;
            this.sinPerdida = sinPerdida;
        }
        
        public Sender(Layer layer1, Layer layer2){
            this(layer1, layer2, false);
        }
        
        public void finish(){
            this.end = true;
        }
        
        public void corromperDatos(char[] datosArray) {
            Random rand = new Random();
            int index = rand.nextInt(datosArray.length);
            char newCaracter = (char)(rand.nextInt(256));
            datosArray[index] = newCaracter;
        }

        private char[] simularErrores(char[] datosArray) {
            Random rand = new Random();
            if(sinPerdida){
                if (rand.nextDouble() < PROBABILIDAD_PERDIDA) {
                    Arrays.fill(datosArray, '0');
                }

                if (rand.nextDouble() < PROBABILIDAD_CORRUPCION) {
                    corromperDatos(datosArray);
                }
            }
            return datosArray;
        }
        
        @Override
        public void run() {
          try {
            System.out.println("\n*** Sender " + i +" Iniciado ***\n");
            while (!end) {
                char[] data = recieve();
                
                if(data!= null){
                    if(new String(data).equals("-1")){
                        break;
                    }
                    simularErrores(data);
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
            }
            System.out.println("\n*** Sender " + i +" Finalizado ***\n");
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