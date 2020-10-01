// Interfaz remota de los servicios del Manager

package interfaces;
import java.rmi.*;
import java.util.List;

public interface Manager extends Remote {
	public void darDeAltaTrabajador(Worker w) throws RemoteException;
	public void darDeBajaTrabajador(Worker w) throws RemoteException;
	public void liberarTrabajadores(List<Worker> v) throws RemoteException;
	public List<Worker> getTrabajadoresRequeridos(int nTrabajadores)throws RemoteException;
}
       

