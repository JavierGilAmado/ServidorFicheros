package message;

import java.io.Serializable;

public class MensajeTomaFichero implements Serializable {
    public String nombreFichero;
    public byte[] contenidoFichero;
    public int bytesValidos;
    public boolean ultimoMensaje;
}

