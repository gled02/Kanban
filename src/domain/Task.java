package domain;

import java.sql.Date;

/**
 * Objet bean permettant de manipuler/r�cup�rer les informations d'une t�che.
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
	 * L'identifiant de la t�che.
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * L'identifiant du kanban o� la t�che se trouve.
	 */
	public Long getIdKanban() {
		return this.idKanban;
	}
	
	/**
	 * La description d'un t�che.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * L'identifiant du responsable de la t�che.
	 */
	public Long getIdAssign() {
		return this.idAssign;
	}
	
	/**
	 * Date limite de la r�alisation de la t�che.
	 */
	public Date getDate() {
		return this.date;
	}
	
	/**
	 * Le nom de la colonne o� se trouve la t�che.
	 */
	public String getNameColumn() {
		return this.nameColumn;
	}
	
	// COMMANDES
	
	/**
	 * Fixe l'identifiant d'une t�che � id.
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Fixe l'identifiant du kanban o� la t�che se trouve � idKanban.
	 * @param idKanban
	 */
	public void setIdKanban(Long idKanban) {
		this.idKanban = idKanban;
	}
	
	/**
	 * Fixe la description d'une t�che � description.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Fixe l'identifiant du responsable de la t�che � idAssing.
	 * @param idAssign
	 */
	public void setIdAssign(Long idAssign) {
		this.idAssign = idAssign;
	}

	/**
	 * Fixe la date limite de la r�alisation d'une t�che � date.
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Fixe le nom de la colonne o� la t�che se trouve � nameColumn.
	 * @param nameColumn
	 */
	public void setNameColumn(String nameColumn) {
		this.nameColumn = nameColumn;
	}

}
