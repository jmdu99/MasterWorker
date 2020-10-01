// Tarea que obtiene cuántos caracteres alfabéticos hay en un fichero

package jobs;

import java.io.*;
import interfaces.*;

public class FileAlphaTask implements Task<String, Integer>, Serializable {
    public static final long serialVersionUID = 1234567893;
    String directory;

    public FileAlphaTask(String dir) {
        directory = dir;
    }
    public Integer execute(String fn) throws Exception {
        int count=0;
        FileInputStream f = new FileInputStream(directory + fn);
        int car;
        while ((car = f.read()) != -1)
             if (Character.isAlphabetic(car)) count++;
        f.close();
        return count;
    }
}
       

