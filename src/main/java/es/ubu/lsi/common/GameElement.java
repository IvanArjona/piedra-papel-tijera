package es.ubu.lsi.common;


import java.io.Serializable;

/**
 * Element in game system.
 * 
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Mario Erro
 *
 */
public class GameElement implements Serializable {


	/** Serial version UID. */
	private static final long serialVersionUID = 7467237896682458959L;

	/** Type. */
	private ElementType element;
		
	/** Client username. */
	private String clientUsername;
	
	/**
	 * Constructor.
	 * 
	 * @param username nombre de usuario del cliente
	 * @param element element
	 */
	public GameElement(String username, ElementType element) {
		this.setClientUsername(username);
		this.setElement(element);
	}
	
	/**
	 * Gets element.
	 * 
	 * @return element
	 */
	public ElementType getElement() {
		return element;
	}
	
	/**
	 * Sets element.
	 * 
	 * @param element element
	 */
	public void setElement(ElementType element) {
		this.element = element;
	}
	
	/**
	 * Gets id.
	 * 
	 * @return sender username
	 */
	public String getClientUsername() {
		return clientUsername;
	}

	/**
	 * Sets sender id.
	 * 
	 * @param id sender id
	 * 
	 */
	private void setClientUsername(String username) {
		this.clientUsername = username;
	}
	

	@Override
	public String toString() {
		return "GameElement [element=" + element + ", clientId=" + clientUsername + "]";
	}
}

