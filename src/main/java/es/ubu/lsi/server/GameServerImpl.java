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
	private int nClients;
	
	private ServerSocket socket;
	
	public GameServerImpl(int port) {
		this.port = port;
		this.nClients = 0;
		this.socket = null;
		this.clients = new ArrayList<ServerThreadForClient>();
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
					ServerThreadForClient clienteAnterior = clients.get(nClients - 1);
					if (clienteAnterior == null) {
						nClients++;
					}
				}
				
				int clientId = nClients++;
				
				// Lanza un hilo por cada cliente
				ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, clientId);
				clientThread.start();
				
				// Añade el cliente a la lista
				clients.add(clientThread);

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
		for (ServerThreadForClient client : clients) {
			client.interrupt();
		}
		
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
		// TODO: Calcula la jugada.
	}

	/**
	 * Elimina un cliente.
	 * 
	 * @param id id del cliente.
	 */
	@Override
	public void remove(int id) {
		int roomId = clients.get(id).getIdRoom();
		List<ServerThreadForClient> room = clients.subList(roomId, roomId + 2);

		for (ServerThreadForClient client : room) {
			if (client != null) {
				client.interrupt();
			}
		}
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
		private int id;
		
		public ServerThreadForClient(Socket client, int id) {
			this.id = id;
			this.room = (int) Math.floor(id / 2.0);

			System.out.println("Cliente conectado. Id: " + id + "  Sala: " + room + 
					" [" + socket.getInetAddress() + ":" + socket.getLocalPort() + "]  ");
			
			try {
				// Flujos de entrada y salida
				this.out = new ObjectOutputStream(client.getOutputStream());
				this.in = new ObjectInputStream(client.getInputStream());
				
				// Crea GameElement y se lo envía al cliente
				GameElement gameElement = new GameElement(id, null);
				this.out.writeObject(gameElement);
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
				while (!interrupted()) {
					GameElement element = (GameElement) in.readObject();
					System.out.println(id + "[" + room + "]: " + element.toString());

					elementType = element.getElement();

					switch (elementType) {
					case SHUTDOWN:
						// Cierra el servidor
						shutdown();
						break;
					case LOGOUT:
						// Elimina el cliente
						remove(id);
						break;
					default:
						result = GameResult.WIN;
						out.writeObject(result);
						break;
					}
				}
				// Cierra recursos
				in.close();
				out.close();
			} catch (Exception e) {
				System.out.println("Cliente desconectado");
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Al lanzar una interrupción cierra los streams
		 * de entrada y salida, y para la ejecución del hilo.
		 */
		@Override
		public void interrupt() {
			super.interrupt();
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
