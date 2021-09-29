/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicafinal1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jcmju
 */
public class NodoN4 {

    public static void main(String[] args) throws IOException {

        // Puerto del nodo
        ServerSocket SocketNodo1;
        int puerto = 5000;
        SocketNodo1 = new ServerSocket(puerto);

        Thread hilo;

        // Socket para conexiones al nodo
        Socket previousNode;

        while (true) {
            System.out.println("Waiting ...");
            previousNode = SocketNodo1.accept();
            
            hilo = new Thread(new NodoN4.handlerNode(previousNode));
            hilo.start();
        }
    }

    public static class handlerNode implements Runnable {

        DataInputStream entrada; // Todo lo que entra al nodo se maneja con esta variable     
        DataOutputStream salida; // Lo que sale del nodo (no necesariamente hacia el central)

        DataOutputStream salidaNodoCentral; // Lo que se manda al nodo central
        DataOutputStream salidaNodo1; // Lo que se manda al nodo N1
        DataOutputStream salidaNodo2; // Lo que se manda al nodo N2
        DataOutputStream salidaNodo3; // Lo que se manda al nodo N3
       
        Socket socket = null;

        public handlerNode(Socket s) {
            socket = s;
            try {
                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(NodoN4.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                String cadena;
                String cliente;
                Random rd = new Random();

                Socket SocketNodoCentral;
                Socket SocketNodo1;
                Socket SocketNodo2;
                Socket SocketNodo3;
                String[] nodos = {"3.238.217.180", "3.92.8.167", "34.231.229.33", "44.193.39.105","3.232.96.218"};                
                int puerto = 5000;

                // Socket de los nodos
                SocketNodoCentral = new Socket(nodos[4], puerto);
                salidaNodoCentral = new DataOutputStream(SocketNodoCentral.getOutputStream());                
                
                SocketNodo1 = new Socket(nodos[0], puerto);
                salidaNodo1 = new DataOutputStream(SocketNodo1.getOutputStream());

                SocketNodo2 = new Socket(nodos[1], puerto);
                salidaNodo2 = new DataOutputStream(SocketNodo2.getOutputStream());

                SocketNodo3 = new Socket(nodos[2], puerto);
                salidaNodo3 = new DataOutputStream(SocketNodo3.getOutputStream());

                while (true) {

                    StringBuilder sb = new StringBuilder(40);
                    cadena = entrada.readUTF();
                    System.out.println("Mensaje recibido: " + cadena);
                    int cl = cadena.length();

                    // Objeto de la clase FileMethods
                    FileMethods archivo = new FileMethods();
                    archivo.setFileName("./clientesDB.txt");

                    // Array de sockets para calcular el siguiente nodo
                    Socket[] socketNodos = {SocketNodo1, SocketNodo2, SocketNodo3};
                    DataOutputStream[] salidaNodos = {salidaNodo1, salidaNodo2, salidaNodo3};

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
                                salidaNodoCentral.writeUTF(dataNodo);
                            }

                            // Se calcula el siguiente nodo para revisar
                            index = rd.nextInt(2) + 1;
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
                Logger.getLogger(NodoN4.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}