// Programa que ejerce el rol de maestro.

package master;

import java.io.EOFException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.*; 
import jobs.*; 

public class Master<T,S> extends UnicastRemoteObject implements TaskCB<T,S>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 211013062992462478L;
	//Trabajo asociado al Master
	@SuppressWarnings("rawtypes")
	private static Job trabajoRecibido;

        //Gestor asociado a maestro
	@SuppressWarnings("rawtypes")
	private static Manager manager;

	public Master() throws RemoteException {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T,S> void main(String args[]){
		if (args.length!=4) {
			System.err.println("Uso: hostRegistro numPuertoRegistro JobClass nWorkers");
			return;
		}
		else {
			Class<?> c = null;
			try {
				c = Class.forName(args[2]);
			} catch (ClassNotFoundException e4) {
				System.err.println("No se ha encontrado la clase asociada a: "+ args[2]);
				System.exit(1);
			}
			Object o = null;
			try {
				o = c.newInstance();
			} catch (InstantiationException | IllegalAccessException e3) {
				System.err.println("La clase no puede ser instanciada");
				System.exit(1);
			}
			if(!(o instanceof Job)) {
				System.err.println("El objeto especificado no es un trabajo");
				System.exit(1);
			}
			else {
				//Trabajo que llega como argumento
				Master.trabajoRecibido = (Job) o;
				//Referencia al manager
				if (System.getSecurityManager() == null)
					System.setSecurityManager(new SecurityManager());
				try {
					manager = (Manager) Naming.lookup("//" + args[0] + ":" + args[1] + "/MW_SRV");
				} catch (MalformedURLException | RemoteException | NotBoundException e2) {
					System.err.println("Maquina de gestor o puerto incorrectos");
					System.exit(1);
				}
				//Entrada a procesar
				T entrada=null;
				//Tarea asociada a cada trabajo
				Task<T,S> tarea=null;
				//Callback asociado a cada tarea
				TaskCB<T,S> callback = null;
				try {
					callback = new Master<T,S>();
				} catch (RemoteException e2) {
					e2.printStackTrace();
				}
				List<Worker<T,S>> trabajadores = null;
				List<Worker<T,S>> trabajadoresCaidos = Collections.synchronizedList(new ArrayList<Worker<T,S>>());
				try {
					//Trabajadores que se requieren
					trabajadores=manager.getTrabajadoresRequeridos(Integer.parseInt(args[3]));
					if(trabajadores.size()==0) {
						//Si no hay suficientes trabajadores el maestro termina la ejecucion con 1
						System.err.println("No hay suficientes trabajadores para satisfacer la peticion");
						System.exit(1);
					}
				} catch (NumberFormatException | RemoteException e1) {
					e1.printStackTrace();
				}
				//Metodo de iniciacion de trabajo
				trabajoRecibido.initJob(Integer.parseInt(args[3]));
				boolean fin=false;
				boolean caido=false;
				boolean iteracion;
				int exito;
				while(!fin) {
					iteracion=false;
					//Para poder meter al final de la lista a los trabajadores no caidos por orden
					exito = 0;
					for(int i=0;i<trabajadores.size() && !iteracion;i++) {
						if(!caido) {
							try {
								//Se consumen datos de entrada
								entrada=(T) trabajoRecibido.getNextInput();
							} catch (EOFException e) {
								//Llegamos al fin de datos de entrada y salimos del bucle
								fin=true;
								break;
							}
							tarea=trabajoRecibido.getTask();
						}
						Worker<T,S> w=trabajadores.get(i);
						try {
							//Trabajador empieza tarea
							w.start(tarea, entrada, callback);
							caido=false;
							//Se suma si trabajador tiene exito en la tarea
							exito++;
						}
						catch(RemoteException e) {
							caido=true;
							iteracion=true;
						}
						//Si se termina el trabajo,se terminan los trabajadores o se cae un trabajador salimos del bucle
					}
					//Para sincronizar los procesos exitosos
					for(int i=0;i<exito;i++) {
						try {
							trabajadores.get(i).join();
						} catch (RemoteException e) {
							System.err.println("No se ha podido conectar con el trabajador");
						}
					}
					//Si el trabajador está caido,ordenamos los exitosos y lo eliminamos
					if(caido==true) {
						for(int i=0;i<exito;i++) {
							Worker<T,S> aux=trabajadores.get(i);
							trabajadores.remove(aux);
							trabajadores.add(aux);
						}
						trabajadoresCaidos.add(trabajadores.get(0));
						trabajadores.remove(trabajadores.get(0));
						//Si todos los trabajadores están caídos 
						if(trabajadores.size()==0) {
							try {
								manager.liberarTrabajadores(trabajadoresCaidos);
							} catch (RemoteException e2) {
								System.err.println("No se ha podido conectar con el gestor");
							}
							System.err.println("No se ha podido realizar el trabajo. Todos los trabajadores caídos");
							System.exit(1);
						}
						System.err.println("Trabajador caído. Cogiendo a otro trabajador");
					}
				}
				trabajoRecibido.endJob();
				try {
					manager.liberarTrabajadores(trabajadores);
					manager.liberarTrabajadores(trabajadoresCaidos);
				} catch (RemoteException e) {
					System.err.println("No se ha podido conectar con el gestor");
				}
				System.exit(0);
			}
		}
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void mandarResultado(T entrada, S resultado, Exception e)throws RemoteException{
		trabajoRecibido.putResult(entrada, resultado, e);
	}
}
