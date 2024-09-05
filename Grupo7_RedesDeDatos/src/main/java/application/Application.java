package application;

import ec.edu.espol.capas.AplicationLayer;
import ec.edu.espol.capas.TransporLayer;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pool.Sender;

/**
 * Clase principal de la aplicación que maneja el envío y recepción de archivos.
 */
public class Application implements Runnable {
    // Capas de la aplicación y transporte
    private AplicationLayer applicationLayer;
    private TransporLayer transportLayer;

    // Tamaño de los datos que se envían
    private final int dataSize = 128;

    // Ruta del archivo a enviar
    public static String path;

    // Contadores de datos enviados y recibidos
    private volatile int datosEnviados;
    private volatile int datosRecibidos;

    // Contadores de errores de corrupción y pérdida
    private volatile int dCorrupccion;
    private volatile int dPerdida;

    // Cola de prioridad para los mensajes
    public PriorityQueue<String> mensajesAlFinalDeTodo = new PriorityQueue<>((o1, o2) -> {
        // Comparar los mensajes por su ID
        int id1 = -1;
        try {
            id1 = Integer.parseInt(o1.split("\\|")[0]);
        } catch (NumberFormatException ex) {
            // Manejo de excepción
        }

        int id2 = -1;
        try {
            id2 = Integer.parseInt(o2.split("\\|")[0]);
        } catch (NumberFormatException ex) {
            // Manejo de excepción
        }
        return id1 - id2;
    });

    // Hilos para el envío y recepción de datos
    private Thread recibidorThread = null;
    private Thread enviadorThread = null;

    // Bandera para indicar si se debe enviar datos
    public String dataF = "";
    private volatile boolean close = false;
    private volatile boolean sendData = false;
    private volatile boolean sinPerdida = true;

    // Semáforos para sincronizar el acceso a los datos
    private Semaphore mutex = new Semaphore(1);
    private Semaphore mutex2 = new Semaphore(1);

    // Probabilidad de pérdida y corrupción
    private volatile double pPerdida;
    private volatile double pCorrupcion;

    /**
     * Constructor de la clase Application.
     *
     * @param connectionOriented Indica si la conexión es orientada o no
     * @param sinPerdida Indica si se debe simular pérdida de datos
     * @param pPerdida Probabilidad de pérdida de datos
     * @param pCorrupcion Probabilidad de corrupción de datos
     */
    public Application(boolean connectionOriented, boolean sinPerdida, double pPerdida, double pCorrupcion) {
        // Inicializar las capas de la aplicación y transporte
        this.applicationLayer = new AplicationLayer(connectionOriented);
        this.transportLayer = new TransporLayer(connectionOriented);
        this.transportLayer.setSourcePort(8080);
        this.transportLayer.setDestinationPort(10);
        this.applicationLayer.conectTransportLayer(transportLayer);
        this.transportLayer.connectToAplicationLayer(applicationLayer);

        // Inicializar los contadores de datos enviados y recibidos
        this.datosEnviados = 0;
        this.datosRecibidos = 0;
        this.sinPerdida = sinPerdida;
        this.dCorrupccion = 0;
        this.dPerdida = 0;
        this.pPerdida = pPerdida;
        this.pCorrupcion = pCorrupcion;
    }

    // Constructor por defecto
    public Application(boolean connectionOriented) {
        this(connectionOriented, true, 0.1, 0.1);
    }

    /**
     * Método para cerrar la aplicación.
     */
    public void close() {
        close = true;
        this.applicationLayer.close();
    }

    /**
     * Método para verificar si la aplicación está cerrada.
     *
     * @return true si la aplicación está cerrada, false en caso contrario
     */
    public boolean isClosed() {
        return this.close;
    }

    // Métodos get para obtener los contadores de datos enviados y recibidos
    public int getDatosEnviados() {
        return datosEnviados;
    }

    public int getDatosRecibidos() {
        return datosRecibidos;
    }

    // Métodos get para obtener los contadores de errores
    public int getdCorrupccion() {
        return dCorrupccion;
    }

    public int getdPerdida() {
        return dPerdida;
    }

    public AplicationLayer getApplicationLayer() {
        return applicationLayer;
    }

    public TransporLayer getTransportLayer() {
        return transportLayer;
    }

    /**
     * Método para enviar un archivo.
     *
     * @param path ruta del archivo a enviar
     * @throws IOException si ocurre un error al enviar el archivo
     */
    public void sendFile(String path) throws IOException {
        sendData = true;
        SendFile sf = new SendFile(path);
        enviadorThread = new Thread(sf);
        enviadorThread.start();
    }

    /**
     * Método para recibir datos y mostrarlos en un área de texto.
     *
     * @param textArea área de texto donde se mostrarán los datos
     * @param datosRecibidos etiqueta que muestra los datos recibidos
     */
    public void receiveDataFile(TextArea textArea, Label datosRecibidos) {
        ReceiveFile rf = new ReceiveFile(textArea, datosRecibidos);
        recibidorThread = new Thread(rf);
        recibidorThread.start();
    }

    // Métodos para obtener los hilos de envío y recepción
    public Thread getRecibidorThread() {
        return recibidorThread;
    }

    public Thread getEnviadorThread() {
        return enviadorThread;
    }

    @Override
    public void run() {
        // Crear senders para enviar datos entre capas
        Sender senderAppToTrans = new Sender(applicationLayer, transportLayer, sinPerdida, pPerdida, pCorrupcion);
        Sender senderTransToApp = new Sender(transportLayer, applicationLayer, sinPerdida, pPerdida, pCorrupcion);
		
		Sender senderTransToNetw = new Sender(transportLayer, transportLayer.getNetworkLayer(), sinPerdida, pPerdida, pCorrupcion);
        Sender senderNetToTrans = new Sender(transportLayer.getNetworkLayer(),transportLayer, sinPerdida, pPerdida, pCorrupcion);

        Sender senderNetToDat = new Sender(transportLayer.getNetworkLayer(),transportLayer.getNetworkLayer().getDataLinkLayer(), sinPerdida, pPerdida, pCorrupcion);
        Sender senderDatToNet = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer(),transportLayer.getNetworkLayer(), sinPerdida, pPerdida, pCorrupcion);

        Sender senderDatToPhy = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer(),transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(), sinPerdida, pPerdida, pCorrupcion);
        Sender senderPhyToDat = new Sender(transportLayer.getNetworkLayer().getDataLinkLayer().getPhysicalLayer(),transportLayer.getNetworkLayer().getDataLinkLayer(), sinPerdida, pPerdida, pCorrupcion);

        // Iniciar hilos para enviar datos
        new Thread(senderAppToTrans).start();
        new Thread(senderTransToApp).start();
		new Thread(senderTransToNetw).start();
        new Thread(senderNetToTrans).start();
        new Thread(senderNetToDat).start();
        new Thread(senderDatToNet).start();
        new Thread(senderDatToPhy).start();
        new Thread(senderPhyToDat).start();

        // Esperar a que se cierre la aplicación
        new Thread(() -> {
            while (!isClosed()) {
            }
            // Terminar los hilos de envío
            senderAppToTrans.finish();
            senderTransToApp.finish();
			senderTransToNetw.finish();
            senderNetToTrans.finish();
            senderNetToDat.finish();
            senderDatToNet.finish();
            senderDatToPhy.finish();
            senderPhyToDat.finish();
			

            // Actualizar contadores de errores
            this.dCorrupccion += senderAppToTrans.getDatosCorruptos();
            this.dCorrupccion += senderTransToApp.getDatosCorruptos();
            this.dCorrupccion += senderTransToNetw.getDatosCorruptos();
            this.dCorrupccion += senderNetToTrans.getDatosCorruptos();
            this.dCorrupccion += senderNetToDat.getDatosCorruptos();
            this.dCorrupccion += senderDatToNet.getDatosCorruptos();
            this.dCorrupccion += senderDatToPhy.getDatosCorruptos();
            this.dCorrupccion += senderPhyToDat.getDatosCorruptos();
            

            this.dPerdida += senderAppToTrans.getDatosPerdidos();
            this.dPerdida += senderTransToApp.getDatosPerdidos();
            this.dPerdida += senderTransToNetw.getDatosPerdidos();
            this.dPerdida += senderNetToTrans.getDatosPerdidos();
            this.dPerdida += senderNetToDat.getDatosPerdidos();
            this.dPerdida += senderDatToNet.getDatosPerdidos();
            this.dPerdida += senderDatToPhy.getDatosPerdidos();
            this.dPerdida += senderPhyToDat.getDatosPerdidos();

            System.out.println(dPerdida + " " + dCorrupccion);
        }).start();
    }

    /**
     * Clase interna para recibir datos y mostrarlos en un área de texto.
     */
    class ReceiveFile implements Runnable {
        private TextArea textArea;
        private Label labelDatosRecibidos;

        public ReceiveFile(TextArea textArea, Label datosRecibidos) {
            this.textArea = textArea;
            this.labelDatosRecibidos = datosRecibidos;
        }

        @Override
        public void run() {
            // Inicializar variables
            boolean fin = false;
            int indiceFinal = -1;
            boolean salioF = false;
            int llegados = 0;
            textArea.clear();

            // Esperar a que se reciban todos los datos
            while (!fin) {
                try {
                    char[] seg = applicationLayer.getPoolInInferior().take();
                    llegados++;
                    if (seg != null) {
                        // Procesar el segmento recibido
                        String mensajeFull = new String(seg);
                        System.out.println("\n***" + mensajeFull + "***\n" + mensajesAlFinalDeTodo.size());
                        if (seg.length == 1 && seg[0] == 'f') {
                            salioF = true;
                        } else if (!mensajeFull.contains("|")) {
                            // Si el mensaje no contiene "|", es posible que sea un dato perdido
                            try {
                                indiceFinal = Integer.parseInt(mensajeFull);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } else {
                            // Agregar el mensaje a la cola de prioridad
                            mensajesAlFinalDeTodo.offer(mensajeFull);
                        }
                        System.out.println("\n***" + indiceFinal + "=" + mensajesAlFinalDeTodo.size() + "***\n");
                        if (salioF && indiceFinal == mensajesAlFinalDeTodo.size()) {
                            fin = true;
                        }
                    } else {
                        // Si no se recibió nada, esperar un tiempo y volver a intentarlo
                        boolean tomo = false;
                        int tiempo = 1;
                        int tiempoFinal = 4;

                        while (!tomo && tiempo < tiempoFinal && !close) {
                            if (sendData) {
                                tiempoFinal = 6;
                                sendData = false;
                            }
                            System.out.println("Esperando " + tiempo + " segundos");
                            seg = applicationLayer.getPoolInInferior().take(tiempo++);
                            if (seg == null) {
                                if (llegados == indiceFinal) {
                                    fin = true;
                                }
                            } else {
                                tomo = true;
                                String mensajeFull = new String(seg);
                                System.out.println("\n***" + mensajeFull + "***\n" + mensajesAlFinalDeTodo.size());
                                if (seg.length == 1 && seg[0] == 'f') {
                                    salioF = true;
                                } else if (!mensajeFull.contains("|")) {
                                    try {
                                        indiceFinal = Integer.parseInt(mensajeFull);
                                    } catch (NumberFormatException e) {
                                        System.out.println(e);
                                    }
                                } else {
                                    mensajesAlFinalDeTodo.offer(mensajeFull);
                                }
                                System.out.println("\n***" + indiceFinal + "=" + mensajesAlFinalDeTodo.size() + "***\n");
                                if (salioF && indiceFinal == mensajesAlFinalDeTodo.size()) {
                                    fin = true;
                                }
                            }
                        }
                        if (!tomo) {
                            fin = true;
                        }
                    }
                    if (close) {
                        fin = true;
                    }
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                    fin = true;
                }
            }
            if (!close) {
                // Mostrar los mensajes en el área de texto
                while (!mensajesAlFinalDeTodo.isEmpty()) {
                    String mensaje = mensajesAlFinalDeTodo.poll();
                    String contenido = mensaje.split("\\|")[1];
                    System.out.println(contenido);
                    try {
                        mutex2.acquire();
                        datosRecibidos += contenido.length();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        mutex2.release();
                    }
                    Platform.runLater(() -> {
                        textArea.appendText(contenido);
                        labelDatosRecibidos.setText("Datoss recibidos: " + datosRecibidos + "\nPaquetes Recibidos: " + (datosRecibidos / dataSize));
                    });
                }
            }
            System.out.println("Paró de recibir datos.");
        }
    }

    /**
     * Clase interna para enviar un archivo.
     */
    class SendFile implements Runnable {
        private String path;

        public SendFile(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                sendFile();
            } catch (IOException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Método para abrir el archivo.
         *
         * @param path ruta del archivo
         * @return FileReader para leer el archivo
         */
        private FileReader openFile(String path) {
            try {
                return new FileReader(path);
            } catch (IOException e) {
                return null;
            }
        }

        /**
         * Método para enviar el archivo.
         *
         * @return true si se envió correctamente, false en caso contrario
         * @throws IOException si ocurre un error al enviar el archivo
         */
        public boolean sendFile() throws IOException {
            ArrayList<char[]> segmentos = new ArrayList<>();
            boolean fin = false;
            FileReader fr = openFile(path);
            int i = 0;
            if (fr != null) {
                while (!fin) {
                    try {
                        char[] data = this.dividirData(fr, i);
                        if (data == null) {
                            fin = true;
                            fr.close();
                        } else {
                            System.out.println(i++);
                            segmentos.add(data);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AplicationLayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                return false;
            }
            System.out.println("segmentosss");
            for (char[] s : segmentos) {
                try {
                    System.out.println("***" + Arrays.toString(s) + "***\n");
                    if (!applicationLayer.isClosed()) {
                        applicationLayer.getPoolInSuperior().add(s);
                    } else {
                        return false;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!applicationLayer.isClosed()) {
                String fString = "f";
                try {
                    applicationLayer.getPoolInSuperior().add(fString.toCharArray());
                    fString = "" + i;
                    applicationLayer.getPoolInSuperior().add(fString.toCharArray());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return true;
        }

        /**
         * Método para dividir el archivo en segmentos.
         *
         * @param fr FileReader para leer el archivo
         * @param indice índice del segmento
         * @return char[] segmento del archivo
         * @throws IOException si ocurre un error al leer el archivo
         */
        private char[] dividirData(FileReader fr, int indice) throws IOException {
            char[] buffer = new char[dataSize];
            String mensaje = indice + "|";
            int charLeidos = 0;
            int x = 1;
            while (charLeidos < dataSize && x != -1) {
                x = fr.read();
                if (x != -1) {
                    buffer[charLeidos] = (char) x;
                    charLeidos++;
                }
            }
            if (charLeidos > 0) {
                String payLoad = new String(buffer);
                String m = mensaje + payLoad + "|" + String.valueOf(charLeidos);
                try {
                    mutex.acquire();
                    datosEnviados += payLoad.length();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    mutex.release();
                }
                return m.toCharArray();
            }
            return null;
        }
    }
}