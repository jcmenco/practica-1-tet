/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicafinal1;

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.util.Scanner;

/**
 *
 * @author jcmju
 */
public class FileMethods {

    // Atributos: nombre del archivo y texto para guardar
    private String fileName;
    private String mensaje;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void writeFile() throws IOException {
        String newline = System.getProperty("line.separator");
        // Convierte el string en un array de bytes
        String s = this.mensaje + newline;
        byte data[] = s.getBytes();
        Path p = Paths.get(this.fileName);

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public String readFile(String Reference, Boolean Cuenta) throws FileNotFoundException {
        File myObj = new File(this.fileName);
        Scanner myReader = new Scanner(myObj);
        StringBuffer sb = new StringBuffer(40);
        int k1;
        int k2;
        
        if (Cuenta) {
            k1 = 3;
            k2 = 14;
        } else {
            k1 = 0;
            k2 = 2;
        }
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String id = data.substring(k1, k2);
            System.out.println("method id " + id);
            if (id.equals(Reference)) {
                return data;
            }
        }
        myReader.close();
        return "Not found";
    }

}
