/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ec.edu.espol.grupo7_redesdedatos;

import java.util.Scanner;

/**
 *
 * @author evin
 */
public class Grupo7_RedesDeDatos {

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        Scanner sc = new Scanner(System.in);
        String mensaje = sc.nextLine();
        cliente.enviarDatos(mensaje);
    }
}
