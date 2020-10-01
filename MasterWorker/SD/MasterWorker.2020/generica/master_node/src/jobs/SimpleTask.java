// Tarea muy simple (es ridículo ejecutarla de forma remota)

package jobs;

import java.io.*;
import interfaces.*;

public class SimpleTask implements Task<Double, Double>, Serializable {
    public static final long serialVersionUID = 1234567895;
    public Double execute(Double x) {
	    return x * x;
    }
}
