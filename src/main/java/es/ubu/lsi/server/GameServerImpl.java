package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.common.ElementType;
import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;

/**
 * Implementación del servidor.
 * 
 * @author Iván Arjona Alonso
 * @author Álvaro Ruifernandez Palacios
 *
 */
public class GameServerImpl implements GameServer {

	private final int port;
	private List<ServerThreadForClient> clients;
	private List<GameElement> movements;
	private int nClients;

	private ServerSocket socket;

	/**
	 * Constructor del servidor.
	 * 
	 * @param port
	 *            puerto
	 */
	public GameServerImpl(int port) {
		this.port = port;
		this.nClients = 0;
		this.socket = null;
		this.clients = new ArrayList<ServerThreadForClient>();
		this.movements = new ArrayList<GameElement>();
	}

	/**
	 * Inicia el servidor.
	 */
	@Override
	public void startup() {
		try {
			socket = new ServerSocket(port);

			while (true) {
				Socket clientSocket = socket.accept();

				// Asigna un id al cliente
				// Si id del jugador es impar y no hay nadie en la sala correspondiente,
				// se le asigna el siguiente id para asignarle a una sala nueva.
				if (nClients % 2 != 0) {
					boolean previousAlive = clients.get(nClients - 1).isAlive();
					if (!previousAlive) {
						clients.add(nClients, null);
						movements.add(nClients, null);
						nClients++;
					}
				}

				int clientId = nClients++;

				// Lanza un hilo por cada cliente
				ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, clientId);
				clientThread.start();

				// Añade el cliente a la lista
				clients.add(clientId, clientThread);
				movements.add(clientId, null);

			}

		} catch (IOException e) {
			System.err.println("Error IO: " + e.getMessage());
		}
	}

	/**
	 * Cierra el servidor.
	 */
	@Override
	public void shutdown() {
		System.out.println("Cerrando servidor");

		// Cierra todos los clientes
		clients.stream().forEach(ServerThreadForClient::logout);

		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Error IO: " + e.getMessage());
		}
	}

	/**
	 * Envía un mensaje a todos los clientes de una sala.
	 * 
	 * @param element
	 *            elemento a enviar
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		int clientId = element.getClientId();
		int oponentId = clientId + (clientId % 2 == 0 ? 1 : -1);
		GameResult result;
		GameElement oponentElement = movements.get(oponentId);

		if (oponentElement == null) {
			// El oponente no ha hecho un movimiento
			result = GameResult.WAITING;
		} else {
			ElementType oponentMovement = oponentElement.getElement();
			ElementType movement = element.getElement();

			result = gameWinner(movement, oponentMovement);
			
			// Borra el movimiento del oponente para la siguiente partida.
			movements.set(oponentId, null);
		}
		// Envía el resultado al cliente.
		clients.get(clientId).sendResult(result);
	}
	
	/**
	 * Determina si el cliente gana, pierde o empata.
	 * 
	 * @param movement movimiento del cliente
	 * @param oponentMovement movimiento del oponente
	 * @return resultado
	 */
	private GameResult gameWinner(ElementType movement, ElementType oponentMovement) {
		GameResult result;
		
		if (movement == oponentMovement || oponentMovement == null) {
			result = GameResult.DRAW;
		} else {
			switch (movement) {
			case PAPEL:
				result = oponentMovement == ElementType.PIEDRA ? GameResult.WIN : GameResult.LOSE;
				break;
			case PIEDRA:
				result = oponentMovement == ElementType.TIJERA ? GameResult.WIN : GameResult.LOSE;
				break;
			case TIJERA:
				result = oponentMovement == ElementType.PAPEL ? GameResult.WIN : GameResult.LOSE;
				break;
			default:
				result = null;
				break;
			}
		}
		
		return result;
	}

	/**
	 * Elimina un cliente y su sala.
	 * 
	 * @param id
	 *            id del cliente.
	 */
	@Override
	public void remove(int id) {
		int roomId = clients.get(id).getIdRoom();
		clients.subList(roomId * 2, roomId * 2 + 2).stream()
				.filter(c -> c != null && c.isAlive())
				.forEach(ServerThreadForClient::logout);
	}

	/**
	 * Abre la aplicación del servidor.
	 * 
	 * @param args
	 *            argumentos
	 */
	public static void main(String[] args) {
		int port = 1500;

		// Instancia el servidor
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
	private class ServerThreadForClient extends Thread {

		private ObjectInputStream in;
		private ObjectOutputStream out;
		private int room;
		private int id;
		private boolean running;
		private String username;

		/**
		 * Constructor del hilo de cada cliente.
		 * 
		 * @param client
		 *            socket del cliente
		 * @param id
		 *            id del cliente
		 */
		public ServerThreadForClient(Socket client, int id) {
			this.id = id;
			this.room = (int) Math.floor(id / 2.0);
			this.running = true;

			System.out.println("Cliente conectado. Id: " + id + "  Sala: " + room);

			try {
				// Flujos de entrada y salida
				out = new ObjectOutputStream(client.getOutputStream());
				in = new ObjectInputStream(client.getInputStream());

				// Envia el id al cliente
				out.writeObject(id);

				// Recibe el nombre de usuario
				username = (String) in.readObject();
			} catch (IOException e) {
				System.err.println("Error IO: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Obtiene el id de la sala en la que está el cliente.
		 * 
		 * @return id de la sala
		 */
		public int getIdRoom() {
			return room;
		}

		/**
		 * Espera los mensajes del cliente.
		 */
		@Override
		public void run() {
			try {
				while (running) {
					GameElement element = (GameElement) in.readObject();
					System.out.println(username + "[" + room + "]: " + element.toString());

					switch (element.getElement()) {
					case SHUTDOWN:
						// Cierra el servidor
						shutdown();
						break;
					case LOGOUT:
						// Elimina el cliente
						remove(id);
						break;
					default:
						movements.set(id, element);
						broadcastRoom(element);
						break;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				// Salta al cerrar la sila out. No hacer nada
			}
		}

		/**
		 * Envía el resultado al cliente.
		 * 
		 * @param result
		 *            resultado
		 */
		private void sendResult(GameResult result) {
			try {
				out.writeObject(result);
				// Si espera vuelve a comprobar la jugada en 1 segundo
				if (result == GameResult.WAITING) {
					GameElement element = movements.get(id);
					Thread.sleep(1000);
					broadcastRoom(element);
				}
			} catch (IOException | InterruptedException e) {
				// Se para la ejecución, no hacer nada
			}
		}

		/**
		 * Al lanzar una interrupción cierra los streams de entrada y salida, y para la
		 * ejecución del hilo.
		 */
		private void logout() {
			running = false;
			System.out.println(username + " desconectado. Id: " + id + "  Sala: " + room);
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
