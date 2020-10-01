// Clase que inicia los servicios del Manager
// NO MODIFICAR

package manager;
import java.rmi.*;
import java.rmi.server.*;

import interfaces.*;

public class ManagerSrv {

    static public void main (String args[])  {
        if (args.length!=1) {
            System.err.println("Uso: numPuertoRegistro");
            return;
        }
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try {
            Manager srv = new ManagerImpl();
            Naming.rebind("rmi://localhost:" + args[0] + "/MW_SRV", srv);
        }
        catch (RemoteException e) {
            System.err.println("Error de comunicacion: " + e.toString());
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println("Excepcion en Servidor:" + e.toString());
            System.exit(1);
        }
    }
}
