// Trabajo que muestra cómo los tipos de los datos de entrada y del
// resultado pueden ser complejos.
// Calcula por cada punto de entrada qué puntos del repositorio
// están a una distancia menor o igual que una cierta cantidad.
// Como resultado final, determina qué puntos del repositorio
// aparecen con más frecuencia como "vecinos" de los puntos de entrada.

package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class NearPointsJob extends Job<Point3D, Vector<Point3D>> {
    // fichero del nodo maestro que guarda los puntos a procesar
    String ficheroEntrada = "../Ficheros/puntos/puntos.txt";

    // fichero del nodo worker donde está almacenado el repositorio de puntos
    static final String ficheroRepositorio = "../Ficheros/puntos/puntos.bin";
    // fichero del nodo worker donde está almacenado el repositorio de puntos
    static final double distancia = 100;

    HashMap <Point3D, Integer> map = new HashMap <Point3D, Integer>();
    Scanner sc;

    public NearPointsJob() {
        super(new NearPointsTask(ficheroRepositorio, distancia));
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
    public Point3D getNextInput() throws EOFException  {
        Point3D p = null;
        while (p==null && sc.hasNextLine()) {
            Scanner sl = new Scanner(sc.nextLine());
            double x=0, y=0, z=0;
            if (sl.hasNextDouble()) {
                x=sl.nextDouble();
                if (sl.hasNextDouble()){
                    y=sl.nextDouble();
                    if (sl.hasNextDouble()) {
                        z=sl.nextDouble();
                        p = new Point3D(x,y,z);
                    }
                }
            }
        }
        if (p!=null) return p;
        else throw new EOFException();
    }
@Override
    public void putResult(Point3D input, Vector<Point3D> result, Exception e) {
        if (e==null) {
	    for (Point3D pt : result) {
                System.out.println(input + " vecino de " + pt);
                Integer count = map.get(pt);
                if (count == null)
                    map.put(pt, 1);
                else
                    map.put(pt, ++count);
            }
        }
       else {
            if (e instanceof FileNotFoundException)
                    System.err.println("fichero repositorio de puntos no accesible");
                else
                    System.err.println(input + ": recibida excepción imprevista " + e.toString());
        }
    }
@Override
    public void endJob() {
        if (map.isEmpty())
            System.out.println("ningún punto de entrada tiene puntos cercanos en el repositorio");
        else {
            int max = 0;
            for (Integer i: map.values()) {
                if (i>max) max = i;
	    }
            for (Point3D p: map.keySet()) {
                Integer cnt = map.get(p);
                if (cnt!=null && cnt==max)
                    System.out.println(p + " cercano a " + max + " puntos de entrada");
	    }
        }
    }
}
