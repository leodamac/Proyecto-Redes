package pool;

import ec.edu.espol.capas.Layer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Sender implements Runnable {
        private double PROBABILIDAD_PERDIDA = 0.10;
        private double PROBABILIDAD_CORRUPCION = 0.10;
        Layer layer1;
        Layer layer2;
        private volatile boolean end = false;
        private int i = 0;
        static int counter = 0;
        private volatile boolean sinPerdida = true;
        private volatile int datosCorruptos;
        private volatile int datosPerdidos;
        private Semaphore mutex = new Semaphore(1);
        
        public Sender(Layer layer1, Layer layer2, boolean sinPerdida, double PROBABILIDAD_PERDIDA, double PROBABILIDAD_CORRUPCION){
            this.layer1 = layer1;
            this.layer2 = layer2;
            this.i = ++counter;
            this.sinPerdida = sinPerdida;
            this.datosCorruptos = 0;
            this.datosPerdidos = 0;
            this.PROBABILIDAD_PERDIDA = PROBABILIDAD_PERDIDA;
            this.PROBABILIDAD_CORRUPCION = PROBABILIDAD_CORRUPCION;
        }
        
        public Sender(Layer layer1, Layer layer2, boolean sinPerdida){
            this(layer1, layer2, sinPerdida, 0.1, 0.1);
        }
        
        public Sender(Layer layer1, Layer layer2){
            this(layer1, layer2, true, 0.1, 0.1);
        }

        public int getDatosCorruptos() {
            return datosCorruptos;
        }

        public int getDatosPerdidos() {
            return datosPerdidos;
        }
        
        public void finish(){
            this.end = true;
        }
        
        public void corromperDatos(char[] datosArray) {
            Random rand = new Random();
            int index = rand.nextInt(datosArray.length);
            char newCaracter = (char)(rand.nextInt(256));
            while(newCaracter == '|'){
                newCaracter = (char)(rand.nextInt(256));
            }
            while(datosArray[index] == '|'){
                index = rand.nextInt(datosArray.length);
            }
            datosArray[index] = newCaracter;
        }

        private char[] simularErrores(char[] datosArray) {
            Random rand = new Random();
            if(!sinPerdida){
                if (rand.nextDouble() < PROBABILIDAD_PERDIDA) {
                    Arrays.fill(datosArray, '0');
                    this.datosPerdidos++;
                    System.out.println("*P*");
                    
                }else if (rand.nextDouble() < PROBABILIDAD_CORRUPCION) {
                    corromperDatos(datosArray);
                    this.datosCorruptos++;
                    
                }
            }
            return datosArray;
        }
        
        @Override
        public void run() {
          try {
            System.out.println("\n*** Sender " + i +" Iniciado ***\n");
            while (!end) {
                mutex.acquire();
                char[] data = recieve();
                mutex.release();
                
                if(data!= null){
                    System.out.println( "^^" + new String(data)+ "|" + data.length + "==" +  0);
                    if(data.length > 5){
                        simularErrores(data);
                    }
                    if(!isPerdido(data)){
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
            }
            System.out.println("\n*** Sender " + i +" Finalizado ***\n");
          } catch (InterruptedException ex) {
              System.out.println(ex);
          }
        }
        
        public char[] recieve() {
            try {
                if(this.layer1.getLevel() > this.layer2.getLevel()){
                    return this.layer1.receiveDataSuperior();
                }else if(this.layer1.getLevel() == this.layer2.getLevel()){
                    return this.layer1.receiveDataEqual();
                }else{
                    return this.layer1.receiveDataInferior();
                }
            } catch (InterruptedException ex) {
                return null;
            }
        }
        
        private boolean isPerdido(char[] data){
            int n = 0;
            for(char c: data){
                if(c == '0'){
                    n++;
                }
            }
            return data.length == n;
        }
    }