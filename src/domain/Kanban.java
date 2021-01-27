package domain;

import java.util.Arrays;
import java.util.List;

/**
 * Objet bean permettant de manipuler/r�cup�rer les informations d'un kanban.
 */
public class Kanban {

	// ATTRIBUTS
	
	private Long id;
	private Long id_owner;
	private String name_kanban;
	private String columns;
	private boolean is_public;
	private String description;
	
	// CONSTRUCTEURS
	
	public Kanban() {
		// rien
	}
	
	// REQUETES
	
	/**
	 * L'identifiant du kanban.
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * L'identifiant de l'utilisateur qui est gestionnaire de ce kanban.
	 */
	public Long getIdOwner() {
		return this.id_owner;
	}
	
	/**
	 * Le nom du kanban.
	 */
	public String getNameKanban() {
		return this.name_kanban;
	}
	
	/**
	 * La liste des colonnes du kanban s�par�es par des virgules, renvoy� sous la 
	 * forme d'une cha�ne de caract�res. 
	 */
	public String getColumns() {
		return this.columns;
	}
	
	/**
	 * La liste des colonnes du kanban.
	 */
	public List<String> getListColumns() {
		return Arrays.asList(this.columns.split(","));
	}
	
	/**
	 * Boolean pour voir si un kanban est public.
	 */
	public boolean isPublic() {
		return this.is_public;
	}
	
	/**
	 * La description d'un kanban.
	 */
	public String getDescription() {
		return this.description;
	}
	
	// COMMANDES
	
	/**
	 * Fixe l'identifiant d'un kanban � id.
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Fixe l'identifiant du gestionnaire de kanban � idOwner.
	 * @param idOwner
	 */
	public void setIdOwner(Long idOwner) {
		this.id_owner = idOwner;
	}
	
	/**
	 * Fixe le nom du kanban � nameKanban.
	 * @param nameKanban
	 */
	public void setNameKanban(String nameKanban) {
		this.name_kanban = nameKanban;		
	}
	
	/**
	 * Fixe les colonnes du kanban � columns.
	 * @param columns
	 */
	public void setColumns(String columns) {
		this.columns = columns;		
	}
	
	/**
	 * D�finit un kanban comme public ou pas selon isPublic.
	 * @param isPublic
	 */
	public void setPublic(boolean isPublic) {
		this.is_public = isPublic;
	}
	
	/**
	 * Fixe la description d'un kanban � description.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
