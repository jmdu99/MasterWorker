// Clase que inicia los servicios del worker.
// NO SE DEBE MODIFICAR.

package worker;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

import interfaces.*;
class WorkerSrv {

    static public void main (String args[])  {
        if (args.length!=2) {
            System.err.println("Uso: hostRegistro numPuertoRegistro");
            return;
        }
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try {
            Manager manager = (Manager) Naming.lookup("//" + args[0] + ":" + args[1] + "/MW_SRV");
            WorkerImpl w = new WorkerImpl(manager);
            System.out.print("Pulse enter para terminar el worker: ");
	    Scanner s = new Scanner(System.in);
            if (s.hasNextLine()){
                w.stopWorker();
                System.exit(1);
            }
        }
        catch (Exception e) {
            System.err.println("Servicio MasterWorker no localizado " + e.toString());
            System.exit(1);
        }
    }
}
