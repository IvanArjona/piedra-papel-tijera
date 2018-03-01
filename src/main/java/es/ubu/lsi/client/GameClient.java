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
	
	/**
	 * Inicia el cliente.
	 * 
	 * @return true si se ha arrancado, false si no
	 */
	public boolean start();
	
	/**
	 * Manda la acción a realizar al servidor. 
	 * 
	 * @param element elemento a mandar
	 */
	public void sendElement(GameElement element);
	
	/**
	 * Sale del juego.
	 */
	public void disconnect();
	
}
