// Tarea que calcula el valor aproximado del número pi

package jobs;

import java.io.*;
import interfaces.*;

public class PiTask implements Task, Serializable {
    public static final long serialVersionUID = 1234567894;
    int nIter;

    public PiTask (Integer nIter) {
        this.nIter = nIter;
    }
    public String execute(String s)  {
	    int nHits = 0;
	    for (int i=0; i<nIter; i++) {
		double x = 2 * Math.random();
		double y = 2 * Math.random();
		if (((x-1)*(x-1) + (y-1)*(y-1)) <=1)
	            nHits++;
            }
	    Double res = 4 * (double)nHits/nIter;
	    return res.toString();
    }
}
