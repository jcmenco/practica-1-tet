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
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 *
 * @author jcmju
 */
public class ClientApplication {

    public static void main(String[] args) throws IOException {

        // Se escoge aleatoriamente un nodo de la red para el request        
        String host = "3.232.96.218";
        int[] nodos = {5000, 5001, 5002, 5003, 5004};
        Random rd = new Random();
        int index = rd.nextInt(4) + 1;
        int puerto = 5000; //nodos[index];
        System.out.println(puerto);

        DataInputStream entrada;
        DataOutputStream salida;
        Socket cliente;
        StringBuffer sb = new StringBuffer(40);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/ddHH:mm");
        LocalDateTime now = LocalDateTime.now();

        cliente = new Socket(host, puerto);
        entrada = new DataInputStream(cliente.getInputStream());
        salida = new DataOutputStream(cliente.getOutputStream());

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        String cuenta;
        String monto = "";
        String tipo = "";
        String fechaHora = "";

        System.out.println("Hola, ¿desea registrar una transacción (1) "
                + "o consultar sus transacciones (2)?");
        String request = br.readLine();

        if (request.equals("1")) {
            System.out.println("Número de cuenta (11 dígitos): ");
            cuenta = br.readLine();
            System.out.println("Tipo: ahorro(1) corriente(2)");
            tipo = br.readLine();
            System.out.println("Monto: ");
            monto = br.readLine();
            int ceros = 9 - monto.length();
            String zeros = "";
            for (int i = 0; i < ceros; i++) {
                zeros = zeros + "0";
            }
            monto = sb.append(zeros).append(monto).toString();
            fechaHora = dtf.format(now);
        } else {
            System.out.println("Número de cuenta (11 dígit os): ");
            cuenta = br.readLine();
        }

        String mensaje = cuenta + tipo + monto + fechaHora;

        // Envía el mensaje al servidor
        salida.writeUTF(mensaje);

        // Lee y muestra en consola la respuesta del servidor
        String respuesta = entrada.readUTF();
        System.out.println(respuesta);

        // Cierra la conexión       
        cliente.close();

//        while (true) {
//            // Lee y muestra en consola la respuesta del servidor
//            String respuesta = entrada.readUTF();
//            System.out.println(respuesta);
//
//            System.out.println("Hola, ¿desea registrar una transacción (1) "
//                    + "o consultar sus transacciones (2)?");
//            mensaje = br.readLine();
//            salida.writeUTF(mensaje);
//
//            if (mensaje.equalsIgnoreCase("close")) {
//                break;
//            }
//
//            System.out.println("Fin del envío");
//            cliente.close();
//        }
    }
}
