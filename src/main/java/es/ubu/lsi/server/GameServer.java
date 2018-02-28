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

	public void startup();
	public void shutdown();
	public void broadcastRoom(GameElement element);
	public void remove(int id);

}
