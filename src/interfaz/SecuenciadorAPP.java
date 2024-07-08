/**
 * Clase principal para la aplicacion
 *
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package interfaz;

import controlador.Controlador;

/**
 * Clase principal que sirve para la aplicacion interprete, se encarga de
 * iniciar el proceso de interpretacion de archivos.
 *
 * @author Mario Palencia
 * @author Luis Gabriel Romero
 */
public class SecuenciadorAPP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Dirección del archivo de entrada
        String direccionEntrada = "C:\\Users\\mario\\Downloads\\Secuenciar if while for - 2023 + Ejercicios.IN\\Prueba1.IN";
        // Dirección del archivo de salida
        String direccionSalida = "C:\\Users\\mario\\Downloads\\Secuenciar if while for - 2023 + Ejercicios.IN\\Prueba1_resultado.out";
        Controlador controlador = new Controlador();
        controlador.procesarArchivo(direccionEntrada, direccionSalida);
        
        
    }
}
