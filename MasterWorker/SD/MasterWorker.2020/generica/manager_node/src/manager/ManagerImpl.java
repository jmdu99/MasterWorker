// Implementación de la interfaz de los servicios del Manager

package manager;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.*;

public class ManagerImpl<T,S> extends UnicastRemoteObject implements Manager<T,S> {
	public static final long  serialVersionUID=1234567890; 

	@SuppressWarnings("rawtypes")
	private static List<Worker> trabajadores=Collections.synchronizedList(new ArrayList<Worker>());

	public ManagerImpl () throws RemoteException  {
		super();
	}
	public synchronized void darDeAltaTrabajador(Worker<T,S> w) throws RemoteException{
		trabajadores.add(w);
	}
	public synchronized void darDeBajaTrabajador(Worker<T,S> w) throws RemoteException{
		trabajadores.remove(w);
	}
	@SuppressWarnings("unchecked")
	public synchronized List<Worker<T,S>> getTrabajadoresRequeridos(int nTrabajadores)throws RemoteException{
		List<Worker<T,S>> resultado = Collections.synchronizedList(new ArrayList<Worker<T,S>>());
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

	public synchronized void liberarTrabajadores(List<Worker<T,S>> v) throws RemoteException{
		//Cuando el maestro termina libera a los trabajadores involucrados
		for(int i=0;i<v.size();i++) {
			trabajadores.add(v.get(i));
		}
		//Cuando los trabajadores terminan se eliminan de ese trabajo
		v.removeAll(v);
	}

}
