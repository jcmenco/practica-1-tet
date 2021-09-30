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
public class NodoN1 {

    public static void main(String[] args) throws IOException {

        DataInputStream entradaNodoN1; // Lo que entra al nodo N1
        DataOutputStream salidaNodoN1; // Lo que sale del nodo N1

        // Puerto del nodo
        ServerSocket SocketNodo1;
        int puerto = 5000;
        SocketNodo1 = new ServerSocket(puerto);

        // Socket para conexiones al nodo
        Socket previousNode;

        while (true) {
            System.out.println("Waiting ...");
            previousNode = SocketNodo1.accept();

            try {
                entradaNodoN1 = new DataInputStream(previousNode.getInputStream());
                salidaNodoN1 = new DataOutputStream(previousNode.getOutputStream());

                String cadena;

                cadena = entradaNodoN1.readUTF();
                System.out.println("Mensaje recibido: " + cadena);
                int cl = cadena.length();

                // Objeto de la clase FileMethods
                FileMethods archivo = new FileMethods();                

                switch (cl) {
                    // Search request desde el nodo central
                    case 2:
                        System.out.println("case 2");
                        archivo.setFileName("./tramasDB.txt");
                        String dataNodo = archivo.readFile(entradaNodoN1.readUTF(), false);

                        if (!dataNodo.equals("Not found")) {
                            // Sí hay coincidencia con el ID de referencia
                            // Envía la data al nodo anterior
                            salidaNodoN1.writeUTF(dataNodo);
                        }

                        //////////// TERMINAR

                        break;

                    // Save request desde el nodo central
                    case 15:
                        System.out.println("case 15");
                        System.out.println(entradaNodoN1.readUTF()+" esto es lo que llega");
                        archivo.setFileName("./tramasDB.txt");
                        archivo.setMensaje(entradaNodoN1.readUTF());
                        archivo.writeFile();
                        
                        // Envía ACK al nodo central
                        salidaNodoN1.writeUTF("OK");
                        
                        // Cierra conexión con el nodo central
                        previousNode.close();

                        break;
                }

            } catch (IOException ex) {
                Logger.getLogger(NodoN1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
