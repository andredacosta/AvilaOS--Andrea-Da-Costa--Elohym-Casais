
package com.avilaos.model;

public enum FaseOperacionBuffer {

    ESPERANDO_PERMISO,

    ESPERANDO_MUTEX,

    OPERANDO_BUFFER,

    LIBERANDO_MUTEX,

    NOTIFICANDO_BUFFER,

    COMPLETADA

}
