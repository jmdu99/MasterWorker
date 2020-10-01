// Tarea muy simple (es ridículo ejecutarla de forma remota)

package jobs;

import java.io.*;
import interfaces.*;

public class SimpleTask implements Task, Serializable {
    public static final long serialVersionUID = 1234567895;
    public String execute(String x) {
	    Double d = Double.parseDouble(x);
	    Double res = d * d;
	    return res.toString();
    }
}
