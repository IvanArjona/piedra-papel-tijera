package es.ubu.lsi.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import es.ubu.lsi.common.GameElement;

/**
 * Implementación del cliente.
 * 
 * @author Iván Arjona Alonso
 * @author Álvaro Ruifernandez Palacios
 *
 */
public class GameClientImpl implements GameClient {

	private final int port;
	private final InetAddress server;
	private final String username;
	
	/**
	 * Constructor del cliente.
	 * 
	 * @param server dirección del servidor
	 * @param port puerto del servidor
	 * @param username nombre de usuario
	 * @throws UnknownHostException Servidor desconocio
	 */
	public GameClientImpl(String server, int port, String username) throws UnknownHostException {
		this.server = InetAddress.getByName(server);
		this.port = port;
		this.username = username;

		// Inicia el cliente
		this.start();
	}
	
	/**
	 * Inicia el cliente.
	 * 
	 * @return true si se ha arrancado, false si no
	 */
	@Override
	public boolean start() {
		// Inicia el hilo para escuchar al servidor
		GameClientListener listener = new GameClientListener();
		new Thread(listener).start();
		
		// TODO: Esto está mal
		return true;
	}

	/**
	 * Manda la acción a realizar al servidor. 
	 * 
	 * @param element elemento a mandar
	 */
	@Override
	public void sendElement(GameElement element) {
		// TODO
	}

	/**
	 * Saca al cliente del juego.
	 */
	@Override
	public void disconnect() {
		// TODO
	}
	
	/**
	 * Abre la aplicación del cliente.
	 * 
	 * @param args argumentos
	 * @throws UnknownHostException Host desconocido
	 */
	public static void main(String[] args) throws UnknownHostException {
		int port = 1500;
		String server = "localhost";
		String username = null;
		
		// Configura el cliente según el número de parámetros
		if (args.length == 1) {
			server = args[0];
			username = args[1];
		} else if (args.length == 2) {
			username = args[0];
		} else {
			System.err.println("Número de parámetros no válido");
			System.exit(1);
		}
		
		// Instancia el cliente
		GameClient client = new GameClientImpl(server, port, username);
	}

	/**
	 * Hilo que escucha las respuesta del servidor.
	 * 
	 * @author Iván Arjona Alonso
	 * @author Álvaro Ruifernandez Palacios
	 * 
	 */
	private class GameClientListener implements Runnable {

		public void run() {
			// TODO
			
		}
		
	}

}
