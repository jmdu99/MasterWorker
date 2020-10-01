// Trabajo que obtiene cuántos caracteres alfabéticos hay en los ficheros
// cuyos nombres aparecen en un fichero de entrada del nodo maestro.
// Se asume que esos ficheros están almacenados en un sistema de ficheros
// distribuido/paralelo y están accesibles para todos los trabajadores

package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class FileAlphaJob extends Job<String, Integer> {

    // fichero del nodo maestro que guarda la lista de ficheros a procesar
    String ficheroEntrada = "../Ficheros/alpha/ficheros.txt";

    // directorio donde están almacenados los ficheros a procesar
    // por los workers
    static final String directory = "../Ficheros/alpha/";

    int count = 0;
    int total = 0;
    Scanner sc;

    public FileAlphaJob() {
        super(new FileAlphaTask(directory));
    }
@Override
    public boolean initJob(int nWorkers) {
        boolean res = true;
        try {
            sc = new Scanner(new File(ficheroEntrada));
        }
        catch (Exception e) {
            System.err.println(ficheroEntrada + " no accesible");
            res = false;
        }
        return res;
    }
@Override
    public String getNextInput() throws EOFException {
        if (sc.hasNext()) return sc.next();
        throw new EOFException();
    }
@Override
    public void putResult(String input, Integer result, Exception e) {
        if (e==null) {
            System.out.println(input + ": " + result);
            total += result;
            count++;
	}
        else {
            if (e instanceof FileNotFoundException)
                    System.err.println(input + ": no accesible");
                else
                    System.err.println(input + ": recibida excepción imprevista " + e.toString());
        }
    }
@Override
    public void endJob() {
        System.out.println("Total = " + total);
        System.out.println("Media = " + (double)total/count);
    }
}
