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
        DataInputStream entradaNodoVecino = null; // Lo que responde el nodo N1
        DataOutputStream salidaNodoVecino = null; // Lo que se manda al nodo N1

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

                Socket SocketNodoVecino;
                String[] nodos = {"3.238.217.180", "3.92.8.167", "34.231.229.33", "44.193.39.105"};

                cadena = entradaCliente.readUTF();
                System.out.println("Mensaje recibido: " + cadena);
                int cl = cadena.length();

                // Objeto de la clase FileMethods
                FileMethods archivo = new FileMethods();
                archivo.setFileName("./clientesDB.txt");

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
                            System.out.println("Not found");
                            // No existe un cliente con ese id
                            // Agrega el nuevo cliente
                            archivo.setMensaje(id + ";" + cuenta);
                            archivo.writeFile();
                        } else {
                            id = cuentaCliente.substring(0, 2);
                        }

                        // Divide el mensaje y a??ade el identificador de 
                        // cliente y orden
                        String c1 = id + "1" + cadena.substring(0, 12);
                        String c2 = id + "2" + cadena.substring(12, 24);
                        String c3 = id + "3" + cadena.substring(24, 36);
                        String[] tramas = {c1, c2, c3};
                        int index;
                        
                        // Env??a cada parte del mensaje al siguiente nodo
                        for (int i = 0; i < 3; i++) {
                            // Calcula el nodo vecino aleatorio
                            index = rd.nextInt(4);
                            
                            // Socket del nodo central como cliente de un nodo vecino aleatorio
                            SocketNodoVecino = new Socket(nodos[index], puerto);
                            entradaNodoVecino = new DataInputStream(SocketNodoVecino.getInputStream());
                            salidaNodoVecino = new DataOutputStream(SocketNodoVecino.getOutputStream());

                            System.out.println(tramas[i] + " esto es lo que se supone que manda cada vez");
                            salidaNodoVecino.writeUTF(tramas[i]);
                        }

                        // Responde al cliente                        
                        String nodoN1SaveResponse = entradaNodoVecino.readUTF();
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
                            System.out.println("Not found");
                            // No existe la cuenta
                            salidaCliente.writeUTF(cuentaCliente);
                        }

                        // S?? existe la cuenta -> se obtiene el ID
                        id = cuentaCliente.substring(0, 2);

                        // Env??a el ID al siguiente nodo
                        salidaNodoVecino.writeUTF(id);

                        break;

                    // Response from 
                    case 15:
                        System.out.println("case 15");

                        /////////// Terminar
                        break;
                }

                // Termina la conexi??n con el cliente
                //socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CentralNode.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
