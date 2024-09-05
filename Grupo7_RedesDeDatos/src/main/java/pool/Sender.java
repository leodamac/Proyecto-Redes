package pool;

import ec.edu.espol.capas.Layer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Sender implements Runnable {
        // Probabilidad de pérdida y corrupción de datos
        private double PROBABILIDAD_PERDIDA = 0.10; // Probabilidad de que se pierda un paquete
        private double PROBABILIDAD_CORRUPCION = 0.10; // Probabilidad de que se corrompa un paquete
        
        // Referencias a las capas de la red
        Layer layer1;
        Layer layer2;
        
        // Variable para controlar el fin de la ejecución del hilo
        private volatile boolean end = false;
        
        // Índice del hilo actual
        private int i = 0;
        // Contador para asignar índices a los hilos
        static int counter = 0;
        
        // Variable para controlar si se simulan pérdidas de datos
        private volatile boolean sinPerdida = true; 
        
        // Variables para el seguimiento de los datos corruptos y perdidos
        private volatile int datosCorruptos;
        private volatile int datosPerdidos;
        
        // Semaforo para la sincronización del acceso a los datos
        private Semaphore mutex = new Semaphore(1);
        
        /**
         * Constructor de la clase Sender.
         * 
         * @param layer1 La primera capa de la red.
         * @param layer2 La segunda capa de la red.
         * @param sinPerdida  Indica si se simulan pérdidas de datos o no.
         * @param PROBABILIDAD_PERDIDA Probabilidad de pérdida de datos.
         * @param PROBABILIDAD_CORRUPCION Probabilidad de corrupción de datos.
         */
        public Sender(Layer layer1, Layer layer2, boolean sinPerdida, double PROBABILIDAD_PERDIDA, double PROBABILIDAD_CORRUPCION){
            this.layer1 = layer1;
            this.layer2 = layer2;
            this.i = ++counter; // Asignar índice al hilo actual
            this.sinPerdida = sinPerdida;  // Establecer la variable sinPerdida
            this.datosCorruptos = 0; // Inicializar el contador de datos corruptos
            this.datosPerdidos = 0; // Inicializar el contador de datos perdidos
            this.PROBABILIDAD_PERDIDA = PROBABILIDAD_PERDIDA; // Establecer la probabilidad de pérdida
            this.PROBABILIDAD_CORRUPCION = PROBABILIDAD_CORRUPCION; // Establecer la probabilidad de corrupción
        }
        
        /**
         * Constructor de la clase Sender con valores por defecto para la probabilidad de pérdida y corrupción
         * 
         * @param layer1 La primera capa de la red.
         * @param layer2 La segunda capa de la red.
         * @param sinPerdida  Indica si se simulan pérdidas de datos o no.
         */
        public Sender(Layer layer1, Layer layer2, boolean sinPerdida){
            this(layer1, layer2, sinPerdida, 0.1, 0.1); // Llamar al constructor con los valores por defecto
        }
        
        /**
         * Constructor de la clase Sender con valores por defecto para la probabilidad de pérdida y corrupción,
         * y con sinPerdida establecido en true
         * 
         * @param layer1 La primera capa de la red.
         * @param layer2 La segunda capa de la red.
         */
        public Sender(Layer layer1, Layer layer2){
            this(layer1, layer2, true, 0.1, 0.1); // Llamar al constructor con los valores por defecto
        }

        /**
         * Obtiene el número de datos corruptos.
         * 
         * @return El número de datos corruptos.
         */
        public int getDatosCorruptos() {
            return datosCorruptos;
        }

        /**
         * Obtiene el número de datos perdidos.
         * 
         * @return El número de datos perdidos.
         */
        public int getDatosPerdidos() {
            return datosPerdidos;
        }
        
        /**
         * Finaliza la ejecución del hilo.
         */
        public void finish(){
            this.end = true; // Establecer la variable end en true
        }
        
        /**
         * Corrompe un caracter aleatorio del array de datos.
         * 
         * @param datosArray El array de datos a corromper.
         */
        public void corromperDatos(char[] datosArray) {
            // Generar un número aleatorio para seleccionar un índice del array
            Random rand = new Random();
            int index = rand.nextInt(datosArray.length);
            // Generar un nuevo caracter aleatorio
            char newCaracter = (char)(rand.nextInt(256));
            // Verificar si el nuevo caracter es "|"
            while(newCaracter == '|'){
                newCaracter = (char)(rand.nextInt(256));
            }
            // Verificar si el caracter en el índice seleccionado es "|"
            while(datosArray[index] == '|'){
                index = rand.nextInt(datosArray.length);
            }
            // Reemplazar el caracter en el índice seleccionado por el nuevo caracter
            datosArray[index] = newCaracter;
        }

        /**
         * Simula errores en la transferencia de datos, como pérdida y corrupción.
         * 
         * @param datosArray El array de datos a simular errores.
         * @return El array de datos con los errores simulados.
         */
        private char[] simularErrores(char[] datosArray) {
            Random rand = new Random();
            // Verificar si se simulan pérdidas de datos
            if(!sinPerdida){
                // Simular la pérdida de datos
                if (rand.nextDouble() < PROBABILIDAD_PERDIDA) {
                    Arrays.fill(datosArray, '0'); // Rellenar el array con '0'
                    this.datosPerdidos++; // Incrementar el contador de datos perdidos
                    System.out.println("*P*"); // Imprimir un mensaje para indicar que se ha perdido un paquete
                    
                // Simular la corrupción de datos
                }else if (rand.nextDouble() < PROBABILIDAD_CORRUPCION) {
                    corromperDatos(datosArray); // Corromper los datos
                    this.datosCorruptos++; // Incrementar el contador de datos corruptos
                    
                }
            }
            // Devolver el array de datos con los errores simulados
            return datosArray;
        }
        
        /**
         * Método que se ejecuta cuando el hilo es iniciado.
         * 
         * @Override
         */
        @Override
        public void run() {
          try {
            // Imprimir un mensaje para indicar que el hilo ha sido iniciado
            System.out.println("\n*** Sender " + i +" Iniciado ***\n");
            // Bucle infinito para recibir y enviar datos
            while (!end) {
                // Obtener el mutex para acceder a los datos de forma sincronizada
                mutex.acquire();
                // Recibir datos de la capa superior
                char[] data = recieve();
                // Liberar el mutex
                mutex.release();
                
                // Verificar si se han recibido datos
                if(data!= null){
                    // Imprimir un mensaje con los datos recibidos
                    System.out.println( "^^" + new String(data)+ "|" + data.length + "==" +  0);
                    // Verificar si la longitud de los datos es mayor que 5
                    if(data.length > 5){
                        // Simular errores en los datos
                        simularErrores(data);
                    }
                    // Verificar si los datos no se han perdido
                    if(!isPerdido(data)){
                        // Imprimir un mensaje con la información de la capa actual
                        System.out.println("Layer 1: "+ layer1.getLevel() + " Layer 2: " + layer2.getLevel() + "\nProcessing data: " + Arrays.toString(data));
                        // Verificar si la capa 1 está en un nivel superior a la capa 2
                        if(this.layer1.getLevel() > this.layer2.getLevel()){
                            // Enviar los datos a la capa inferior
                            this.layer1.sendDataInferior(data);
                            System.out.println("Sending data to lower layer");
                            // Agregar los datos al pool de la capa superior
                            this.layer2.getPoolInSuperior().add(this.layer1.getPoolOutInferior().take());
                            System.out.println("Data added to superior layer pool");
                        // Verificar si la capa 1 está en el mismo nivel que la capa 2
                        }else if(this.layer1.getLevel() == this.layer2.getLevel()){
                            // Enviar los datos a la capa igual
                            System.out.println("Sending data to EQUAL layer");
                            this.layer1.sendDataInferior(data);
                            System.out.println("Data added to EQUAL layer pool");
                            // Agregar los datos al pool de la capa igual
                            this.layer2.getPoolInInferior().add(this.layer1.getPoolOutInferior().take());
                        // Verificar si la capa 1 está en un nivel inferior a la capa 2
                        }else{
                            // Enviar los datos a la capa superior
                            System.out.println("Sending data to higher layer");
                            this.layer1.sendDataSuperior(data);
                            System.out.println("Data added to inferior layer pool");
                            // Agregar los datos al pool de la capa inferior
                            this.layer2.getPoolInInferior().add(this.layer1.getPoolOutSuperior().take());
                        }
                    }
                }
            }
            // Imprimir un mensaje para indicar que el hilo ha finalizado
            System.out.println("\n*** Sender " + i +" Finalizado ***\n");
          } catch (InterruptedException ex) {
              // Imprimir un mensaje de error si el hilo es interrumpido
              System.out.println(ex);
          }
        }
        
        /**
         * Recibe datos de la capa superior.
         * 
         * @return El array de datos recibido.
         * @throws InterruptedException Si el hilo es interrumpido.
         */
        public char[] recieve() {
            try {
                // Verificar el nivel de la capa 1
                if(this.layer1.getLevel() > this.layer2.getLevel()){
                    // Recibir datos de la capa superior
                    return this.layer1.receiveDataSuperior();
                }else if(this.layer1.getLevel() == this.layer2.getLevel()){
                    // Recibir datos de la capa igual
                    return this.layer1.receiveDataEqual();
                }else{
                    // Recibir datos de la capa inferior
                    return this.layer1.receiveDataInferior();
                }
            } catch (InterruptedException ex) {
                // Devolver null si el hilo es interrumpido
                return null;
            }
        }
        
        /**
         * Verifica si los datos se han perdido.
         * 
         * @param data El array de datos a verificar.
         * @return true si los datos se han perdido, false en caso contrario.
         */
        private boolean isPerdido(char[] data){
            // Contar el número de caracteres '0' en el array
            int n = 0;
            for(char c: data){
                if(c == '0'){
                    n++;
                }
            }
            // Verificar si la longitud del array es igual al número de caracteres '0'
            return data.length == n;
        }
    }