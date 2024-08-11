/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.espol.grupo7_redesdedatos;

/**
 *
 * @author evin
 */
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Cliente {
    private static final double PROBABILIDAD_PERDIDA = 0.2;
    private static final double PROBABILIDAD_CORRUPCION = 0.1;
    private ArrayList<Segmento> segmentos;
    
    public Cliente() {
        this.segmentos = new ArrayList<>();
    }

    public void enviarDatos(String mensaje) {
        segmentarMensaje(mensaje);
        simularErrores();
        simularEnvioFueraDeOrden();
        Servidor.recibirDatos(segmentos);
    }

    private void segmentarMensaje(String mensaje) {
        int numeroSegmentos = mensaje.length() / 5 + 1;
        for (int i = 0; i < numeroSegmentos; i++) {
            int inicio = i * 5;
            int fin = Math.min(inicio + 5, mensaje.length());
            String datos = mensaje.substring(inicio, fin);
            segmentos.add(new Segmento(i, datos));
        }
    }

    private void simularErrores() {
        Random rand = new Random();
        for (Segmento segmento : segmentos) {
            // Simular pérdida aleatoria de paquetes
            if (rand.nextDouble() < PROBABILIDAD_PERDIDA) {
                segmento.setPerdido(true);
            }
            // Simular corrupción de datos
            if (rand.nextDouble() < PROBABILIDAD_CORRUPCION) {
                segmento.corromperDatos();
            }
        }
    }

    private void simularEnvioFueraDeOrden() {
        Collections.shuffle(segmentos);
    }
}