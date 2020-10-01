// Tarea que calcula el valor aproximado del número pi

package jobs;

import java.io.*;
import interfaces.*;

public class PiTask implements Task<Object, Double>, Serializable {
    public static final long serialVersionUID = 1234567894;
    int nIter;

    public PiTask (Integer nIter) {
        this.nIter = nIter;
    }
    public Double execute(Object o)  {
	    int nHits = 0;
	    for (int i=0; i<nIter; i++) {
		double x = 2 * Math.random();
		double y = 2 * Math.random();
		if (((x-1)*(x-1) + (y-1)*(y-1)) <=1)
	            nHits++;
            }
	    return 4 * (double)nHits/nIter;
    }
}
