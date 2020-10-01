// Implementación de la interfaz de los servicios del Manager

package manager;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.*;

public class ManagerImpl extends UnicastRemoteObject implements Manager {
	public static final long  serialVersionUID=1234567890; 

	private static List<Worker> trabajadores=Collections.synchronizedList(new ArrayList<Worker>());

	public ManagerImpl () throws RemoteException  {
		super();
	}
	public synchronized void darDeAltaTrabajador(Worker w) throws RemoteException{
		trabajadores.add(w);
	}
	public synchronized void darDeBajaTrabajador(Worker w) throws RemoteException{
		trabajadores.remove(w);
	}
	public synchronized List<Worker> getTrabajadoresRequeridos(int nTrabajadores)throws RemoteException{
		List<Worker> resultado = Collections.synchronizedList(new ArrayList<Worker>());
		//Si se requieren mas trabajadores de los que hay disponibles
		if(nTrabajadores>trabajadores.size()) {
			return resultado;
		}
		else {
			for(int i=0;i<nTrabajadores;i++) {
				resultado.add(trabajadores.get(i));
			}
			trabajadores.removeAll(resultado);
		}
		return resultado;
	}

	public synchronized void liberarTrabajadores(List<Worker> v) throws RemoteException{
		//Cuando el maestro termina libera a los trabajadores involucrados
		for(int i=0;i<v.size();i++) {
			trabajadores.add(v.get(i));
		}
		//Cuando los trabajadores terminan se eliminan de ese trabajo
		v.removeAll(v);
	}

}
