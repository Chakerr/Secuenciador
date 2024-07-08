package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import gfutria.Expresiones;
import java.util.ArrayList;

/**
 * Clase que representa la lógica del interprete.
 *
 * Se encarga de leer documentos, procesarlos y generar un archivo con su
 * resultado, implementando funciones especificas para analizar las estructuras
 * de control.
 *
 * @author Luis Gabriel Romero
 * @author mario Palencia
 *
 */
public class LogicaSecuenciador {

    /**
     * Matriz que almacena las líneas del documento y su informacion asociada.
     */
    public String pregunta[][];

    /**
     * Contador de lineas del documento.
     */
    public int contadorLineas;

    /**
     * Stack para realizar seguimiento de las lineas de codigo en bloques de
     * control.
     */
    public Stack<Integer> stack = new Stack<>();

    /**
     * Stack para realizar seguimiento de los bloques de control
     */
    public Stack<String> stackBloque = new Stack<>();
    Stack<String> partesForStack = new Stack<>();

    /**
     * Constructor de la clase LogicaInterprete, inicializa el contador de
     * Lineas
     */
    public LogicaSecuenciador() {
        contadorLineas = 0;
    }

    /**
     * Lee un documento y carga su contenido dentro de la matriz de preguntas.
     *
     * @param Direccion Es la direccion del archivo a leer.
     */
    public void leerDocumento(String Direccion) {
        try {
            File doc = new File(Direccion);
            BufferedReader obj = new BufferedReader(new FileReader(doc));
            String linea;
            while ((linea = obj.readLine()) != null) {
                if (!linea.isEmpty()) {
                    contadorLineas++;
                }
            }
            obj.close();

            pregunta = new String[contadorLineas][3]; // Inicializar pregunta con el tamaño adecuado
            obj = new BufferedReader(new FileReader(doc));
            contadorLineas = 0;
            while ((linea = obj.readLine()) != null) {

                linea = linea.replaceFirst("^\\s+", "");

                if (linea != null && !linea.isEmpty()) {
                    pregunta[contadorLineas][0] = Integer.toString(contadorLineas + 1); // Enumeración
                    pregunta[contadorLineas][1] = linea; // Expresión
                    pregunta[contadorLineas][2] = "_"; // Saltos
                    contadorLineas++;
                }
            }
            obj.close();
            pregunta = Arrays.copyOf(pregunta, contadorLineas + 1);
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo.");
        }
    }

    /**
     * Genera un archivo en la direccion especificada con el contenido actual de
     * la matriz
     *
     * @param direccion Es la direccion del archivo a generar.
     */
    public void generarArchivo(String direccion) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(direccion))) {
                for (String[] fila : pregunta) {
                    if (fila != null) {
                        //Cambio de fijo a postfijo:
                        ArrayList<String> posfijo = Expresiones.infAPosf(fila[1]);
                        writer.write(fila[0] + "\t" + posfijo + "\t" + fila[2] + "\t");
                        writer.newLine();
                    }
                }
            }
            System.out.println("Archivo generado con éxito.");
        } catch (IOException e) {
            System.err.println("Error al generar el archivo.");
        }
    }

    /**
     * Procesa la matriz de preguntas para identificar y analizar las
     * estructuras de control
     */
    public void procesarMatriz() {
        int contadorLineasOrdenadas = 0;
        String[] partesFor = new String[3];
        for (int i = 0; i < pregunta.length; i++) {
            String[] linea = pregunta[i];

            if (linea != null && linea.length > 1 && linea[1] != null) {
                if (linea[1].startsWith("if")) {
                    stackBloque.push(linea[1]);
                    stack.push(contadorLineasOrdenadas + 1);
                    String condicion = linea[1].substring(linea[1].indexOf("(") + 1, linea[1].lastIndexOf(")")); // Obtener la condición entre paréntesis
                    condicion = condicion.replaceAll("[()]", ""); // Eliminar todos los paréntesis
                    pregunta[contadorLineasOrdenadas][1] = condicion.trim(); // Eliminar los espacios en blanco al principio y al final
                    pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                    contadorLineasOrdenadas++;

                } else if (linea[1].trim().startsWith("else")) {
                    stack.push(contadorLineasOrdenadas + 1);
                    stackBloque.push(linea[1]);
                    pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                    pregunta[contadorLineasOrdenadas][1] = "JUMP";
                    pregunta[contadorLineasOrdenadas][2] = "_";
                    contadorLineasOrdenadas++;

                } else if (linea[1].startsWith("for")) {
                    stackBloque.push(linea[1]);
                    String condicion = linea[1].substring(linea[1].indexOf("(") + 1, linea[1].indexOf(")"));
                    partesFor = condicion.split(";");
                    for (int j = partesFor.length - 1; j >= 0; j--) {
                        partesForStack.push(partesFor[j].trim());
                    }
                    pregunta[contadorLineasOrdenadas][1] = partesForStack.pop();
                    pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                    contadorLineasOrdenadas++;

                } else if (linea[1].startsWith("while")) {
                    stackBloque.push(linea[1]);
                    stack.push(contadorLineasOrdenadas + 1);
                    String condicion = linea[1].substring(linea[1].indexOf("(") + 1, linea[1].indexOf(")"));
                    pregunta[contadorLineasOrdenadas][1] = condicion;
                    pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                    contadorLineasOrdenadas++;

                } else if (linea[1].startsWith("{")) {

                    if (!stackBloque.isEmpty() && stackBloque.peek().startsWith("for")) {
                        stack.push(contadorLineasOrdenadas + 1);
                        pregunta[contadorLineasOrdenadas][1] = partesForStack.pop();
                        pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                        contadorLineasOrdenadas++;
                    }
                } else if (linea[1].startsWith("}")) {
                    if (!stackBloque.isEmpty() && stackBloque.peek().startsWith("if")) {
                        if (pregunta[i + 1] != null && pregunta[i + 1][1] != null && pregunta[i + 1][1].trim().startsWith("else")) {
                            int salto = stack.pop();
                            pregunta[salto - 1][2] = Integer.toString(contadorLineasOrdenadas + 2);
                            stackBloque.pop();
                        } else {
                            int salto = stack.pop();
                            stackBloque.pop();
                            pregunta[salto - 1][2] = Integer.toString(contadorLineasOrdenadas + 1);
                        }
                    } else if (!stackBloque.isEmpty() && stackBloque.peek().startsWith("else")) {
                        int salto = stack.pop();
                        stackBloque.pop();
                        pregunta[salto - 1][2] = Integer.toString(contadorLineasOrdenadas + 1);

                    } else if (!stackBloque.isEmpty() && stackBloque.peek().startsWith("for")) {
                        int finalFor = stack.pop();
                        stackBloque.pop();
                        pregunta[finalFor - 1][2] = Integer.toString(contadorLineasOrdenadas + 2);
                        pregunta[contadorLineasOrdenadas][1] = partesForStack.pop();
                        pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                        pregunta[contadorLineasOrdenadas][2] = Integer.toString(finalFor);
                        contadorLineasOrdenadas++;

                    } else if (!stackBloque.isEmpty() && stackBloque.peek().startsWith("while")) {
                        stackBloque.pop();
                        int finalWhile = stack.pop();
                        pregunta[finalWhile - 1][2] = Integer.toString(contadorLineasOrdenadas + 2);
                        pregunta[contadorLineasOrdenadas][1] = "JUMP";
                        pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                        pregunta[contadorLineasOrdenadas][2] = Integer.toString(finalWhile);
                        contadorLineasOrdenadas++;
                    }

                } else {
                    String expresion = linea[1].trim();
                    if (expresion.endsWith(";")) {
                        expresion = expresion.substring(0, expresion.length() - 1);
                    }
                    pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
                    pregunta[contadorLineasOrdenadas][1] = expresion;
                    pregunta[contadorLineasOrdenadas][2] = "_";
                    contadorLineasOrdenadas++;
                }
            }
        }

        pregunta = Arrays.copyOf(pregunta, contadorLineasOrdenadas + 1);
        pregunta[contadorLineasOrdenadas] = new String[3]; // Asegurar que la posición de la matriz esté inicializada
        pregunta[contadorLineasOrdenadas][0] = Integer.toString(contadorLineasOrdenadas + 1);
        pregunta[contadorLineasOrdenadas][1] = "END";
        pregunta[contadorLineasOrdenadas][2] = "_";
        contadorLineasOrdenadas++;

        System.out.println("Contenido del stack:");
        for (Integer elemento : stack) {
            System.out.println(elemento);
        }
        System.out.println("Contenido del stackBloque:");
        for (String elemento : stackBloque) {
            System.out.println(elemento);
        }
    }

}
