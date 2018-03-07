package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.ubu.lsi.common.ElementType;
import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;

/**
 * Implementación del cliente.
 * 
 * @author Iván Arjona Alonso
 * @author Álvaro Ruifernandez Palacios
 *
 */
public class GameClientImpl implements GameClient {

	private int id;
	private final int port;
	private final InetAddress server;
	private final String username;
	private boolean blocked;
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
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
		this.blocked = false;
	}
	
	/**
	 * Inicia el cliente.
	 * 
	 * @return true si se ha arrancado, false si no
	 */
	@Override
	public boolean start() {
		try {
			// Crea el socket para comunicarse con el servidor
			socket = new Socket(server, port);

			// Flujos de datos. entrada y salida
			out = new ObjectOutputStream(this.socket.getOutputStream());
			in = new ObjectInputStream(this.socket.getInputStream());

			// Recibe el id del servidor
			id = (Integer) in.readObject();
			
			// Envía el nombre de usuario al servidor
			out.writeObject(username);
			
			// Inicia el hilo para escuchar al servidor
			GameClientListener listener = new GameClientListener();
			listener.start();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Manda la acción a realizar al servidor. 
	 * 
	 * @param element elemento a mandar
	 */
	@Override
	public void sendElement(GameElement element) {
		blocked = true;
		try {
			out.writeObject(element);
		} catch (IOException e) {
			System.err.println("Error IO: " + e.getMessage());
		}
	}

	/**
	 * Saca al cliente del juego.
	 */
	@Override
	public void disconnect() {
		System.out.println("Partida terminada");
		try {
			// Cierra recursos
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		ElementType elementType = null;
				
		// Configura el cliente según el número de parámetros
		if (args.length == 1) {
			username = args[0];
		} else if (args.length == 2) {
			server = args[0];
			username = args[1];
		} else {
			System.err.println("Número de parámetros no válido");
			System.exit(1);
		}
		
		// Instancia el cliente
		GameClientImpl client = new GameClientImpl(server, port, username);

		// Inicia el cliente
		if (client.start()) {
			System.out.println("Entrando al juego como " + username);
			System.out.println("Posibles movimientos: PIEDRA, PAPEL, TIJERA.");
			System.out.println("Salir de la sala: LOGOUT. Apagar el servidor: SHUTDOWN");
			System.out.println("-------------------------------------------------------");
			
			while (true) {
				// Espera una respuesta del servidor
				while (client.blocked) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				
				// Lee el movimiento
				elementType = readMovement();
				
				// Envía el movimiento al servidor
				GameElement element = new GameElement(client.id, elementType);
				client.sendElement(element);
			}
		} else {
			System.err.println("No se ha podido conectar con el servidor");
			System.exit(1);
		}
	}
	
	private static ElementType readMovement() {
		ElementType elementType = null;
		System.out.println("Elige tu movimiento: ");

		try {
			// Lee la entrada por teclado
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String movimiento = stdIn.readLine();
			
			// Convierte el movimiento a mayúsculas
			movimiento = movimiento.toUpperCase();
						
			elementType = ElementType.valueOf(movimiento);
			
		} catch (IllegalArgumentException e) {
			// Vuelve a pedir el movimiento
			System.out.println("Movimiento no válido. Vuelve a intentarlo");
			return readMovement();
		} catch (IOException e) {
			System.err.println("Error IO: " + e.getMessage());
			System.exit(1);
		}
		
		return elementType;
	}
	


	/**
	 * Hilo que escucha las respuesta del servidor.
	 * 
	 * @author Iván Arjona Alonso
	 * @author Álvaro Ruifernandez Palacios
	 * 
	 */
	private class GameClientListener extends Thread {
		
		private String mensaje;
		
		/**
		 * Escucha mensajes del servidor.
		 */
		@Override
		public void run() {
			while (true) {
				try {
					GameResult result = (GameResult) in.readObject();
					switch (result) {
					case DRAW:
						mensaje = "Habeís empatado";
						blocked = false;
						break;
					case LOSE:
						mensaje = "Has perdido";
						blocked = false;
						break;
					case WAITING:
						mensaje = "Esperando al otro jugador";
						break;
					case WIN:
						mensaje = "Has ganado!";
						blocked = false;
					}
					System.out.println(mensaje);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// Desconectado del servidor
					System.out.println("Partida terminada");
					System.exit(1);
				}
			}
		}
		
	}

}
