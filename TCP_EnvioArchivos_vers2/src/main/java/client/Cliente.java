package client;

import java.io.*;
import java.net.*;
import message.*;

public class Cliente {

    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PUERTO);
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Conectado al servidor.");

            // Simulación de operación de descarga de archivo
            MensajeDameFichero mensaje = new MensajeDameFichero();
            mensaje.nombreFichero = "archivo.txt";
            oos.writeObject(mensaje);
            recibirFichero(ois);

            // Simulación de operación de subida de archivo
            File archivo = new File("archivo_subir.txt");
            if (archivo.exists()) {
                subirFichero(archivo, oos);
            }
        } catch (IOException e) {
            System.out.println("Error en la conexión con el servidor: " + e.getMessage());
        }
    }

    private static void recibirFichero(ObjectInputStream ois) throws IOException {
        try {
            Object mensaje;
            while ((mensaje = ois.readObject()) != null) {
                if (mensaje instanceof MensajeTomaFichero) {
                    MensajeTomaFichero ficheroRecibido = (MensajeTomaFichero) mensaje;
                    System.out.println("Recibido: " + ficheroRecibido.nombreFichero);
                    // Aquí deberías guardar los bytes recibidos en un archivo
                    FileOutputStream fos = new FileOutputStream(ficheroRecibido.nombreFichero);
                    fos.write(ficheroRecibido.contenidoFichero);
                    fos.close();
                    if (ficheroRecibido.ultimoMensaje) {
                        break;  // Salimos cuando recibimos el último bloque
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error al recibir el archivo: " + e.getMessage());
        }
    }

    private static void subirFichero(File archivo, ObjectOutputStream oos) throws IOException {
        MensajeSubirFichero mensajeSubir = new MensajeSubirFichero();
        mensajeSubir.nombreFichero = archivo.getName();
        byte[] contenido = new byte[(int) archivo.length()];
        try (FileInputStream fis = new FileInputStream(archivo)) {
            fis.read(contenido);
        }
        mensajeSubir.contenidoFichero = contenido;
        oos.writeObject(mensajeSubir);
    }
}

