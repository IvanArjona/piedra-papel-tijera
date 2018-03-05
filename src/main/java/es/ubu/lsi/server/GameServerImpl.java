package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	private Map<Integer, List<ServerThreadForClient>> rooms;
	private int nClients;
	
	private ServerSocket socket;
	
	
	public GameServerImpl(int port) {
		this.port = port;
		this.nClients = 0;
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
				int nSala = (int) Math.ceil(++nClients / 2);

				// Lanza un hilo por cada cliente
				ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, nSala);
	
				if (nClients % 2 != 0) {
					rooms.put(nSala, new ArrayList<ServerThreadForClient>());
				}
				
				List<ServerThreadForClient> sala = rooms.get(nSala);
				sala.add(clientThread);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Cierra el servidor.
	 */
	@Override
	public void shutdown() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Envía un mensaje a todos los clientes de una sala.
	 * 
	 * @param element elemento a enviar
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		int id = element.getClientId();
		List<ServerThreadForClient> clientThreads = rooms.get(id);
		
		for (ServerThreadForClient client : clientThreads) {

		}
	}

	/**
	 * Elimina un cliente.
	 * 
	 * @param id id del cliente.
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

		ObjectInputStream in;
		ObjectOutputStream out;
		int room;
		
		public ServerThreadForClient(Socket client, int room) {
			this.room = room;
			try {
				this.in = new ObjectInputStream(client.getInputStream());
				this.out = new ObjectOutputStream(client.getOutputStream());
				this.start();
			} catch (IOException e) {
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
			// TODO
		}
		
	}

}
