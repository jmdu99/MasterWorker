// Clase abstracta que define un trabajo

package jobs;
import java.io.*;

import interfaces.*;

public abstract class Job<T,S> {
    Task <T, S> t = null;

    Job(Task<T,S> t) {
        this.t = t;
    }

    public boolean initJob(int nWorkers) {
        return true;
    }

    public Task<T,S> getTask() {
        return t;
    }

    public abstract T getNextInput() throws EOFException;

    public abstract void putResult(T input, S result, Exception e);

    public void endJob() {
    }
}
       

