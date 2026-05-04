package biblioteca;

import biblioteca.controlador.Controlador;
import biblioteca.modelo.Modelo;
import biblioteca.vista.Vista;

/*
  AppBiblioteca:
  Punto de entrada del programa.
  Crea Modelo, Vista y Controlador y arranca la aplicación.
  Se captura cualquier error no controlado para que el programa no termine abruptamente.
*/
public class AppBiblioteca {

    public static void main(String[] args) {
        // Creamos Vista y Modelo y los conectamos con el Controlador.
        // Si ocurre un error inesperado, lo capturamos para evitar que el programa termine abruptamente.
        try {
            Vista vista = new Vista();
            Modelo modelo = new Modelo();
            Controlador controlador = new Controlador(modelo, vista);
            controlador.comenzar();
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
}
