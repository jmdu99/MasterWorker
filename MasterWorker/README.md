Esquema maestro/trabajador con Java/RMI (MasterWorker)
Se trata de un proyecto práctico de desarrollo en grupos de 2 personas cuyo plazo de entrega termina el 27 de mayo.
Consideraciones previas
Antes de describir la práctica propiamente dicha, se considera conveniente resaltar algunos aspectos sobre la misma:
Se trata de una práctica significativamente más compleja que la otra práctica de grupo. Además de que la funcionalidad tiene un mayor grado de dificultad, en esta práctica, a diferencia de lo que ocurre con la otra práctica de grupo, no están especificadas la mayoría de las interfaces, lo que implica un esfuerzo de diseño adicional. Esto es intencionado puesto que se plantea pensando en que sea una práctica minoritaria, de forma que solo la hagan aquellos alumnos que ya hayan completado la otra práctica de grupo y quieran profundizar en algunos conceptos adicionales de la asignatura buscando una calificación más alta en la parte práctica de la misma (incluso por encima del 10), pero asumiendo un esfuerzo extra.
Como se verá a lo largo de este enunciado, se plantea una versión básica de la práctica donde toda la información que se procesa es de tipo String y una más avanzada donde se gestionan tipos genéricos. Se puede optar por solo realizar la versión básica, que permite obtener una nota máxima de 7, o por completar la básica y luego evolucionarla hacia la versión avanzada, pero también por centrarse desde el principio directamente en la versión genérica saltándose la básica, lo que permite también obtener la nota máxima. Nótese que el trabajo con tipos genéricos en Java dista mucho de ser trivial y, si no se tiene una gran soltura en el manejo de los mismos, conlleva un esfuerzo apreciable.
Grados de libertad en el desarrollo de la práctica
Aunque todavía no sabemos nada de la práctica y no se puede enteder completamente el contenido de esta sección, se ha considerado oportuno incluir al principio del documento, para que no pasen inadvertidas, qué restricciones existen a la hora de desarrollar el código de la práctica:
No se pueden declarar nuevas clases, excepto si están anidadas en otra clase ya existente. El mandato de entrega no recogerá ningún fichero fuente adicional.
No se pueden modificar las clases ManagerSrv ni WorkerSrv, así como tampoco las contenidas en el paquete jobs ni la clase Task que define la interfaz de las tareas y está incluida en el paquete interfaces.
Los cambios que se hagan durante el desarrollo de la práctica no pueden causar que dejen de estar operativas las clases del paquete jobs.
No se puede redefinir qué argumentos de línea de mandatos recibe la clase Master.
Objetivo de la práctica
La práctica consiste en desarrollar un esquema maestro-trabajador que, como se ha analizado en la parte teórica de la asignatura, es el esquema de procesamiento más habitual en los entornos de computación distribuida, estando detrás de modelos tan populares como MapReduce.
En este esquema hay básicamente tres roles, que, normalmente, corresponderán a procesos ejecutando en distintas máquinas:

El maestro (master) que recibe una solicitud de ejecutar un trabajo compuesto de múltiples tareas. En el sistema puede haber múltiples maestros activos solicitando la ejecución de distintos trabajos.
El trabajador (worker) que ejecuta las tareas del trabajo. Existirán un número considerable de procesos de este tipo.
Suele haber también un rol adicional, que podríamos denominar gestor (manager), que conoce qué trabajadores hay en el sistema, y si están ocupados ejecutando un trabajo, pertimiendo de esta forma la ejecución simultánea de múltiples trabajos.
Repasemos a continuación el modo de operación de este modelo de procesamiento:

El maestro recibe la petición de ejecutar un trabajo usando un determinado número de trabajadores.
La descripción del trabajo incluye los datos de entrada del mismo, la tarea que debe realizarse sobre cada dato de entrada produciendo un resultado, así como la labor que se llevará a cabo con los resultados obtenidos.
El maestro solicita al gestor el número de trajadores requeridos por el trabajo y el gestor se los asigna. Previamente, cada proceso trabajador se habrá dado de alta en el gestor.
El maestro distribuye el código de la tarea entre los trabajadores asignados.
El maestro va obteniendo cada dato del trabajo enviándoselo a cualquiera de los trabajadores asignados que esté libre, teniendo que esperar en caso de que todos estuvieran ocupados. También se puede considerar un esquema en el que un trabajador puede estar procesando simultáneamente hasta N datos para aprovechar su potencia de procesamiento (piense en un trabajador multinúcleo), pero para la práctica, por sencillez, hemos planteado un esquema con un dato por trabajador en cada momento.
El trabajador completa el trabajo enviando el resultado.
El maestro va procesando los resultados obtenidos hasta que recibe el último resultado, dando por completada la ejecución del trabajo informándole al gestor para que sepa que esos nodos trabajadores están disponibles para otros trabajos.
Dado el elevado tiempo de ejecución de este tipo de trabajos y el gran número de nodos involucrados, hay una probabilidad no despreciable de que se caiga algún nodo durante la evolución del mismo. En el caso de la caída del maestro, en el esquema maestro-trabajador básico se pierde toda la computación (para evitarlo, habría que implementar un esquema de replicación para la funcionalidad del maestro). Si se trata de un trabajador, el maestro debería detectar esa caída para enviar la tarea no completada a otro nodo. En la práctica, no vamos a resolver ese escenario de error donde el trabajador se cae en la mitad de una tarea, pero sí uno más sencillo: el trabajador está caído cuando le enviamos el dato de un trabajo y, por tanto, hay que enviar ese dato a otro trabajador.
En cuanto a la tecnología de comunicación usada en la práctica, se ha elegido Java RMI (si no está familiarizado con el uso de esta tecnología puede consultar esta guía sobre la programación en Java RMI). Es importante resaltar que, gracias al mecanismo de descarga dinámica del código de las clases, la tecnología Java RMI facilita considerablemente la implementación de este esquema automatizando directamente el cuarto paso de la lista previa.

Una visión global preliminar
Para entender mejor las características del sistema que se pretende desarrollar, consideramos conveniente mostrar a priori qué pasos habría que llevar a cabo para ejecutar un trabajo en este sistema.
Con respecto a la fase de compilación, hay un script de compilación (compila.sh) dentro del directorio asociado a cada uno de los tres roles identificados, así como en el directorio donde se definen las interfaces comunes. Nótese que en un sistema de estas características normalmente cada máquina estará dedicada a realizar un determinado rol y solo necesitará tener instalado el software correspondiente a dicho rol, así como tener acceso al fichero de tipo JAR donde se encuentran definidas las interfaces comunes. Dado que, por comodidad, vamos a desarrollar y probar el código en local, ese fichero JAR se va hacer accesible a través de un enlace simbólico y se va a proporcionar un script (compila_todo.sh) para realizar la compilación de todo el código apoyándose en los scripts correspondientes de cada directorio.

Una vez compilado el código, hay que lanzar en cada máquina el software correspondiente al rol que va a interpretar esa máquina. Para ello, se dispone para cada rol de un script (ejecuta.sh) que lleva a cabo esa labor. Nótese que, por razones evidentes, el gestor es el único proceso que se va dar de alta en el RMI Registry y, por tanto, en ese nodo hay que arrancar también ese proceso de Java RMI (se proporciona para ello el script arranca_rmiregistry.sh, que recibe como argumento por qué puerto queremos que dé servicio este proceso):

fperez@maq_manager: ./arranca_rmiregistry.sh 12345 &
fperez@maq_manager: ./ejecuta.sh 12345 
A continuación, hay que lanzar todos los trabajadores (supongamos que hay 1000), que se darán de alta en el gestor, indicándoles en qué máquina y por qué puerto da servicio RMI Registry:
fperez@maq_worker1: ./ejecuta.sh maq_manager 12345
fperez@maq_worker1000: ./ejecuta.sh maq_manager 12345
En este punto ya podemos ejecutar el trabajo en el nodo maestro, teniendo que especificar, además de en qué máquina y por qué puerto da servicio RMI Registry, la clase que describe el trabajo junto con el número de trabajadores que queremos usar:
fperez@maq_master1: ./ejecuta.sh maq_manager 12345 jobs.SimpleJob 1000
Anatomía de un trabajo
Dado que la práctica está centrada en la ejecución de trabajos, en esta sección se va a presentar la clase abstracta que describe un trabajo, que, como se comentó previamente, no se puede modificar, así como un ejemplo de una clase derivada de la misma que representa un trabajo muy sencillo: calcular la suma del cuadrado de una colección de tamaño aleatorio de números aleatorios que se reciben como entrada; evidentemente, se trata de un trabajo que no tiene sentido ejecutarlo de forma remota puesto que el procesamiento de cada dato de entrada conlleva un tiempo despreciable y, a diferencia del resto de los trabajos propuestos, solo tiene interés didáctico.
Para poder entender las diferencias entre las dos versiones de la práctica, se van a presentar tanto la versión genérica, que permite que el dato de entrada y el resultado sean de cualquier tipo, como la básica, en la que el tipo del dato de entrada y el que corresponde al resultado son de la clase String.

La clase abstracta Job, incluida en el paquete jobs, permite definir un trabajo. A continuación, se muestra la versión genérica de la misma:

// Clase abstracta que define un trabajo
package jobs;
import java.io.*;

import interfaces.*;

public abstract class Job<T,S> {
    Task <T, S> t = null;

    Job(Task<T,S> t) {
        this.t = t;
    }

    public boolean initJob(int nWorkers) {
        return true;
    }

    public Task<T,S> getTask() {
        return t;
    }

    public abstract T getNextInput() throws EOFException;

    public abstract void putResult(T input, S result, Exception e);

    public void endJob() {
    }
}
Y acto seguido la versión básica:
// Clase abstracta que define un trabajo
package jobs;
import java.io.*;

import interfaces.*;

public abstract class Job {
    Task t = null;

    Job(Task t) {
        this.t = t;
    }

    public boolean initJob(int nWorkers) {
        return true;
    }

    public Task getTask() {
        return t;
    }

    public abstract String getNextInput() throws EOFException;

    public abstract void putResult(String input, String result, Exception e);

    public void endJob() {
    }
}
Para comprender cómo se comporta esta clase abstracta, a continuación, se muestra el ejemplo de un trabajo muy sencillo (clase SimpleJob del paquete jobs) en su versión genérica:
// Trabajo muy simple (es ridículo ejecutarlo de forma remota)
package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class SimpleJob extends Job<Double, Double> {
    int inputDataSize;
    Double [] inputData;
    int count=0;
    double total = 0;

    public SimpleJob() {
        super(new SimpleTask());
    }
@Override
    public boolean initJob(int nWorkers) {
        inputDataSize = ((int) (1000000 * Math.random()) % 100) + 1;
        inputData = new Double[inputDataSize];
        for (int i=0; i<inputData.length; i++)
            inputData[i] = Math.random();
        return true;
    }
@Override
    public Double getNextInput() throws EOFException  {
        if (count<inputDataSize) return inputData[count++];
        else throw new EOFException();
    }
@Override
    public void putResult(Double input, Double result, Exception e) {
        if (e==null) {
            System.out.println("cuadrado de " + input + " = " + result);
            total += result;
        }
        else
            System.err.println(input + ": recibida excepción imprevista " + e.toString());
    }
@Override
    public void endJob() {
        System.out.println("Total = " + total);
    }
}
Y en la versión básica que usa el tipo String tanto para datos como para resultados (observe la necesidad de conversiones de String a Double y viceversa):
/ Trabajo muy simple (es ridículo ejecutarlo de forma remota)
package jobs;

import java.util.*;
import java.io.*;
import interfaces.*;

public class SimpleJob extends Job {
    int inputDataSize;
    Double [] inputData;
    int count=0;
    double total = 0;

    public SimpleJob() {
        super(new SimpleTask());
    }
@Override
    public boolean initJob(int nWorkers) {
        inputDataSize = ((int) (1000000 * Math.random()) % 100) + 1;
        inputData = new Double[inputDataSize];
        for (int i=0; i<inputData.length; i++)
            inputData[i] = Math.random();
        return true;
    }
@Override
    public String getNextInput() throws EOFException  {
        if (count<inputDataSize) return inputData[count++].toString();
        else throw new EOFException();
    }
@Override
    public void putResult(String input, String result, Exception e) {
        if (e==null) {
            System.out.println("cuadrado de " + input + " = " + result);
            total += Double.parseDouble(result);
        }
        else
            System.err.println(input + ": recibida excepción imprevista " + e.toString());
    }
@Override
    public void endJob() {
        System.out.println("Total = " + total);
    }
}
Basándonos en la clase abstracta y en el ejemplo de clase derivada podemos explicar el modo de operación de un trabajo, teniendo en mente los tres componentes que lo definen: la fuente de datos de entrada que alimenta el trabajo, la computación que se aplica a cada dato (la tarea; nótese que un trabajo tiene asociadas dos clases: una que representa el trabajo propiamente dicho y otra, que se analizará más adelante, que corresponde a la tarea que hay que realizar sobre cada dato de entrada) y el tratamiento que se realiza con el resultado de procesar cada dato:
La clase derivada instancia en su constructor un objeto de la clase que realizará el procesamiento de cada dato de entrada del trabajo. Esa clase debe implementar la interfaz Task. El constructor de la superclase simplemente almacena ese objeto en un atributo y proporciona un método (getTask) para dar acceso al mismo. Como se verá más adelante, el maestro recibirá como argumento de línea de mandatos la clase que define el trabajo que se desea ejecutar e instanciará un objeto de esa clase.
El siguiente paso en el ciclo de vida del trabajo es iniciarlo y, para ello, el maestro invocará el método initJob pasándole como argumento el número de trabajadores que se van a usar para ejecutarlo (ese dato será ignorado en algunos trabajos, pero puede ser útil en otros: en la tarea PiJob, que se describirá más adelante, interesa ejecutar solo una tarea por cada trabajador). En este método, se incluirán las operaciones requeridas para preparar la colección de datos. En el ejemplo, se genera el vector de tamaño aleatorio de números aleatorios que se usa como fuente de entrada. En los ejemplos donde los datos de entrada estén almacenados en un fichero en el nodo maestro, en este punto se puede realizar la apertura del fichero. El carácter booleano del método permite indicar cualquier error a la hora de preparar esos datos de entrada. Si no se requiere ningún tipo de preparación no es necesario redefinir este método dejando directamente visible el de la clase base.
Una vez iniciado el trabajo, el maestro solicitará un nuevo dato de entrada (getNextInput), hará que lo procese la tarea asociada al trabajo (accesible a través del método getTask de la clase base) e informará al trabajo del resultado, o de la generación de una excepción durante el procesamiento del dato, invocando el método putResult que lo procesará.
Con respecto a getNextInput, retornará el siguiente dato de entrada, usando la excepción EOFException para indicar el final de los datos.
En cuanto a putResult, además del resultado, recibe nuevamente el dato de entrada correspondiente a ese resultado (nótese que, dado que el trabajo se ejecutará en paralelo, los resultados pueden llegar en cualquier orden y es necesario, por tanto, relacionar cada resultado con el dato de entrada al que corresponde) y la excepción que se ha podido producir durante el procesamiento del dato. En algunos casos, como en el ejemplo, no se espera a priori ningún tipo de excepción y, por tanto, su tratamiento conlleva únicamente informar de ese error imprevisto. En otros casos, como en el trabajo FileAlphaJob, sí se espera como parte del procesamiento la posibilidad de que aparezca la excepción FileNotFoundException, tratándose pertinentemente. Nótese que en los ejemplos propuestos la excepción se trata de forma síncrona pero también podría hacerse de una manera más asíncrona lanzándola al inicio de este método.
Finalizado el tratamiento, el maestro invocará el método endJob, que, en caso de ser necesario, puede realizar algún tipo de cálculo agregando los resultados (en el ejemplo, calculando la suma total) y liberar, en caso de que se requiera, los recursos asociados al acceso a la fuente de datos.
Un trabajo, por tanto, queda definido por una clase derivada de la clase Job que representa el trabajo propiamente dicho y una clase que implementa la interfaz Task. Nótese que se trata de una interfaz que tienen que compartir el maestro y los trabajadores pero no es remota. En consecuencia, la clase que implementa esta interfaz viajará serializada desde el maestro a los trabajadores y, por tanto, además de Task, debe implementar la interfaz Serializable.
A continuación, se muestra la interfaz para la versión genérica:

// Interfaz que debe satisfacer una tarea para poder ser procesada
// No se trata de una interfaz remota. Por tanto, una clase que implementa esta
// interfaz se transferirá "serializada" del maestro al trabajador.
package interfaces;

public interface Task <T,S> {
    public S execute(T t) throws Exception;
}
Y acto seguido la correspondiente a la versión básica:
// Interfaz que debe satisfacer una tarea para poder ser procesada
// No se trata de una interfaz remota. Por tanto, una clase que implementa esta
// interfaz se transferirá "serializada" del maestro al trabajador.

package interfaces;

public interface Task  {
    public String execute(String t) throws Exception;
Observe que esta interfaz, que no puede modificarse, proporciona un método (execute) para realizar el procesamiento de un dato devolviendo un resultado y pudiendo generar una excepción.
A continuación, se muestra la tarea asociada al trabajo simple en su versión genérica:

// Tarea muy simple (es ridículo ejecutarla de forma remota)
package jobs;

import java.io.*;
import interfaces.*;

public class SimpleTask implements Task<Double, Double>, Serializable {
    public static final long serialVersionUID = 1234567895;
    public Double execute(Double x) {
            return x * x;
    }
}
y en su versión basica:
// Tarea muy simple (es ridículo ejecutarla de forma remota)
package jobs;

import java.io.*;
import interfaces.*;

public class SimpleTask implements Task, Serializable {
    public static final long serialVersionUID = 1234567895;
    public String execute(String x) {
            Double d = Double.parseDouble(x);
            Double res = d * d;
            return res.toString();
    }
}
Como parte del material de apoyo, se proporcionan cuatro ejemplos de trabajos:
SimpleJob (y SimpleTask) ya explicado.
PiJob (y PiTask) que corresponde a un trabajo que realiza un cálculo aproximado del número pi usando el método de Monte Carlo. Para el lector curioso, se trata de un algoritmo que se basa en la generación de pares de números aleatorios con valores entre 0 y 2 que se ubicarían en un espacio bidimensional donde existe un círculo de radio 1 inscrito en un cuadrado de lado 2 (lógicamente, la longitud del lado del cuadrado se corresponde con el diámetro del círculo). Observe que calculando la proporción de puntos aleatorios que se ubican dentro del círculo con respecto al total de puntos generados se corresponde con la misma proporción que hay entre el área de un círculo y la del cuadrado, por lo que multiplicando por 4, que corresponde al área del cuadrado, esa proporción tenemos una estimación del número pi que será más precisa cuántas más iteraciones se lleven a cabo. Centrándonos en el trabajo, se trata de un ejemplo donde no hay valores de entrada (se usa el valor null) y solo se van a generar tantas tareas como trabajadores.
FileAlphaJob (y FileAlphaTask) que calcula cuántos caracteres alfabéticos hay en una colección de ficheros almacenados en un hipotético sistema de ficheros distribuido/paralelo al que tienen acceso todos los trabajadores. El nombre de los ficheros que hay que procesar está contenido en un fichero almacenado en el nodo maestro.
NearPointsJob (y NearPointsTask) que calcula por cada punto tridimensional de entrada, almacenados en un fichero de texto en el nodo maestro, qué puntos de un repositorio de puntos, guardados en un fichero binario accesible por todos los trabajadores, están a una distancia menor o igual que un valor especificado. En la operación final de agrupamiento, se determina qué puntos del repositorio han aparecido más frecuentemente como vecinos de los puntos de entrada. El interés de este ejemplo es el uso de tipos de datos definidos por el usuario (el punto 3D) tanto de manera individual como formando parte de colecciones. Nótese que este ejemplo solo se ha incluido para la versión genérica para ilustrar la potencia de poder usar cualquier tipo para los datos y los resultados.
Arquitectura del software del sistema
Antes de pasar a presentar la funcionalidad que se debe desarrollar, se especifica en esta sección qué distintos componentes hay en este sistema.
En primer lugar, hay que recordar que la práctica está diseñada para no permitir la definición de nuevas clases (a no ser que se trate de clases anidadas), estando todas ya presentes, aunque mayoritariamente vacías, en el material de apoyo.

En las siguientes secciones se describe el contenido de cada uno de los cuatro directorios presentes en el material de apoyo de la práctica:

interfaces: contiene las interfaces comunes entre los distintos componentes del sistema.
manager_node: incluye la funcionalidad asociada al rol de gestor.
master_node: incluye la funcionalidad asociada al rol de maestro.
worker_node: incluye la funcionalidad asociada al rol de trabajador.
En cada directorio el código fuente está almacenado dentro del subdirectorio src y dentro de este, como es preceptivo, en un subdirectorio con el nombre del paquete. Con respecto a las clases compiladas, se almacenan dentro del subdirectorio bin.
Aunque se puede usar un IDE para el desarrollo de la práctica, como se explicó al principio del documento, en cada directorio existen scripts (compila.sh) para compilar el código fuente de ese directorio. Recuerde que en el nivel superior se dispone de otro script denominado compila_todo.sh que compila todo el software usando los scripts de compilación particulares de cada directorio.

Asimismo, en todos ellos, exceptuando el correspondiente a las interfaces, se proporciona un script (ejecuta.sh) para ejecutar el programa asociado a ese directorio y, en el directorio del nodo maestro, otro script para arrancar el Registry de RMI.

Con respecto al directorio de las interfaces, el script de compilación también genera un fichero de tipo JAR que está accesible en el resto de los directorios mediante un enlace simbólico.

Revisemos, a continuación, las clases contenidas en cada directorio.

Directorio interfaces
En este directorio se almacenan las clases que definen las interfaces usadas en la interacción entre los distintos componentes y que están ubicadas en el paquete interfaces:
Task: interfaz no remota que define la operación (execute) que debe implementar una tarea para poder ser ejecutada de forma remota. Para realizar la ejecución remota el maestro enviará a un trabajador un objeto que implemente esta interfaz. No se puede modificar este fichero.
TaskCB: interfaz remota de tipo callback que permite al trabajador enviar al maestro el resultado de ejecutar asíncronamente una tarea, tanto el resultado propiamente dicho como, en caso de que se produzca, la excepción generada durante la ejecución de la tarea.
Manager: interfaz remota que define los servicios proporcionados por el gestor, que deberá permitir a los trabajadores darse de alta y de baja, y al maestro solicitar trabajadores para ejecutar un trabajo y liberarlos cuando este se haya completado.
Worker: interfaz remota que define los servicios proporcionados por un trabajador que permiten solicitarle de forma remota la ejecución de una tarea.
Directorio manager_node
En este directorio se almacenan las clases que definen la funcionalidad del nodo gestor contenidas en el paquete manager:
ManagerImpl: contiene la funcionalidad del gestor implementando los servicios definidos en la interfaz Manager.
ManagerSrv: clase principal de este componente que instancia un objeto de la clase anterior y lo da de alta en el registro. Recibe como argumento el puerto por el que está dando servicio el registry. No se puede modificar este fichero.
Directorio worker_node
En este directorio se almacenan las clases que definen la funcionalidad del trabajador que están contenidas en el paquete worker:
WorkerImpl: contiene la funcionalidad de un trabajador implementando los servicios definidos en la interfaz Worker. Asimismo, incluye un método local (stopWorker) que permite finalizar la ejecución del trabajador.
WorkerSrv: clase principal de este componente que obtiene una referencia remota al gestor usando una operación de lookup e instancia un objeto de la clase anterior pasándole como parámetro dicha referencia remota. Por último, muestra un mensaje al usuario y se queda esperando si el usuario teclea algo para invocar el método stopWorker para finalizar la ejecución del trabajador. Recibe como argumentos la máquina y el puerto por el que está dando servicio el registry. No se puede modificar este fichero.
En este directorio hay un subdirectorio denominado Ficheros que representa el hipotético sistema de ficheros distribuido/paralelo accesible desde todos los nodos trabajadores.
Directorio master_node
En este directorio se almacena la clase que define la funcionalidad del maestro (clase Master englobada en el paquete master), así como todos ejemplos de trabajos y tareas ya presentados anteriormente (incluidos en el paquete jobs donde también se encuentra la clase abstracta Job):
Master recibe como argumentos la máquina y el puerto por el que está dando servicio el registry así como la clase que define el trabajo y el número de trabajadores que se utilizarán para ejecutarlo. La funcionalidad de la misma ya ha sido descrita de forma global en los apartados previos y se explicará con más detalle en la próxima sección.
En este directorio hay un subdirectorio denominado Ficheros que representa el hipotético sistema de ficheros local del nodo maestro.
Funcionalidad de la práctica
Como se comentó al principio del documento, se plantean dos versiones del sistema planteado: una básica, que usa datos y resultados de tipo String, y una avanzada, basada en genéricos, que permite datos y resultados de cualquier tipo. Recuerde que se puede optar por solo realizar la parte básica, por completar ambas de forma incremental o por afrontar directamente la versión genérica sin pasar por la básica pudiendo obtener también de esta forma la nota máxima.
En esta sección, se detalla la funcionalidad que se requiere implementar presentándola en varios pasos sucesivos. Se plantea de esta forma para ir incorporando la funcionalidad de manera incremental, aunque uno puede optar por afrontar desde el principio toda la funcionalidad.

Primer paso: ejecución local de un trabajo
En este primer paso no vamos a realizar ningún procesamiento distribuido sino simplemente ejecutar el trabajo en el propio nodo maestro. Esto permitirá conocer mejor los trabajos y las tareas e implementar el ciclo de vida de un trabajo. Evidentemente, toda la labor asociada a este paso se implementa únicamente en la clase Master:
Debe instanciar la clase trabajo recibida como argumento (en este paso se ignorarán el resto de los argumentos). En caso de que el argumento no sea válido el programa terminará con un valor de 1.
Acto seguido debe invocar el método de iniciación del trabajo.
A continuación, debe ir consumiendo los datos de entrada, ejecutando la tarea asociada al trabajo para cada dato e informando al trabajo del resultado, o posible excepción, de esa ejecución.
Consumidos los datos, se informará de la finalización del trabajo.
Para probar la funcionalidad de este primer paso se debería verificar que ejecutan correctamente en local todos los trabajos. Asegúrese de que en el trabajo FileAlphaJob se detecta y propaga la excepción FileNotFoundException cuando el fichero a procesar no existe en el sistema de ficheros accesible por el trabajador.
Segundo paso: ejecución remota secuencial de un trabajo usando un solo trabajador
En este paso ya entran en juego los tres roles y el sistema ya empieza a tener un modo de operación distribuido. Nótese que por el momento solo se va a plantear una funcionalidad mínima para el nodo gestor: simplemente la capacidad para que un único trabajador se dé de alta y para que el maestro pueda obtener la referencia remota a ese trabajador. En el próximo paso desarrollaremos la funcionalidad completa del gestor. A continuación, se especifican los cambios que se deben realizar:
En el gestor, se debe definir e implementar una interfaz mínima para poder realizar la funcionalidad restringida que se plantea en este paso para ese rol.
Con respecto al trabajador, hay que definir en su interfaz la operación para ejecutar de forma remota una tarea, que requerirá la referencia a la misma así como el dato a procesar, e implementarla en la clase WorkerImpl. Nótese que no hay que hacer ningún tratamiento especial con las excepciones en el nodo trabajador puesto que RMI ya se encarga de propagarlas automáticamente al maestro que invocó la operación remota. En esta clase también habrá que incorporar la interacción con el gestor para darse de alta y de baja.
En la clase Master hay que añadir la lógica para obtener la referencia remota al gestor y solicitar un único trabajador, con independencia del valor especificado en el argumento de entrada. Asimismo, habrá que modificar las llamadas locales a la tarea por invocaciones del método del trabajador que permite la ejecución remota.
En este paso puede volver a ejecutar todas las tareas y verificar que siguen funcionando correctamente pero esta vez se ejecutan en el contexto del trabajador remoto.
Es interesante resaltar que en este paso se puede apreciar uno de los mecanismos que hacen a RMI una herramienta adecuada para este tipo de escenarios: la descarga dinámica y automática del código de las clases. El maestro envía un objeto de una clase que implementa la interfaz Task al trabajador y este puede invocar un método de la misma a pesar de no disponer localmente de su código. Si revisa el script que permite ejecutar un trabajador, en el mismo se puede ver la propiedad codebase que permite especificar de qué dirección, local o remota, se puede descargar el código de las clases no presentes en el nodo local:

java.rmi.server.codebase=file:../../master_node/bin/
Tercer paso: ejecución remota secuencial de un trabajo usando múltiples trabajadores
En este paso completaremos la funcionalidad del gestor que debe permitir:
que se den de alta y de baja trabajadores.
que un maestro solicite un conjunto de trabajadores para realizar un trabajo y que los libere cuando termine (recuerde que un trabajador solo puede estar involucrado en un único trabajo en cada momento). Tenga en cuenta que en el sistema puede haber varios trabajos activos y hay que asegurarse de que las estructuras de datos que maneja el gestor se gestionan correctamente ante peticiones concurrentes. Nótese que si no hay suficientes trabajadores para satisfacer la petición de un maestro no se le asignará ninguno y se le informará de este hecho y el maestro deberá terminar la ejecución del trabajo con un valor de 1.
Se va a cambiar también el maestro para que vaya solicitando la ejecución remota de forma rotatoria entre los trabajadores asignados.
Pruebe de nuevo los trabajos pero esta vez con múltiples trabajadores y compruebe cómo se reparte el procesamiento entre ellos, aunque de forma secuencial lo cual no es muy útil.

Cuarto paso: ejecución asíncrona remota de una tarea
La clave para lograr que este sistema sea útil es habilitar la ejecución asíncrona remota de una tarea: el maestro contacta con un trabajador para que inicie la ejecución de una tarea y este le devuelve el control inmediatamente, activando la ejecución de la tarea en el contexto de un thread. Completada la tarea, el trabajador usará un mecanismo de tipo callback (interfaz remota TaskCB) que incluirá un método remoto para notificar al maestro de la finalización de la tarea informándole del resultado, o posible excepción, de la misma.
Este modo de operación asíncrono requiere, por tanto, que en el nodo maestro se implemente la interfaz de tipo callback y que se añada a la petición de ejecución remota una referencia a un objeto de este tipo,

Asimismo, hay que tener en cuenta que este esquema asíncrono requiere poder identificar a qué petición corresponde una respuesta. Para ello, dado que se ha optado por un modelo en el que un trabajador solo puede estar ejecutando una tarea en cada instante, bastaría con enviar la identificación del trabajador como parte de la respuesta. Nótese que a partir de ese identificador el maestro debe deducir a qué dato de entrada corresponde esa respuesta.

La ejecución remota en el contexto de un thread también trae como consecuencia que la excepción que pueda producirse durante la ejecución de la tarea haya que enviarla explícitamente como parte de la respuesta.

Nótese que para este paso nos vamos a conformar con ejecutar un trabajo con una sola tarea y un único trabajador comprobando que somos capaces de recibir de forma asíncrona el resultado, o posible excepción, asociado a la ejecución remota asíncrona de esa tarea. En el siguiente, y último, paso ya se planteará la solución general con múltiples tareas y trabajadores.

A continuación, se especifican algunos de los cambios que se deben realizar en ese paso:

Hay que modificar la interfaz Worker y definir la interfaz TaskCB para adaptarse a este modelo de ejecución asíncrono.
Se debe cambiar WorkerImpl para que ejecute la tarea en el contexto de un thread y envíe el resultado, o posible excepción, así como su identificación mediante el callback.
Se tiene que adaptar Master para incorporar el callback lo que hará que el resultado, o posible excepción, se reciban de forma asíncrona (en el contexto de un thread creado automáticamente por RMI). En ese paso intermedio, que solo plantea la ejecución de una tarea, bastaría con guardar información de qué dato de entrada se está procesando. En el siguiente paso, ya se incluirá la lógica para casar cada respuesta con el dato al que corresponde.
Como se ha comentado previamente, en esta sección nos conformamos con procesar un trabajo con una única tarea por lo que bastaría con incorporar un mecanismo de sincronización que permita a la clase maestra, una vez enviada esa única tarea, esperar por el resultado de la misma.
Quinto paso: versión final
En este paso final, que solo afecta a la clase maestra, ya podemos afrontar toda la funcionalidad del sistema, teniendo que contemplar aspectos tales como:
Como se comentó previamente, el maestro debe incorporar un mecanismo para casar cada respuesta con el dato de entrada al que corresponde.
La recepción asíncrona y concurrente de las respuestas exige revisar la funcionalidad de la clase maestra para evitar condiciones de carrera. Nótese, asimismo, que la entrega de los resultados a la clase que define el trabajo habrá que hacerla de forma secuencial puesto que no sabemos si esa clase está diseñada para permitir una ejecución concurrente.
Si se detecta un error en la comunicación con un trabajador al enviarle un trabajo, se le considerará como definitivamente caído y ya no se le tendrá en cuenta para el resto de la computación. Puede ocurrir, por tanto, que el procesamiento de un trabajo se vea abortado por la falta de trabajadores, terminando con un valor de salida igual a 1. Nótese que, termine de forma correcta o no el trabajo, se liberarán todos los trabajadores asignados al final del mismo, estén estos caídos o no.
En este paso, alcanzamos finalmente el modo de operación característico de un proceso que ejerce el rol de maestro:
Mientras haya trabajadores libres y tareas pendientes de procesar, el maestro enviará la tarea, el dato de entrada y el callback al trabajador libre seleccionado.
Si en ese envío se produce un error de comunicación, se descartará definitivamente ese trabajador y se seleccionará otro libre, pudiendo tener que esperar hasta que uno se libere.
El trabajo se completará cuando finalice la computación de la última tarea o sea abortado por la caída de todos los trabajadores asignados.
Material de apoyo de la práctica
El material de apoyo de la práctica se encuentra en este enlace.
Al descomprimir el material de apoyo se crea el entorno de desarrollo de la práctica, que reside en el directorio: $HOME/DATSI/SD/MasterWorker.2020/.

Entrega de la práctica
Se realizará en la máquina triqui, usando el mandato:
entrega.sd MasterWorker.2020
Este mandato recogerá los siguientes ficheros:

autores Fichero con los datos de los autores:
DNI APELLIDOS NOMBRE MATRÍCULA
memoria.txt Memoria de la práctica. En ella se pueden comentar los aspectos del desarrollo de su práctica que considere más relevantes. Asimismo, puede exponer los comentarios personales que considere oportuno.
MW.zip: Debe crear un paquete ZIP de la siguiente forma:
$ cd DATSI/SD/MasterWorker.2020/
$ zip MW.zip basica/interfaces/src/interfaces/Manager.java basica/interfaces/src/interfaces/TaskCB.java basica/interfaces/src/interfaces/Worker.java basica/master_node/src/master/Master.java basica/manager_node/src/manager/ManagerImpl.java basica/worker_node/src/worker/WorkerImpl.java generica/interfaces/src/interfaces/Manager.java generica/interfaces/src/interfaces/TaskCB.java generica/interfaces/src/interfaces/Worker.java generica/master_node/src/master/Master.java generica/manager_node/src/manager/ManagerImpl.java generica/worker_node/src/worker/WorkerImpl.java