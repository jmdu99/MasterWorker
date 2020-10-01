// Clase que implementa un punto en un espacio tridimensional.
// Usada por el trabajo NearPointsJob

package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class Point3D implements Serializable {
    public static final long serialVersionUID = 1234567896;
    public double x, y, z;

    public Point3D (double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }
    public Point3D (DataInputStream o) throws IOException {
        x=o.readDouble(); y=o.readDouble(); z=o.readDouble();
    }
    public void write(DataOutputStream o) throws IOException {
        o.writeDouble(x); o.writeDouble(y); o.writeDouble(z);
    }
@Override
    public String toString() {
        return new String("[" + x + "," + y + "," + z + "]");
    }
@Override
    public boolean equals(Object o) {
        if (o instanceof Point3D) {
            Point3D p = (Point3D) o;
            return ((x == p.x) && (y == p.y) && (z == p.z));
        }
        else return false;
    }
@Override
    public int hashCode()
    {
        return ((Double) x).hashCode() + ((Double) y).hashCode() + ((Double) z).hashCode();
    }

}
       

