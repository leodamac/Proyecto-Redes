/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.espol.grupo7_redesdedatos;

/**
 *
 * @author evin
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Servidor {
    public static void recibirDatos(ArrayList<Segmento> segmentos) {
        // Filtrar segmentos no perdidos
        ArrayList<Segmento> segmentosRecibidos = new ArrayList<>();
        int cantidadPaquetesPerdidos = 0;
        for (Segmento segmento : segmentos) {
            if (!segmento.isPerdido()) 
                segmentosRecibidos.add(segmento);
            else
              cantidadPaquetesPerdidos++;
        }
        System.out.println("Paquetes perdidos: " + (cantidadPaquetesPerdidos * 100 / segmentos.size()) + "%");

        // Ordenar segmentos por número de secuencia
        Collections.sort(segmentosRecibidos, Comparator.comparingInt(Segmento::getNumeroSecuencia));

        // Mostrar datos recibidos
        StringBuilder datosRecibidos = new StringBuilder();
        for (Segmento segmento : segmentosRecibidos) {
            if (segmento.isCorrompido()) {
                System.out.println("Error: El segmento " + segmento.getNumeroSecuencia() + " está corrompido.");
            }
            datosRecibidos.append(segmento.getDatos());
        }

        System.out.println("Datos recibidos en el servidor: " + datosRecibidos.toString());
    }
}

