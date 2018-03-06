package es.ubu.lsi.server;

import es.ubu.lsi.common.GameElement;

/**
 * Define los métodos que implementará el servidor
 * 
 * @author Iván Arjona Alonso
 * @author Álvaro Ruifernandez Palacios
 *
 */
public interface GameServer {

	/**
	 * Inicia el servidor.
	 */
	public void startup();
	
	/**
	 * Cierra el servidor.
	 */
	public void shutdown();
	
	/**
	 * Envía un mensaje a todos los clientes de una sala.
	 * 
	 * @param element elemento a enviar
	 */
	public void broadcastRoom(GameElement result);
	
	/**
	 * Elimina un cliente.
	 * 
	 * @param id id del cliente.
	 */
	public void remove(int id);

}
