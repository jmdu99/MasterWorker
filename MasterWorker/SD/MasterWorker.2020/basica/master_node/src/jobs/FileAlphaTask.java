// Tarea que obtiene cuántos caracteres alfabéticos hay en un fichero

package jobs;

import java.io.*;
import interfaces.*;

public class FileAlphaTask implements Task, Serializable {
    public static final long serialVersionUID = 1234567893;
    String directory;

    public FileAlphaTask(String dir) {
        directory = dir;
    }
    public String execute(String fn) throws Exception {
        Integer count=0;
        FileInputStream f = new FileInputStream(directory + fn);
        int car;
        while ((car = f.read()) != -1)
             if (Character.isAlphabetic(car)) count++;
        f.close();
        return count.toString();
    }
}
       

