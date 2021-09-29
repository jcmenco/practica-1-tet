# Práctica 1: Tópicos especiales en telemática (TET)
## Sistema distribuido

### Propósito del sistema
La idea de este sistema es que un cliente pueda almacenar la información de sus transacciones bancarias en una especie de base de datos distribuida. Naturalmente, el cliente no tiene idea del funcionamiento de la red, ya que desde su perspectiva simplemente está mandando información a un "servidor". 

Se diseñó la red de tal forma que haya comunicación entre todos los nodos (esto incrementa la tolerancia a fallos o a pérdida de información), pero solo exista un punto de acceso con el cliente. Esto puede parecer contradictorio, porque que solo exista un punto de comunicación con el cliente representa un riesgo, ya que si se cae ese punto se pierde la comunicación. La razón para hacerlo de esta forma es porque es más simple de implementar, y se quería diseñar un sistema minimalista (algo simple).

