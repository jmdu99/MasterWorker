// Interfaz remota para la notificación al maestro del fin de una tarea

package interfaces;
import java.rmi.*;

public interface TaskCB<T,S> extends Remote  {
	public void mandarResultado(T entrada,S resultado, Exception e)throws RemoteException;
}

       

