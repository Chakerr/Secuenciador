/**
 * Se encarga de leer un documento, procesar una matriz y generar un archivo de salida
 */
package controlador;

import modelo.LogicaSecuenciador;

/**
 * Representa un controlador para interactuar con un interprete
 *
 * @author Mario Palencia
 * @author Luis Gabriel Romero
 */
public class Controlador {

    /**
     *
     * Instancia de la clase LogicaSecuenciador utilizada para realizar
 operaciones.
     *
     */
    LogicaSecuenciador interprete = new LogicaSecuenciador();

    /**
     * Procesa un archivo de entrada y genera un archivo de salida
     *
     * @param direccionArchivoEntrada Hace referencia a la direccion del archivo
     * de entrada
     * @param direccionArchivoSalida Hace referencia a la direccion del archivo
     * de salida
     */
    public void procesarArchivo(String direccionArchivoEntrada, String direccionArchivoSalida) {
        interprete.leerDocumento(direccionArchivoEntrada);
        interprete.procesarMatriz();
        interprete.generarArchivo(direccionArchivoSalida);
        
    }
}
