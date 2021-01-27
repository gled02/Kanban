package domain;

import java.sql.Date;

/**
 * Objet bean permettant de manipuler/récupérer les informations d'une tâche.
 */
public class Task {
	
	// ATTRIBUTS 
	
	private Long id;
	private Long idKanban;
	private String description;
	private Long idAssign;
	private Date date;
	private String nameColumn;
	
	// CONSTRUCTEURS
	
	public Task() {
		// rien
	}
	
	// REQUETES
	
	/**
	 * L'identifiant de la tâche.
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * L'identifiant du kanban où la tâche se trouve.
	 */
	public Long getIdKanban() {
		return this.idKanban;
	}
	
	/**
	 * La description d'un tâche.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * L'identifiant du responsable de la tâche.
	 */
	public Long getIdAssign() {
		return this.idAssign;
	}
	
	/**
	 * Date limite de la réalisation de la tâche.
	 */
	public Date getDate() {
		return this.date;
	}
	
	/**
	 * Le nom de la colonne où se trouve la tâche.
	 */
	public String getNameColumn() {
		return this.nameColumn;
	}
	
	// COMMANDES
	
	/**
	 * Fixe l'identifiant d'une tâche à id.
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Fixe l'identifiant du kanban où la tâche se trouve à idKanban.
	 * @param idKanban
	 */
	public void setIdKanban(Long idKanban) {
		this.idKanban = idKanban;
	}
	
	/**
	 * Fixe la description d'une tâche à description.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Fixe l'identifiant du responsable de la tâche à idAssing.
	 * @param idAssign
	 */
	public void setIdAssign(Long idAssign) {
		this.idAssign = idAssign;
	}

	/**
	 * Fixe la date limite de la réalisation d'une tâche à date.
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Fixe le nom de la colonne où la tâche se trouve à nameColumn.
	 * @param nameColumn
	 */
	public void setNameColumn(String nameColumn) {
		this.nameColumn = nameColumn;
	}

}
