// Interfaz remota de los servicios del Manager

package interfaces;
import java.rmi.*;
import java.util.List;

public interface Manager<T,S> extends Remote {
	public void darDeAltaTrabajador(Worker<T,S> w) throws RemoteException;
	public void darDeBajaTrabajador(Worker<T,S> w) throws RemoteException;
	public void liberarTrabajadores(List<Worker<T,S>> v) throws RemoteException;
	public List<Worker<T,S>> getTrabajadoresRequeridos(int nTrabajadores)throws RemoteException;
}
       

