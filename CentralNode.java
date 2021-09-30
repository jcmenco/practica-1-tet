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
public class CentralNode {

    public static void main(String[] args) throws IOException {

        DataInputStream entradaCliente; // Lo que entra al nodo central
        DataOutputStream salidaCliente; // Lo que se manda al cliente
        DataInputStream entradaNodo1; // Lo que responde el nodo N1
        DataOutputStream salidaNodo1; // Lo que se manda al nodo N1

        // Puerto del nodo
        ServerSocket SocketNodoCentral;
        int puerto = 5000;
        SocketNodoCentral = new ServerSocket(puerto);

        // Socket para conexiones con el cliente
        Socket socketCliente;

        while (true) {

            System.out.println("Waiting ...");
            socketCliente = SocketNodoCentral.accept();

            try {

                entradaCliente = new DataInputStream(socketCliente.getInputStream());
                salidaCliente = new DataOutputStream(socketCliente.getOutputStream());

                String cadena;
                String cuentaCliente;
                Random rd = new Random();

                Socket SocketNodo1;
                String[] nodos = {"3.238.217.180", "3.92.8.167", "34.231.229.33", "44.193.39.105"};

                // Socket del nodo central como cliente del nodo N1 (?)
                SocketNodo1 = new Socket(nodos[0], puerto);
                entradaNodo1 = new DataInputStream(SocketNodo1.getInputStream());
                salidaNodo1 = new DataOutputStream(SocketNodo1.getOutputStream());

                StringBuilder sb = new StringBuilder(40);
                cadena = entradaCliente.readUTF();
                System.out.println("Mensaje recibido: " + cadena);
                int cl = cadena.length();

                // Objeto de la clase FileMethods
                FileMethods archivo = new FileMethods();
                archivo.setFileName("./clientesDB.txt");

                DataOutputStream outNextNode = salidaNodo1;

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
                        cuentaCliente = archivo.readFile(cuenta, true);
                        System.out.println("Cliente " + cuentaCliente);

                        if (cuentaCliente.equals("Not found")) {
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
                        }

                        // Responde al cliente                        
                        String nodoN1SaveResponse = entradaNodo1.readUTF();                        
                        salidaCliente.writeUTF(nodoN1SaveResponse);
                        
                        socketCliente.close();

                        break;

                    // Search request desde el cliente
                    case 11:
                        System.out.println("case 11");

                        // Revisa si ya existe la cuenta
                        cuentaCliente = archivo.readFile(entradaCliente.readUTF(), true);
                        System.out.println("Cliente/Cuenta " + cuentaCliente);

                        if (cuentaCliente.equals("Not found")) {
                            System.out.println("ajá y entonce?");
                            // No existe la cuenta
                            salidaCliente.writeUTF(cuentaCliente);
                        }

                        // Sí existe la cuenta -> se obtiene el ID
                        id = cuentaCliente.substring(0, 2);

                        outNextNode = salidaNodo1;

                        // Envía el ID al siguiente nodo
                        outNextNode.writeUTF(id);

                        break;

                    // Response from 
                    case 15:
                        System.out.println("case 15");

                        /////////// Terminar
                        break;
                }

                // Termina la conexión con el cliente
                //socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CentralNode.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
