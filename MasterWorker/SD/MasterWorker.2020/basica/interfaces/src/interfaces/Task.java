// Interfaz que debe satisfacer una tarea para poder ser procesada.
// No se trata de una interfaz remota. Por tanto, una clase que implementa esta
// interfaz se transferirá "serializada" del maestro al trabajador.
// NO MODIFICAR

package interfaces;

public interface Task  {
    public String execute(String t) throws Exception;
}
       

