// Interfaz remota del Worker que permite lanzar la ejecución de una tarea

package interfaces;
import java.rmi.*;

public interface Worker extends Remote{
	public void stopWorker() throws RemoteException;
	public void start(Task tarea,String entrada, TaskCB callback) throws RemoteException;
	public void join() throws RemoteException;
}
       

