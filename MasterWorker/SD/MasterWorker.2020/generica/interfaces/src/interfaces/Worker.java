// Interfaz remota del Worker que permite lanzar la ejecución de una tarea

package interfaces;
import java.rmi.*;

public interface Worker<T,S> extends Remote {
	public void stopWorker() throws RemoteException;
	public void start(Task<T, S> tarea,T entrada, TaskCB<T,S> callback) throws RemoteException;
	public void join() throws RemoteException;
}
       

