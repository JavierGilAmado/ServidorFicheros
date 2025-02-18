package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {

    public static void main(String[] args) {
        int puerto = 12345; // Puerto para escuchar conexiones

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor en espera de conexiones...");

            // Acepta múltiples clientes
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());

                // Creamos un hilo para manejar la conexión del cliente
                new HiloServidor(clienteSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}



