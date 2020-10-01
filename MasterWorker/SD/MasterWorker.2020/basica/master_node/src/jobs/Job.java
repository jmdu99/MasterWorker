// Clase abstracta que define un trabajo

package jobs;
import java.io.*;

import interfaces.*;

public abstract class Job {
    Task t = null;

    Job(Task t) {
        this.t = t;
    }

    public boolean initJob(int nWorkers) {
        return true;
    }

    public Task getTask() {
        return t;
    }

    public abstract String getNextInput() throws EOFException;

    public abstract void putResult(String input, String result, Exception e);

    public void endJob() {
    }
}
       

