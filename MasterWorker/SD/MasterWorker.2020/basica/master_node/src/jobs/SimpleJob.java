// Trabajo muy simple (es ridículo ejecutarlo de forma remota)

package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class SimpleJob extends Job {
    int inputDataSize;
    Double [] inputData;
    int count=0;
    double total = 0;

    public SimpleJob() {
        super(new SimpleTask());
    }
@Override
    public boolean initJob(int nWorkers) {
        inputDataSize = ((int) (1000000 * Math.random()) % 100) + 1;
        inputData = new Double[inputDataSize];
        for (int i=0; i<inputData.length; i++)
            inputData[i] = Math.random();
        return true;
    }
@Override
    public String getNextInput() throws EOFException  {
        if (count<inputDataSize) return inputData[count++].toString();
        else throw new EOFException();
    }
@Override
    public void putResult(String input, String result, Exception e) {
        if (e==null) {
            System.out.println("cuadrado de " + input + " = " + result);
            total += Double.parseDouble(result);
        }
        else 
            System.err.println(input + ": recibida excepción imprevista " + e.toString());
    }
@Override
    public void endJob() {
        System.out.println("Total = " + total);
    }
}
