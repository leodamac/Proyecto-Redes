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

public class Segmento {
    private int numeroSecuencia;
    private String datos;
    private boolean perdido;
    private boolean corrompido;

    public Segmento(int numeroSecuencia, String datos) {
        this.numeroSecuencia = numeroSecuencia;
        this.datos = datos;
        this.perdido = false;
        this.corrompido = false;
    }

    public int getNumeroSecuencia() {
        return numeroSecuencia;
    }

    public String getDatos() {
        return datos;
    }

    public boolean isPerdido() {
        return perdido;
    }

    public void setPerdido(boolean perdido) {
        this.perdido = perdido;
    }

    public void corromperDatos() {
        Random rand = new Random();
        char[] datosArray = datos.toCharArray();
        int index = rand.nextInt(datosArray.length);
        char newCaracter = (char)(rand.nextInt(26) + 'a');
//        System.out.println(datosArray[index]);
        datosArray[index] = newCaracter;  // Cambiar un carácter aleatoriamente
//        System.out.println(newCaracter);
        this.datos = new String(datosArray);
        this.corrompido = true;
    }

    public boolean isCorrompido() {
        return corrompido;
    }
}