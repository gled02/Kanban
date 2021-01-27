package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domain.Kanban;
import domain.Task;
import domain.User;

/**
 * Objet permettant d'envoyer des requ�tes � la base donn�es, sur la table Task
 * 	Cette table correspondant � l'ensemble des t�ches disponibles dans tous les kanbans
 * @cons <pre>
 *     $DESC$ UN objet utilisant factory afin d'initialiser les connexions � la base de donn�e
 *     $ARGS$ DAOFactory factory
 *     $PRE$
 *         DAOFactory.dbIsValidate() </pre>
 */
public class TaskDAO {
	
	// ATTRIBUTS 

	// Requ�te qui s�lectionne le plus grand identifiant dans table Task
	private static final String SQL_MAX_ID 						= String.format("SELECT max(id) FROM %s;"																, DAOFactory.TABLE_TASK);
	
	// Requ�te qui s�lectionne la t�che � partir de son identifiant dans la table Task
	private static final String SQL_SELECT_BY_ID				= String.format("SELECT * FROM %s WHERE id=?"															, DAOFactory.TABLE_TASK);

	// Requ�te qui s�lectionne les t�ches assign�es � un identifiant d'utilisateur dans la table Task
	private static final String SQL_SELECT_ASSIGNED_TASKS		= String.format("SELECT * FROM %s WHERE id_assign=?;"													, DAOFactory.TABLE_TASK);

	// Requ�te qui s�lectionne les t�ches assign�es � un kanban dans la table Task
	private static final String SQL_SELECT_KANBAN_TASKS			= String.format("SELECT * FROM %s WHERE id_kanban=?;"													, DAOFactory.TABLE_TASK);

	// Requ�te qui s�lectionne les t�ches assign�es � un utilisateur sp�cifique � un kanban dans la table Task
	private static final String SQL_SELECT_KANBAN_ASSIGN_TASKS	= String.format("SELECT * FROM %s WHERE id_kanban=? AND id_assign=?;"									, DAOFactory.TABLE_TASK);

	// Requ�te qui s�lectionne les t�ches associ� � un kanban ce trouvant une colonne en particulier dans la table Task
	private static final String SQL_SELECT_KANBAN_COLUMN_TASKS	= String.format("SELECT * FROM %s WHERE id_kanban=? AND name_column=?;"									, DAOFactory.TABLE_TASK);
	
	// Requ�te qui insert une nouvelle ligne dans la table Task
	private static final String SQL_INSERT 						= String.format("INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?);"												, DAOFactory.TABLE_TASK);

	// Requ�te qui supprime la ligne d'identifiant de la table Task
	private static final String SQL_DELETE 						= String.format("DELETE FROM %s WHERE id=?;"															, DAOFactory.TABLE_TASK);

	// Requ�te qui supprime les t�ches associ�es � un kanban dans la table Task
	private static final String SQL_DELETE_BY_KANBAN 			= String.format("DELETE FROM %s WHERE id_kanban=?;"														, DAOFactory.TABLE_TASK);
	
	// Requ�te qui met � jour la t�che � partir de son identifiant dans la table Task
	private static final String SQL_UPDATE_BY_ID				= String.format("UPDATE %s SET description=?, id_assign=?, max_date=?, name_column=?  WHERE id=?"		, DAOFactory.TABLE_TASK);

	// Requ�te qui met � jour l'assignation d'une t�che dans la table Task
	private static final String SQL_UPDATE_ASSIGN_BY_ID			= String.format("UPDATE %s SET id_assign=? WHERE id=?"													, DAOFactory.TABLE_TASK);
	
	private DAOFactory factory;
	
	// CONSTRUCTEURS
	
	/**
	 * Contructeur de l'objet, prend en param�tre un DAOFactory uniquement si DAOFactory.dbIsValidate()
	 * @param factory
	 */
	TaskDAO(DAOFactory factory) {
		this.factory = factory;
	}
	
	// REQUETES
	
	/**
	 * Requ�te renvoyant la t�che d'identifiant id
	 * @param Long id
	 * @return Task task : task.getId() == id
	 * @throws DAOException
	 */
	public Task getTask(Long id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Task task = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_BY_ID, 
					false,
					id);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				task = map(resultSet);
			} else {
				throw new DAOException("Any task corresponding to id='" + id + "'.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return task;
	}
	
	/**
	 * Requ�te renvoyant la liste des t�ches assign� � l'utilisateur user
	 * @param User user
	 * @return List<Task> tasks : (foreach task in tasks : task.getIdAssign() == user.getId())
	 * @throws DAOException
	 */
	public List<Task> getAssignedTasks(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Task> tasks = new ArrayList<Task>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_ASSIGNED_TASKS,
					false,
					user.getId());
			
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				tasks.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return tasks;
	}
	
	/**
	 * Requ�te renvoyant la liste des t�ches assign�s � l'utilisateur user sp�cifique au projet kanban
	 * @param Kanban kanban
	 * @param User user
	 * @return List<Task> tasks : (foreach task in tasks : 
	 * 			task.getIdAssign() == user.getId() &&
	 * 			task.getIdKanban() == kanban.getId())
	 * @throws DAOException
	 */
	public List<Task> getKanbanAssignedTasks(Kanban kanban, User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Task> tasks = new ArrayList<Task>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_KANBAN_ASSIGN_TASKS,
					false,
					kanban.getId(), user.getId());
			
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				tasks.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return tasks;
	}
	
	/**
	 * Requ�te renvoyant la liste des t�ches associ�s � un kanban
	 * @param Kanban kanban
	 * @return List<Task> tasks : (foreach task in tasks : task.getIdKanban() == kanban.getId())
	 * @throws DAOException
	 */
	public List<Task> getKanbanTasks(Kanban kanban) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Task> tasks = new ArrayList<Task>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_KANBAN_TASKS,
					false,
					kanban.getId());
			
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				tasks.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return tasks;
	}
	
	/**
	 * Requ�te renvoyant la liste des t�ches associ�s � un kanban dans une colonne donn�e
	 * @param Kanban kanban
	 * @param String column
	 * @param boolean verifyNameColumn
	 * @return List<Task> tasks : (foreach task in tasks : 
	 * 			task.getIdKanban() == kanban.getId() &&
	 * 			task.getNameColumn() == column)
	 * @throws DAOException
	 */
	public List<Task> getKanbansColumnTasks(Kanban kanban, String column, boolean verifyNameColumn) throws DAOException {
		if (verifyNameColumn && !kanban.getColumns().contains(column)) {
			throw new DAOException("'" + column + "' is not a column of kanban '" + kanban.getNameKanban() + "' !");
		}
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Task> tasks = new ArrayList<Task>();
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_KANBAN_COLUMN_TASKS,
					false,
					kanban.getId(), column);
			
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				tasks.add(map(resultSet));
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return tasks;
	}
	
	// COMMANDES
	
	/**
	 * Commande permettant d'ajouter une t�che dans la base de donn�e
	 * @param Task task
	 * @throws DAOException
	 */
	public void createTask(Task task) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			Long maxId = getNextId() + 1;
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_INSERT,
					false,
					maxId, task.getIdKanban(), task.getDescription(), task.getIdAssign(), task.getDate(), task.getNameColumn());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Creating task failed !");
			}
			task.setId(maxId);
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	/**
	 * Commande permettant de supprimer la t�che de la base de donn�e
	 * @param Task task
	 * @throws DAOException
	 */
	public void deleteTask(Task task) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_DELETE,
					false,
					task.getId());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Deleting task failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	/**
	 * Commande permettant de supprimer les t�ches associ� au kanban pass� en param�tre
	 * @param Kanban kanban
	 * @throws DAOException
	 */
	public void deleteTaskByKanban(Kanban kanban) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_DELETE_BY_KANBAN,
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

	
	/**
	 * Met � jour la t�che d'identifiant id avec les valeurs donn�es en param�tre
	 * @param String description
	 * @param Long assign
	 * @param Date date
	 * @param String column
	 * @param Long id
	 * @throws DAOException
	 */
	public void updateTask(String description, Long assign, Date date, String column, Long id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_UPDATE_BY_ID,
					false,
					description, assign, date, column, id);
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Updating task failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	/**
	 * Met � jour l'assignation de la t�che d'identifiant id
	 * @param Long assign
	 * @param Long id
	 * @throws DAOException
	 */
	public void updateAssignTask(Long assign, Long id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_UPDATE_ASSIGN_BY_ID,
					false,
					assign, id);
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Updating task failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	// OUTILS
	
	/**
	 * M�thode priv�e pour r�cup�rer le prochain id de la table Task
	 * @return
	 * @throws DAOException
	 */
	private Long getNextId() throws DAOException{
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
				throw new DAOException("Error while loading max id inside tasks table.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * M�thode priv�e permettant d'analyser le r�sultat d'une requ�te contenant les donn�es d'une t�che
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	protected static Task map(ResultSet resultSet) throws SQLException {
		Task task = new Task();
		task.setId(resultSet.getLong("id"));
		task.setIdKanban(resultSet.getLong("id_kanban"));
		task.setDescription(resultSet.getString("description"));
		task.setIdAssign(resultSet.getLong("id_assign"));
		task.setDate(resultSet.getDate("max_date"));
		task.setNameColumn(resultSet.getString("name_column"));
		
		return task;
	}
	
}
