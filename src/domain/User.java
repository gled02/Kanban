package domain;

/**
 * Objet bean permettant de manipuler/récupérer les informations d'un utilisateur.
 */
public class User {

	// ATTRIBUTS
	
	private Long id;
	private String username;
	private String name;
	private String surname;
	private String password;
	
	// CONSTRUCTEURS
	
	public User() {
		// rien
	}
	
	// REQUETES
	
	/**
	 * L'identifiant d'un utilisateur.
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * Nom d'utilisateur.
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Le prénom de l'utilisateur.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Le nom de l'utilisateur.
	 */
	public String getSurname() {
		return this.surname;
	}
	
	/**
	 * Le mot de passe de l'utilisateur.
	 */
	public String getPassword() {
		return this.password;
	}
	
	// COMMANDES
	
	/**
	 * Fixe l'identifiant d'un utilisateur à id.
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Fixe le nom d'utilisateur à username.
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Fixe le prénom de l'utilisateur à name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Fixe le nom de l'utilisateur à surname.
	 * @param surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	/**
	 * Fixe le mot de passe de l'utilisateur à password.
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
