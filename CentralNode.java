/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicafinal1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jcmju
 */
public class CentralNode {

    public static void main(String[] args) throws IOException {

        // Puerto del nodo
        ServerSocket servidor;
        int puerto = 5000;
        servidor = new ServerSocket(puerto);

        Thread hilo;

        // Socket del cliente
        Socket cliente;

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while (true) {
            System.out.println("Waiting ...");
            cliente = servidor.accept();

            // Para manejar posibles múltiples conexiones al nodo
            hilo = new Thread(new handlerClient(cliente));
            hilo.start();
        }
    }

    public static class handlerClient implements Runnable {

        DataInputStream entrada; // Lo que viene desde el cliente
        DataOutputStream salida; // Lo que se manda al cliente

        DataInputStream entradaNodo1; // Lo que viene desde el nodo N1
        DataOutputStream salidaNodo1; // Lo que se manda al nodo N1

        DataInputStream entradaNodo2; // Lo que viene desde el nodo N2
        DataOutputStream salidaNodo2; // Lo que se manda al nodo N2

        DataInputStream entradaNodo3; // Lo que viene desde el nodo N3
        DataOutputStream salidaNodo3; // Lo que se manda al nodo N3

        DataInputStream entradaNodo4; // Lo que viene desde el nodo N4
        DataOutputStream salidaNodo4; // Lo que se manda al nodo N4

        // Socket del cliente
        Socket socket = null;

        public handlerClient(Socket s) {
            socket = s;

            try {
                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(CentralNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                String cadena;
                String cliente;
                Random rd = new Random();

                Socket clienteNodo1;
                Socket clienteNodo2;
                Socket clienteNodo3;
                Socket clienteNodo4;
                String[] nodos = {"5001", "5002", "5003", "5004"};
                int puerto = 5000;

                // Socket de los demás nodos
                clienteNodo1 = new Socket(nodos[0], puerto);
                entradaNodo1 = new DataInputStream(clienteNodo1.getInputStream());
                salidaNodo1 = new DataOutputStream(clienteNodo1.getOutputStream());

                clienteNodo2 = new Socket(nodos[1], puerto);
                entradaNodo2 = new DataInputStream(clienteNodo2.getInputStream());
                salidaNodo2 = new DataOutputStream(clienteNodo2.getOutputStream());

                clienteNodo3 = new Socket(nodos[2], puerto);
                entradaNodo3 = new DataInputStream(clienteNodo3.getInputStream());
                salidaNodo3 = new DataOutputStream(clienteNodo3.getOutputStream());

                clienteNodo4 = new Socket(nodos[3], puerto);
                entradaNodo4 = new DataInputStream(clienteNodo4.getInputStream());
                salidaNodo4 = new DataOutputStream(clienteNodo4.getOutputStream());

                while (true) {

                    StringBuilder sb = new StringBuilder(40);
                    cadena = entrada.readUTF();
                    System.out.println("Mensaje recibido: " + cadena);
                    int cl = cadena.length();

                    // Objeto de la clase FileMethods
                    FileMethods archivo = new FileMethods();
                    archivo.setFileName("./clientesDB.txt");

                    // Array de sockets para calcular el siguiente nodo
                    Socket[] socketNodos = {clienteNodo1, clienteNodo2, clienteNodo3, clienteNodo4};
                    DataOutputStream[] salidaNodos = {salidaNodo1, salidaNodo2, salidaNodo3, salidaNodo4};

                    // Calcula el siguente nodo para enviarle las partes del mensaje
                    int index = rd.nextInt(3) + 1;
                    Socket nextNode = socketNodos[index];
                    DataOutputStream outNextNode = salidaNodos[index];
                    System.out.println("Siguiente nodo " + nextNode.getInetAddress());

                    switch (cl) {
                        // Save request desde el cliente
                        case 36:

                            // Crear identificador de cliente
                            String cuenta = cadena.substring(0, 11);
                            int index1 = rd.nextInt(10) + 1;
                            int index2 = rd.nextInt(10) + 1;
                            String id = cadena.substring(index1 - 1, index1)
                                    + cadena.substring(index2 - 1, index2);
                            System.out.println("id " + id);

                            // Revisa si ya existe un cliente con ese id
                            cliente = archivo.readFile(cuenta, true);
                            System.out.println("Cliente " + cliente);

                            if (cliente.equals("Not found")) {
                                System.out.println("ajá y entonce?");
                                // No existe un cliente con ese id
                                // Agrega el nuevo cliente
                                archivo.setMensaje(id + ";" + cuenta);
                                archivo.writeFile();
                            }

                            // Divide el mensaje y añade el identificador de 
                            // cliente y orden
                            String c1 = id + "1" + cadena.substring(0, 12);
                            String c2 = id + "2" + cadena.substring(12, 24);
                            String c3 = id + "3" + cadena.substring(24, 36);
                            String[] tramas = {c1, c2, c3};

                            // Envía cada parte del mensaje al siguiente nodo
                            for (int i = 0; i < 3; i++) {
                                outNextNode.writeUTF(tramas[i]);
                                // Calcula el siguente nodo
                                index = rd.nextInt(3) + 1;
                                nextNode = socketNodos[index];
                                outNextNode = salidaNodos[index];
                            }

                            // Responde al cliente
                            salida.writeUTF("Guardado");

                            break;

                        // Search request desde el cliente
                        case 11:
                            System.out.println("case 11");

                            // Revisa si ya existe la cuenta
                            cliente = archivo.readFile(entrada.readUTF(), true);
                            System.out.println("Cliente/Cuenta " + cliente);

                            if (cliente.equals("Not found")) {
                                System.out.println("ajá y entonce?");
                                // No existe la cuenta
                                salida.writeUTF(cliente);
                            }

                            // Sí existe la cuenta -> se obtiene el ID
                            id = cliente.substring(0, 2);

                            // Calcula el siguente nodo
                            index = rd.nextInt(3) + 1;
                            nextNode = socketNodos[index];
                            outNextNode = salidaNodos[index];

                            // Envía el ID al siguiente nodo
                            outNextNode.writeUTF(id);

                            break;

                        // Response from 
                        case 15:
                            System.out.println("case 15");

                            // Revisa todos los sockets para obtener el mensaje
                            String[] dataNodos = {entradaNodo1.readUTF(),
                                entradaNodo2.readUTF(),
                                entradaNodo3.readUTF(),
                                entradaNodo4.readUTF()};

                            /////////// Terminar
                            break;
                    }

                    // Termina la conexión con el cliente
                    socket.close();

                }
            } catch (IOException ex) {
                Logger.getLogger(CentralNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
