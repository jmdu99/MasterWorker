// Interfaz remota para la notificación al maestro del fin de una tarea.

package interfaces;
import java.rmi.*;




public interface TaskCB extends Remote  {
	public void mandarResultado(String entrada,String resultado, Exception e)throws RemoteException;
}

       

