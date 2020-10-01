// Trabajo que calcula el valor aproximado del número pi

package jobs;
import java.io.*;
import interfaces.*;

public class PiJob extends Job<Object, Double> {
    int inputDataSize=0;
    static final int iter=5000000;
    int count=0;
    double sum=0;

    public PiJob() {
        super(new PiTask(iter));
    }
@Override
    public boolean initJob(int nWorkers) {
        inputDataSize = nWorkers;  // una tarea por trabajador
        return true;
    }
@Override
    public Object getNextInput() throws EOFException {
        if (inputDataSize-- > 0) return null; // la tarea no tiene datos de entrada
        throw new EOFException();
    }
@Override
    public void putResult(Object input, Double result, Exception e) {
        if (e==null) {
            count++;
            sum+=result;
            System.out.println("Resultado parcial = " + result);
        }
        else 
            System.err.println(input + ": recibida excepción imprevista " + e.toString());
    }
@Override
    public void endJob() {
        System.out.println("Resultado final = " + sum/count);
    }
}
