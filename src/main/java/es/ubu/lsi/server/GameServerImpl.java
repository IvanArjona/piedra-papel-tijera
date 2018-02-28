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
	 * Arranca el servidor.
	 */
	public void startup() {
		// TODO: inicializa variables
		
		runServer();
	}
	
	private void runServer() {
		
		runServer();
	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public void broadcastRoom(GameElement element) {
		// TODO Auto-generated method stub

	}

	public void remove(int id) {
		// TODO Auto-generated method stub

	}

	/**
	 * Inicia el servidor.
	 * 
	 * @param args argumentos
	 */
	public static void main(String[] args) {
		int port = 1500;
		
		GameServer server = new GameServerImpl(port);
		
		// Arranca el servidor
		server.startup();
	}
	
	private class ServerThreadForClient implements Runnable {

		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
