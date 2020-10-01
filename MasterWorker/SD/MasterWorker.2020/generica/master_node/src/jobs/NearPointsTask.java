// Tarea que calcula los puntos de un repositorio que son vecinos de uno dado

package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class NearPointsTask implements Task<Point3D, Vector<Point3D>>, Serializable {
    public static final long serialVersionUID = 1234567895;
    String fichero;
    double distancia;

    public NearPointsTask (String f, double d) {
        distancia = d;
        fichero = f;
    }
    boolean vecino(Point3D p1, Point3D p2){
        return (((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y)+
               (p2.z-p1.z)*(p2.z-p1.z)) <= (distancia * distancia));
    }

    public Vector<Point3D> execute(Point3D p) throws Exception  {
        Vector<Point3D> res = new Vector<Point3D>();
        try {
            DataInputStream f = new DataInputStream(new FileInputStream(fichero));
            Point3D q;
            while ((q = new Point3D(f)) != null)
                if (vecino(p,q)) res.add(q);
            f.close();
        }
        catch (EOFException e) {}
	return res;
    }
}
       

