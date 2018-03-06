package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.plaf.synth.SynthSeparatorUI;

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

	private final int port;
	private final InetAddress server;
	private final String username;
	
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
	}
	
	/**
	 * Inicia el cliente.
	 * 
	 * @return true si se ha arrancado, false si no
	 */
	@Override
	public boolean start() {
		try {
			socket = new Socket(server, port);

			out = new ObjectOutputStream(this.socket.getOutputStream());
			in = new ObjectInputStream(this.socket.getInputStream());

			// Inicia el hilo para escuchar al servidor
			GameClientListener listener = new GameClientListener();

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

		// Inicia el cliente
		if (client.start()) {
			System.out.println("Iniciado");
			System.out.println("Posibles movimientos: PIEDRA, PAPEL, TIJERA.");
			System.out.println("Salir de la sala: LOGOUT. Apagar el servidor: SHUTDOWN");
			System.out.println("-------------------------------------------------------");
			
			while (elementType != ElementType.LOGOUT) {
				// Lee el movimiento
				ElementType elemtType = readMovement();
				GameElement element = new GameElement(username, elementType);
				// Envía el movimiento al servidor
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
			System.out.println("et: " + elementType.toString());
			
		} catch (IllegalArgumentException e) {
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
		
		public GameClientListener() {
			// Inicia el hilo
			this.start();
		}
		
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
						break;
					case LOSE:
						mensaje = "Has perdido";
						break;
					case WAITING:
						mensaje = "Esperando al otro jugador";
						break;
					case WIN:
						mensaje = "Has ganado!";
					}
					System.out.println(mensaje);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Se ha cerrado el servidor");
					System.exit(1);
				}
			}
		}
		
	}

}
