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
public class SecondaryNodes {

    public static void main(String[] args) throws IOException {

        // Puerto del nodo
        ServerSocket servidor;
        int puerto = 5000;
        servidor = new ServerSocket(puerto);

        Thread hilo;

        // Socket del nodo central
        Socket central;

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while (true) {
            System.out.println("Waiting ...");
            central = servidor.accept();

            // Para manejar posibles múltiples conexiones al nodo
            hilo = new Thread(new CentralNode.handlerClient(central));
            hilo.start();
        }
    }

    public static class handlerClient implements Runnable {

        DataInputStream entrada; // Lo que viene desde el nodo central
        DataOutputStream salida; // Lo que se manda al nodo central

        DataInputStream entradaNodo1;
        DataOutputStream salidaNodo1;

        DataInputStream entradaNodo2;
        DataOutputStream salidaNodo2;

        DataInputStream entradaNodo3;
        DataOutputStream salidaNodo3;

        DataInputStream entradaNodo4;
        DataOutputStream salidaNodo4;

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

                    int index;
                    Socket nextNode;
                    DataOutputStream outNextNode;

                    switch (cl) {
                        // Search request desde el nodo central
                        case 2:
                            System.out.println("case 2");
                            archivo.setFileName("./tramasDB.txt");
                            String dataNodo = archivo.readFile(entrada.readUTF(), false);

                            if (!dataNodo.equals("Not found")) {
                                // Sí hay coincidencia con el ID de referencia
                                salida.writeUTF(dataNodo);
                            }

                            // Se calcula el siguiente nodo para revisar
                            index = rd.nextInt(3) + 1;
                            nextNode = socketNodos[index];
                            outNextNode = salidaNodos[index];

                            // Envía el ID al siguiente nodo
                            outNextNode.writeUTF(entrada.readUTF());     
                            
                            break;

                        // Save request desde el nodo central
                        case 15:
                            System.out.println("case 15");
                            archivo.setFileName("./tramasDB.txt");
                            archivo.setMensaje(entrada.readUTF());
                            archivo.writeFile();

                            break;
                    }
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(CentralNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
