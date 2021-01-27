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
 * Objet permettant d'envoyer des requêtes à la base de données, sur la table GUEST.
 * 		Cette table correspond à l'ensemble des invités dans tous les kanbans.
 * @cons <pre>
 *     $DESC$ Un objet utilisant factory afin d'initialiser les connexions à la base de données.
 *     $ARGS$ DAOFactory factory
 *     $PRE$
 *         DAOFactory.dbIsValidate() </pre>
 */
public class GuestDAO {
	
	// ATTRIBUTS 
	
	// Requête qui sélectionne le plus grand identifiant dans table GUEST.
	private static final String SQL_MAX_ID 						= String.format("SELECT max(id) FROM %s;"							, DAOFactory.TABLE_GUEST);
	
	// Requête qui sélectionne les identifiants des invités d'un kanban donné.
	private static final String SQL_SELECT_GUEST				= String.format("SELECT id_user FROM %s WHERE id_kanban=?"			, DAOFactory.TABLE_GUEST);
	
	// Requête qui sélectionne les identifiants des kanbans d'un invité donné.
	private static final String SQL_SELECT_KANBANS_GUEST		= String.format("SELECT id_kanban FROM %s WHERE id_user=?"			, DAOFactory.TABLE_GUEST);
	
	// Insère dans la table GUEST les valeurs données.
	private static final String SQL_INSERT_GUEST				= String.format("INSERT INTO %s VALUES (?, ?, ?)"					, DAOFactory.TABLE_GUEST);

	// Supprime de la table GUEST les lignes qui correspondent à un kanban donné.
	private static final String SQL_DELETE_KANBAN_GUEST			= String.format("DELETE FROM %s WHERE id_kanban=?"					, DAOFactory.TABLE_GUEST);
	
	// Supprime de la table GUEST les lignes qui correspondent à un kanban donné et à un invité donné.
	private static final String SQL_DELETE_USER_KANBAN_GUEST	= String.format("DELETE FROM %s WHERE id_kanban=? AND id_user=?"	, DAOFactory.TABLE_GUEST);
	
	private DAOFactory factory;
	
	// CONSTRUCTEURS
	
	GuestDAO(DAOFactory factory) {
		this.factory = factory;
	}
	
	// REQUETES
	
	/**
	 * Renvoie la liste des utilisateurs qui sont invités dans le kanban kanban.
	 * Pour faire cela, nous ouvrons une connexion avec la base de données avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requête, en utilisant l'attribut SQL_SELECT_GUEST 
	 * et en donnant comme paramètre l'identifiant du kanban. Si tout se passe bien, une liste de 
	 * type List<User> est envoyé, sinon une exception est levée.
	 * À la fin, la connexion avec la base de données est fermée.
	 * @param kanban
	 * @throws DAOException
	 */
	public List<User> getListGuests(Kanban kanban) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<User> users = new ArrayList<User>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_GUEST, 
					false,
					kanban.getId());
			
			resultSet = preparedStatement.executeQuery();

			UserDAO userDao = factory.getUtilisateurDao();
			while (resultSet.next()) {
				users.add(userDao.getUser(resultSet.getLong("id_user")));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return users;
	}
	
	/**
	 * Renvoie la liste des noms des utilisateurs qui sont invités dans le kanban kanban.
	 * @param kanban
	 * @throws DAOException
	 */
	public List<String> getListGuestsUsername(Kanban kanban) throws DAOException {
		List<User> listUsers = getListGuests(kanban);
		List<String> listUsernames = new ArrayList<String>();
		for (User user : listUsers) {
			listUsernames.add(user.getUsername());
		}
		return listUsernames;
	}
	
	/**
	 * Renvoie la liste des kanbans où l'utilisateur user est invité.
	 * Pour faire cela, nous ouvrons une connexion avec la base de données avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requête, en utilisant l'attribut SQL_SELECT_KANBANS_GUEST 
	 * et en donnant comme paramètre l'identifiant de l'utilisateur. Si tout se passe bien, une liste de 
	 * type List<Kanban> est envoyé, sinon une exception est levée.
	 * À la fin, la connexion avec la base de données est fermée.
	 * @param user
	 * @throws DAOException
	 */
	public List<Kanban> getListKanbanGuest(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Kanban> kanbans = new ArrayList<Kanban>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_KANBANS_GUEST, 
					false,
					user.getId());
			
			resultSet = preparedStatement.executeQuery();

			KanbanDAO kanbanDao = factory.getKanbanDao();
			while (resultSet.next()) {
				kanbans.add(kanbanDao.getKanban(resultSet.getLong("id_kanban")));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return kanbans;
	}
	
	// COMMANDES
	
	/**
	 * Permet d'ajouter un invité dans la table GUEST.
	 * Pour faire cela, nous ouvrons une connexion avec la base de données avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requête, en utilisant l'attribut SQL_INSERT_GUEST
	 * et en donnant comme paramètres le plus grand identifiant dans la table GUEST, l'identifiant de 
	 * l'utilisateur et l'identifiant du kanban où il est invité. Une exception est levée en cas de problème.
	 * À la fin, la connexion avec la base de données est fermée.
	 * @param kanban
	 * @param user
	 * @throws DAOException
	 */
	public void addGuest(Kanban kanban, User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			Long maxId = getNextId() + 1;
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_INSERT_GUEST,
					false,
					maxId, kanban.getId(), user.getId());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Adding guest failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	/**
	 * Permet de supprimer les utilisateurs qui ont comme identifiant user.getId() et qui sont invités 
	 * dans le kanban kanban.	 
	 * Pour faire cela, nous ouvrons une connexion avec la base de données avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requête, en utilisant l'attribut SQL_DELETE_USER_KANBAN_GUEST
	 * et en donnant comme paramètres l'identifiant de l'utilisateur et l'identifiant du kanban où il est invité. 
	 * Une exception est levée en cas de problème.
	 * À la fin, la connexion avec la base de données est fermée.
	 * @param kanban
	 * @param user
	 * @throws DAOException
	 */
	public void deleteKanbanGuest(Kanban kanban, User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_DELETE_USER_KANBAN_GUEST,
					false,
					kanban.getId(), user.getId());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Deleting kanban of user failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}

	/**
	 * Permet de supprimer les utilisateurs qui sont invités dans le kanban kanban.	 
	 * Pour faire cela, nous ouvrons une connexion avec la base de données avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requête, en utilisant l'attribut SQL_DELETE_KANBAN_GUEST
	 * et en donnant comme paramètre l'identifiant du kanban.
	 * Une exception est levée en cas de problème.
	 * À la fin, la connexion avec la base de données est fermée.
	 * @param kanban
	 * @throws DAOException
	 */
	public void deleteKanbanGuests(Kanban kanban) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_DELETE_KANBAN_GUEST,
					false,
					kanban.getId());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Deleting Kanban failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
		
	// OUTILS
	
	/**
	 * Cette méthode privée est utilisée lorsque nous faisons des insertions dans la table 
	 * GUEST, pour avoir des identifiants uniques (contrainte d'intégralité).
	 * Renvoie le plus grand identifiant de la table GUEST.	 
	 * Pour faire cela, nous ouvrons une connexion avec la base de données avec l'aide 
	 * de l'objet DAOFactory. Nous faisons une requête, en utilisant l'attribut SQL_MAX_ID.
	 * Une exception est levée en cas de problème.
	 * À la fin, la connexion avec la base de données est fermée.
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
					true);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong(1);
			} else {
				throw new DAOException("Error while loading max id inside guests table.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
	}
}
