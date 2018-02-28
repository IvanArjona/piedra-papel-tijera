package es.ubu.lsi.client;

import es.ubu.lsi.common.GameElement;

/**
 * Define los métodos que implementará el cliente
 * 
 * @author Iván Arjona Alonso
 * @author Álvaro Ruifernandez Palacios
 *
 */
public interface GameClient {
	
	public boolean start();
	public void sendElement(GameElement element);
	public void disconnect();
	
}
