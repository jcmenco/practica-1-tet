# Práctica 1: Tópicos especiales en telemática (TET)

## Sistema distribuido

### Propósito del sistema
La idea de este sistema es que un cliente pueda almacenar la información de sus transacciones bancarias en una especie de base de datos distribuida. Naturalmente, el cliente no tiene idea del funcionamiento de la red, ya que desde su perspectiva simplemente está mandando información a un "servidor". 

Se diseñó la red de tal forma que haya comunicación entre todos los nodos (esto incrementa la tolerancia a fallos o a pérdida de información), pero solo exista un punto de acceso con el cliente. Esto puede parecer contradictorio, porque que solo exista un punto de comunicación con el cliente representa un riesgo, ya que si se cae ese punto se pierde la comunicación. La razón para hacerlo de esta forma es porque es más simple de implementar, y se quería diseñar un sistema minimalista (algo simple).

### Topología de la red
Esta es una red P2P centralizada que tiene 4 nodos "secundarios" y un nodo central. 

Las interacción con el cliente se hace a través del nodo central, además, este nodo se encarga de la identificación de cada cliente y el particionamiento de la data. Los demás nodos se encargan únicamente de guardar y buscar información en sus archivos y enviarla al nodo central.

Hay comunicación entre todos los nodos, de tal manera que se pueda acceder a los datos por distintas rutas.

![image](https://user-images.githubusercontent.com/80720494/135316763-bca6da64-9fcd-44f3-bd17-c6ab5aef8202.png)

### Funcionamiento del sistema
El cliente tiene 2 opciones: guardar información o buscar información. La distinción entre estas dos solicitudes se hace en el nodo central, y de acuerdo a eso se despliegan algunos procesos. Cabe aclarar que antes de cualquier cosa el nodo central identifica al cliente con una ID construida con 2 números aleatorios tomados de su número de cuenta. Verifica si la cuenta del cliente ya está registrada, y si no lo está la agrega a su registro (que es un archivo llamado "clientesDB.txt").

Guardar información: El cliente escribe su número de cuenta junto con información de la transacción (tipo de cuenta, monto y fecha). Siempre se cumple que la longitud de este mensaje es de 36 caracteres. Una vez identificado esto, en el nodo central se divide el mensaje en 3 partes, y se le agrega el identificador de cliente y un identificador de orden para reconstruir el mensaje adecuadamente dada una solicitud de búsqueda de datos. Cada parte del mensaje se envía a un nodo vecino aleatorio, y una vez realizado esto vuelve a estar a la espera de una nueva conexión/solicitud.

En los nodos secundarios, la trama de mensaje recibida para guardarse siempre es de 15 caracteres, y simplemente se añade al registro de datos (que es un archivo llamado "tramasDB.txt").

Buscar información: El cliente escribe únicamente su cuenta, y el nodo central se encarga de encontrar el identificador asociado con esa cuenta. Una vez encontrado, se envía una solicitud a un nodo vecino aleatorio para que revise en su registro si existe una o más tramas que tengan ese identificador. Si no existe, se pasa la solicitud a otro nodo vecino aleatorio. Este proceso se realiza hasta que el nodo central reciba las 3 partes del mensaje original. Una vez pasa esto, envía un mensaje a todos los nodos para que dejen de buscar.

Una vez obtenidas todas las partes, se reconstruye el mensaje teniendo en cuenta el orden, y se devuelve al cliente. Si no se encontró un ID para la cuenta dada, se retorna un "Not found" al cliente.
