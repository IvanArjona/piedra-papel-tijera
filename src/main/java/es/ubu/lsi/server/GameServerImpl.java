/**
 * 
 */
package es.ubu.lsi.server;

import java.net.InetAddress;

import es.ubu.lsi.common.GameElement;

/**
 * Implementación del servidor.
 * 
 * @author Iván Arjona Alonso
 * @author Álvaro Ruifernandez Palacios
 *
 */
public class GameServerImpl implements GameServer {
	
	private final int port;
	
	public GameServerImpl(int port) {
		this.port = port;
	}
	
	/**
	 * Inicia el servidor.
	 */
	public void startup() {
		// TODO
	}
	
	/**
	 * Cierra el servidor.
	 */
	public void shutdown() {
		// TODO

	}

	/**
	 * Envía un mensaje a todos los clientes de una sala.
	 * 
	 * @param element elemento a enviar
	 */
	public void broadcastRoom(GameElement element) {
		// TODO

	}

	/**
	 * Elimina un cliente.
	 * 
	 * @param id id del cliente.
	 */
	public void remove(int id) {
		// TODO

	}

	/**
	 * Abre la aplicación del servidor.
	 * 
	 * @param args argumentos
	 */
	public static void main(String[] args) {
		int port = 1500;
		
		GameServer server = new GameServerImpl(port);
		
		// Arranca el servidor
		server.startup();
	}
	
	/**
	 * Hilo que escucha a cada cliente.
	 * 
	 * @author Iván Arjona Alonso
	 * @author Álvaro Ruifernandez Palacios
	 *
	 */
	private class ServerThreadForClient implements Runnable {

		/**
		 * Espera los mensajes de cada cliente.
		 */
		public void run() {
			// TODO
			
		}
		
	}

}
