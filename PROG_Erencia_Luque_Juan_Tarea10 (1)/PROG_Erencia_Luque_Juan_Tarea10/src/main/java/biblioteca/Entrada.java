package biblioteca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Clase que nos permite leer datos por teclado asegurandose
 * que el tipo de dato introducido es compatible con el esperado.
 * 
 * @author pepino
 */
public class Entrada {
	
	/*
	 * Metodo estatico que lee una cadena por teclado
	 * @return la cadena leida
	 */
	public static String cadena() {
            // Lee una línea completa (String) desde teclado.
            String valor = "";
            InputStreamReader flujo = new InputStreamReader(System.in);
            BufferedReader teclado = new BufferedReader(flujo);
            try {
                    valor = teclado.readLine();
            } catch (IOException e) {
                    System.out.print("Error de Entrada/Salida. Intentalo de nuevo: ");
            }
            return valor;
	}

	/*
	 * Metodo estatico que lee un entero por teclado y se asegura
	 * que el valor introducido es compatible con un entero
	 * @return el entero leido
	 */
	public static int entero() {
            // Lee un entero; repite hasta que el usuario introduzca un valor valido.
            int valor = 0;
            boolean leido = false;
            do {
                    try {
                            valor = Integer.parseInt(cadena());
                            leido = true;
                    } catch (NumberFormatException e) {
                            System.out.print("ERROR: Esperaba un entero. Intentalo de nuevo: ");
                    }
            } while (!leido);
            return valor;
	}
	
	/*
	 * Metodo estatico que lee un entero largo (long) por teclado y se asegura
	 * que el valor introducido es compatible con un entero largo
	 * @return el entero largo leido
	 */
	public static long enteroLargo() {
            // Lee un long; repite hasta que el usuario introduzca un valor valido.
            long valor = 0;
            boolean leido = false;
            do {
                    try {
                            valor = Long.parseLong(cadena());
                            leido = true;
                    } catch (NumberFormatException e) {
                            System.out.print("ERROR: Esperaba un entero largo. Intentalo de nuevo: ");
                    }
            } while (!leido);
            return valor;
	}
	
	/*
	 * Metodo estatico que lee un real (float) por teclado y se asegura
	 * que el valor introducido es compatible con un real
	 * @return el real leido
	 */
	public static float real() {
            // Lee un float; repite hasta que el usuario introduzca un valor valido.
            float valor = 0;
            boolean leido = false;
            do {
                    try {
                            valor = Float.parseFloat(cadena());
                            leido = true;
                    } catch (NumberFormatException e) {
                            System.out.print("ERROR: Esperaba un real. Intentalo de nuevo: ");
                    }
            } while (!leido);
            return valor;
	}
	
	/*
	 * Metodo estatico que lee un real de doble precision (double) 
	 * por teclado y se asegura que el valor introducido es compatible 
	 * con un real de doble precision
	 * @return el real de doble precision leido
	 */
	public static double realDoble() {
            // Lee un double; repite hasta que el usuario introduzca un valor valido.
            double valor = 0;
            boolean leido = false;
            do {
                    try {
                            valor = Double.parseDouble(cadena());
                            leido = true;
                    } catch (NumberFormatException e) {
                            System.out.print("ERROR: Esperaba un real de doble precision. Intentalo de nuevo: ");
                    }
            } while (!leido);
            return valor;
	}
	
	/*
	 * Metodo estatico que lee un caracter por teclado y se asegura
	 * que el valor introducido es compatible con un caracter
	 * @return el caracter leido
	 */
	public static char caracter() {
            // Lee un caracter; repite hasta que el usuario introduzca solo 1 caracter.
            String valor = "";
            boolean leido = false;
            do {
                    valor = cadena();
                    if (valor.length() == 1)
                            leido = true;
                    else 
                            System.out.print("ERROR: Esperaba un caracter. Intentalo de nuevo: ");
            } while (!leido);
            return valor.charAt(0);
	}

}
