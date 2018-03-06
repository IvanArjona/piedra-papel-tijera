package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<Integer, List<ServerThreadForClient>> rooms;
	private Map<Integer, List<GameElement>> movements;
	private int nClients;
	
	private ServerSocket socket;
	
	public GameServerImpl(int port) {
		this.port = port;
		this.nClients = 0;
		this.socket = null;
		this.rooms = new HashMap<Integer, List<ServerThreadForClient>>();
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
				System.out.println("Cliente conectado [" + socket.getInetAddress() + ":" + socket.getLocalPort() + "]");

				int nSala = (int) Math.ceil(++nClients / 2.0);

				// Lanza un hilo por cada cliente
				ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, nSala);

				if (nClients % 2 != 0) {
					rooms.put(nSala, new ArrayList<ServerThreadForClient>());
				}

				List<ServerThreadForClient> sala = rooms.get(nSala);
				sala.add(clientThread);
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
		System.out.println("Shutdown");
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Error IO: " + e.getMessage());
		}
	}

	/**
	 * Envía un mensaje a todos los clientes de una sala.
	 * 
	 * @param element elemento a enviar
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		// TODO: Canlcula la jugada.
	}

	/**
	 * Elimina una sala.
	 * 
	 * @param id id de la sala.
	 */
	@Override
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
		private boolean running;
		
		public ServerThreadForClient(Socket client, int room) {
			this.room = room;
			this.running = true;
			try {
				// Flujos de entrada y salida
				this.out = new ObjectOutputStream(client.getOutputStream());
				this.in = new ObjectInputStream(client.getInputStream());
				// Inicia el hilo
				this.start();
			} catch (IOException e) {
				System.err.println("Error IO: " + e.getMessage());
			}
		}

		/**
		 * Envía el resultado al cliente.
		 * 
		 * @param element elemento a mandar.
		 */
		public void sendResult(GameElement result) {
			try {
				out.writeObject(result);
			} catch (IOException e) {
				System.err.println("Error IO: " + e.getMessage());
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
			GameResult result;
			ElementType elementType;
			try {
				while (true) {
					GameElement element = (GameElement) in.readObject();
					System.out.println(element.toString());
					elementType = element.getElement();

					if (elementType == ElementType.LOGOUT) {
						// Para el hilo
						this.finalize();
					} else if (elementType == ElementType.SHUTDOWN) {
						// Cierra el servidor
						shutdown();
					}

					result = GameResult.WIN;
					out.writeObject(result);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
