package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domain.Kanban;
import domain.User;

/**
 * Objet permettant d'envoyer des requ�tes � la base de donn�es, sur la table KANBAN.
 * 		Cette table correspond � l'ensemble des kanbans.
 * @cons <pre>
 *     $DESC$ Un objet utilisant factory afin d'initialiser les connexions � la base de donn�es.
 *     $ARGS$ DAOFactory factory
 *     $PRE$
 *         DAOFactory.dbIsValidate() </pre>
 */
public class KanbanDAO {
	
	// ATTRIBUTS
	
	// Requ�te qui s�lectionne le plus grand identifiant dans table KANBAN.
	private static final String SQL_MAX_ID 						= String.format("SELECT max(id) FROM %s;"							, DAOFactory.TABLE_KANBAN);
	
	// Requ�te qui s�lectionne les kanbans d'un identifiant donn�.
	private static final String SQL_SELECT_BY_ID 				= String.format("SELECT * FROM %s WHERE id=?;"						, DAOFactory.TABLE_KANBAN);
	
	// Requ�te qui s�lectionne les kanbans publics.
	private static final String SQL_SELECT_BY_IS_PUBLIC 		= String.format("SELECT * FROM %s WHERE is_public=?;"				, DAOFactory.TABLE_KANBAN);
	
	// Requ�te qui s�lectionne les kanbans de l'utilisateur donn�.
	private static final String SQL_SELECT_BY_OWNER 			= String.format("SELECT * FROM %s WHERE id_owner=?;"   				, DAOFactory.TABLE_KANBAN);
	
	// Requ�te qui ins�re un kanban dans la table KANBAN.
	private static final String SQL_INSERT 						= String.format("INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?);"			, DAOFactory.TABLE_KANBAN);
	
	// Requ�te qui supprime un kanban de l'identifiant donn�.
	private static final String SQL_DELETE 						= String.format("DELETE FROM %s WHERE id=?;"						, DAOFactory.TABLE_KANBAN);
	
	private DAOFactory factory;
	
	// CONSTRUCTEURS
	
	public KanbanDAO(DAOFactory factory) {
		this.factory = factory;
	}
	
	// REQUETES
	
	/**
	 * Renvoie le kanban de l'identifiant id.
	 * Pour faire cela, nous ouvrons une connexion avec la base de donn�es avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requ�te, en utilisant l'attribut SQL_SELECT_BY_ID
	 * et en donnant comme param�tre l'identifiant du kanban. 
	 * Une exception est lev�e en cas de probl�me.
	 * � la fin, la connexion avec la base de donn�es est ferm�e.
	 * @param id
	 * @throws DAOException
	 */
	public Kanban getKanban(Long id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Kanban kanban = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_BY_ID, 
					false,
					id);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				kanban = map(resultSet);
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return kanban;
	}
	
	/**
	 * Renvoie la liste des kanbans de l'utilisateur user.
	 * Pour faire cela, nous ouvrons une connexion avec la base de donn�es avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requ�te, en utilisant l'attribut SQL_SELECT_BY_OWNER
	 * et en donnant comme param�tre l'identifiant de l'utilisateur. 
	 * Une exception est lev�e en cas de probl�me.
	 * � la fin, la connexion avec la base de donn�es est ferm�e.
	 * @param user
	 * @throws DAOException
	 */
	public List<Kanban> getKanbanByOwner(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Kanban> kanban = new ArrayList<Kanban>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_BY_OWNER, 
					false,
					user.getId());
			
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				kanban.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return kanban;
	}

	/**
	 * Renvoie la liste des kanbans publics.
	 * Pour faire cela, nous ouvrons une connexion avec la base de donn�es avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requ�te, en utilisant l'attribut SQL_SELECT_BY_IS_PUBLIC
	 * et en donnant comme param�tre 1 (kanban public -> 1, kanban priv� -> 0). 
	 * Une exception est lev�e en cas de probl�me.
	 * � la fin, la connexion avec la base de donn�es est ferm�e.
	 * @throws DAOException
	 */
	public List<Kanban> getPublicKanbans() throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Kanban> kanban = new ArrayList<Kanban>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_BY_IS_PUBLIC, 
					false,
					1);
			
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				kanban.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return kanban;
	}
	
	// COMMANDES
	
	/**
	 * Ins�re le kanban kanban dans la table KANBAN.
	 * Pour faire cela, nous ouvrons une connexion avec la base de donn�es avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requ�te, en utilisant l'attribut SQL_INSERT
	 * et en donnant comme param�tres l'id, le nom, le gestionnaire, les colonnes, le boolean 
	 * isPublic et la description du kanban.
	 * Une exception est lev�e en cas de probl�me.
	 * � la fin, la connexion avec la base de donn�es est ferm�e.
	 * @param kanban
	 * @throws DAOException
	 */
	public void createKanban(Kanban kanban) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			Long maxId = getNextId() + 1;
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_INSERT,
					false,
					maxId, kanban.getIdOwner(), kanban.getNameKanban(), kanban.getColumns(), 
					kanban.isPublic(), kanban.getDescription());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Creating kanban failed !");
			}
			kanban.setId(maxId);
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
		
	}
	
	/**
	 * Supprime de la table KANBAN le kanban kanban.
	 * Pour faire cela, nous ouvrons une connexion avec la base de donn�es avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requ�te, en utilisant l'attribut SQL_DELETE
	 * et en donnant comme param�tre l'identifiant du kanban.
	 * Une exception est lev�e en cas de probl�me.
	 * � la fin, la connexion avec la base de donn�es est ferm�e.
	 * @param kanban
	 * @throws DAOException
	 */
	public void deleteKanban(Kanban kanban) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_DELETE,
					false,
					kanban.getId());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Deleting kanban failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	// OUTILS
	
	/**
	 * Cette m�thode priv�e est utilis�e lorsque nous faisons des insertions dans la table 
	 * KANBAN, pour avoir des identifiants uniques (contrainte d'int�gralit�).
	 * Renvoie le plus grand identifiant de la table KANBAN.	 
	 * Pour faire cela, nous ouvrons une connexion avec la base de donn�es avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requ�te, en utilisant l'attribut SQL_MAX_ID.
	 * Une exception est lev�e en cas de probl�me.
	 * � la fin, la connexion avec la base de donn�es est ferm�e.
	 * @throws DAOException
	 */
	private Long getNextId() throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_MAX_ID,
					false);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong(1);
			} else {
				throw new DAOException("Error while loading max id inside kanbans table.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * Renvoie un objet kanban qui est initialis� � partir de la table KANBAN.
	 * Pour cela nous utilisons le bean Kanban.
	 * @param resultSet
	 * @throws SQLException
	 */
	protected static Kanban map(ResultSet resultSet) throws SQLException {
		Kanban kanban = new Kanban();
		kanban.setId(resultSet.getLong("id"));
		kanban.setIdOwner(resultSet.getLong("id_owner"));
		kanban.setNameKanban(resultSet.getString("name_kanban"));
		kanban.setColumns(resultSet.getString("columns"));
		kanban.setPublic(resultSet.getBoolean("is_public"));
		kanban.setDescription(resultSet.getString("description"));
		return kanban;
	}
}

