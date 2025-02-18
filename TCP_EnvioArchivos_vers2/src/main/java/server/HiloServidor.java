package server;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import message.*;

public class HiloServidor extends Thread {
    private Socket clienteSocket;

    public HiloServidor(Socket socket) {
        this.clienteSocket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(clienteSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clienteSocket.getOutputStream())) {

            Object mensaje;
            while ((mensaje = ois.readObject()) != null) {
                if (mensaje instanceof MensajeDameFichero) {
                    String nombreFichero = ((MensajeDameFichero) mensaje).nombreFichero;
                    System.out.println("Cliente solicita archivo: " + nombreFichero);
                    enviarFichero(nombreFichero, oos);
                } else if (mensaje instanceof MensajeSubirFichero) {
                    MensajeSubirFichero archivo = (MensajeSubirFichero) mensaje;
                    System.out.println("Cliente desea subir archivo: " + archivo.nombreFichero);
                    recibirFichero(archivo);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión con el cliente: " + e.getMessage());
            }
        }
    }

    // Enviar un archivo al cliente
    private void enviarFichero(String nombreFichero, ObjectOutputStream oos) throws IOException {
        File file = new File("servidor/" + nombreFichero); // Directorio en el servidor
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesLeidos;
                while ((bytesLeidos = fis.read(buffer)) != -1) {
                    MensajeTomaFichero mensaje = new MensajeTomaFichero();
                    mensaje.nombreFichero = nombreFichero;
                    mensaje.contenidoFichero = Arrays.copyOf(buffer, bytesLeidos);  // Guardamos solo los bytes leídos
                    mensaje.bytesValidos = bytesLeidos;
                    mensaje.ultimoMensaje = (bytesLeidos < buffer.length);  // Si es menor que el buffer, es el último
                    oos.writeObject(mensaje);
                }
                System.out.println("Archivo enviado: " + nombreFichero);
            }
        } else {
            System.out.println("Archivo no encontrado en el servidor.");
        }
    }

    // Recibir un archivo desde el cliente
    private void recibirFichero(MensajeSubirFichero archivo) throws IOException {
        File file = new File("servidor/" + archivo.nombreFichero);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(archivo.contenidoFichero);
        }
        System.out.println("Archivo recibido y guardado en el servidor: " + archivo.nombreFichero);
    }
}