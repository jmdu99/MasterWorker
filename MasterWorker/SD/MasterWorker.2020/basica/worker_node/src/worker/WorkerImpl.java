// Implementación de la interfaz del worker

package worker;
import java.rmi.*;
import java.rmi.server.*;

import interfaces.*;

public class WorkerImpl extends UnicastRemoteObject implements Worker{
	public static final long  serialVersionUID=1234567891;
	//Gestor asociado a trabajador
	private  Manager m;
	//Hilo asociado a cada trabajador
	private Thread th1;
	public WorkerImpl(Manager m) throws RemoteException {
		super();
		this.m=m;
		//Damos de alta al trabajador en el gestor
		this.m.darDeAltaTrabajador(this);
	}
	//Dar de baja a un trabajador
	public synchronized void stopWorker() throws RemoteException {
		//Eliminamos al trabajador del gestor
		m.darDeBajaTrabajador(this);
	}
	public synchronized void start(Task tarea,String entrada, TaskCB callback) throws RemoteException{
		th1=new Thread() {
			public synchronized void run() {
				String resultado = null;
				try {
					resultado = tarea.execute(entrada);
				} catch (Exception e) {
					try {
						callback.mandarResultado(entrada,resultado,e);
					} catch (RemoteException e2) {
						System.err.println("No se ha podido conectar con el maestro");
						System.exit(1);
					}
				} 
				try {
					callback.mandarResultado(entrada,resultado,null);
				} catch (RemoteException e) {
					System.err.println("No se ha podido conectar con el maestro");
					System.exit(1);
				}
			}
		};
		th1.start();
	}
	public synchronized void join() throws RemoteException{
		try {
			th1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
