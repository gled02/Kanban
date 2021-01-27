package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import domain.User;

/**
 * Objet permettant d'envoyer des requêtes à la base données, sur la table User
 * 	Cette table correspondant à l'ensemble des utilisateurs enregistrés dans la base de donnée
 * @cons <pre>
 *     $DESC$ UN objet utilisant factory afin d'initialiser les connexions à la base de donnée
 *     $ARGS$ DAOFactory factory
 *     $PRE$
 *         DAOFactory.dbIsValidate() </pre>
 */
public class UserDAO {
	
	// ATTRIBUTS 

	// Requête qui sélectionne le plus grand identifiant dans table User
	private static final String SQL_MAX_ID 						= String.format("SELECT max(id) FROM %s;"												, DAOFactory.TABLE_USER);

	// Requête qui sélectionne l'utilisateur d'identifiant dans la table User
	private static final String SQL_SELECT_BY_ID 				= String.format("SELECT * FROM %s WHERE id=?;"											, DAOFactory.TABLE_USER);

	// Requête qui sélectionne l'utilisateur à partir de son username et son password dans la table User
	private static final String SQL_SELECT_BY_USER_PASSWORD 	= String.format("SELECT * FROM %s WHERE username=? AND passwd=?;"						, DAOFactory.TABLE_USER);

	// Requête qui sélectionne l'identifiant d'un utilisateur dans la table User
	private static final String SQL_SELECT_ID_USER				= String.format("SELECT id FROM %s WHERE username=?"									, DAOFactory.TABLE_USER);

	// Requête qui sélectionne le mot de passe enregistré pour un utilisateur dans la table User
	private static final String SQL_SELECT_PWD_USER				= String.format("SELECT passwd FROM %s WHERE username=?"								, DAOFactory.TABLE_USER);
	
	// Requête qui insert une ligne dans la table User
	private static final String SQL_INSERT 						= String.format("INSERT INTO %s VALUES (?, ?, ?, ?, ?);"								, DAOFactory.TABLE_USER);
	
	// Requête qui met à jour les informations d'un utilisateur dans la table User
	private static final String SQL_UPDATE_BY_ID				= String.format("UPDATE %s SET username=?, passwd=?, name=?, surname=?  WHERE id=?"		, DAOFactory.TABLE_USER);

	// Algorithme de hachage utilisé pour le stockage des mots de passe
	private static final String DIGEST_ALGO = "SHA-256";
	private DAOFactory factory;
	
	// COMMANDES

	
	/**
	 * Contructeur de l'objet, prend en paramètre un DAOFactory uniquement si DAOFactory.dbIsValidate()
	 * @param factory
	 */
	UserDAO(DAOFactory factory) {
		this.factory = factory;
	}
	
	// REQUETES
	
	/**
	 * Requête renvoyant l'utilisateur d'identifiant id
	 * @param Long id
	 * @return User user : user.getId() == id
	 * @throws DAOException
	 */
	public User getUser(Long id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		User user = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_BY_ID, 
					false,
					id);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				user = map(resultSet);
			} else {
				throw new DAOException("Any user corresponding to id='" + id + "'.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return user;
	}

	/**
	 * Requête récupérant l'utilisateur de surnom username et de mot de passe password
	 * @param String username
	 * @param String password
	 * @return User user : user.getUsername() == usernale && user.getPassword() == password
	 * @throws DAOException
	 */
	public User getUser(String username, String password) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		User user = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_BY_USER_PASSWORD, 
					false,
					username, password);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				user = map(resultSet);
			} else {
				throw new DAOException("Any rows selected with name '" + username + "'.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return user;
	}
	
	/**
	 * Requête permettant de récupérer l'identifiant de l'utilisateur de nom name
	 * @param String name
	 * @return User user : user.getUsername() == name
	 * @throws DAOException
	 */
	public Long getUserId(String name) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Long id = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_ID_USER, 
					false,
					name);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				id = resultSet.getLong("id");
			} else {
				throw new DAOException("This user does not exist !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return id;
	}
	
	/**
	 * Requête renvoyant le mot de passe de l'utilisateur username
	 * @param String username
	 * @return getUser(username, getUserPassword(username)) != null
	 * @throws DAOException
	 */
	public String getUserPassword(String username) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String password = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_PWD_USER, 
					false,
					username);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				password = resultSet.getString("passwd");
			} else {
				throw new DAOException("This user does not exist !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return password;
	}
	
	/**
	 * Requête renvoyant vrai si l'utilisateur username existe dans la base de données
	 * @param String username
	 * @return vrai ssi getUserId(username) != null
	 * @throws DAOException
	 */
	public boolean userExist(String username) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean exist = false;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_SELECT_ID_USER, 
					false,
					username);
			
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				exist = true;
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
		return exist;
	}

	/**
	 * Requête permettant de renvoyer le mot de passe hashé avec du sel
	 * @param String username
	 * @param String passwordToHash
	 * @return
	 * @throws DAOException
	 */
    public String encodePassword(String username, String passwordToHash) throws DAOException {
        String saltString = generateSalt(12);
        passwordToHash = saltString + passwordToHash;
        
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGO);
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i< bytes.length; i++)
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
           	throw new DAOException("Instance of '" + DIGEST_ALGO + "' does not exist for MessageDigest");
        }
        return saltString + generatedPassword;
    }
    
    /**
     * Requête permettant de vérifier si le hachage de passwordToHash correspond au hachage du mot de passe
     * 	de l'utilisateur username, en utilisant le même seul contenu dans le mot de passe du l'utilisateur
     * @param String username
     * @param String passwordToHash
     * @return getUserPasswor(username) == encodePassword(username, passwordToHash)
     * @throws DAOException
     */
    public String verifyPassword(String username, String passwordToHash) throws DAOException  {
    	String storedPassword = null;
    	try {
        	storedPassword = getUserPassword(username);
    	} catch (DAOException e) {
    		throw e;
    	}
    	String salt = storedPassword.substring(0, 12);
    	passwordToHash = salt + passwordToHash;

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGO);
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i< bytes.length; i++)
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
           	throw new DAOException("Instance of '" + DIGEST_ALGO + "' does not exist for MessageDigest");
        }
        if (!(salt + generatedPassword).equals(storedPassword)) {
        	return null;
        }
        return storedPassword;
    }
    
    /**
     * Requête privée renvoyant un sel aléatoire de taille size
     * @param int size
     * @return length(generateSalt(size)) == size
     */
    private String generateSalt(int size) {
    	final String HEXA = "0123456789abcdef";
    	String salt = "";
    	for (int i = 0; i < size; ++i) {
    		salt += HEXA.charAt(new Random().nextInt(16));
    	}
    	
    	return salt;
    }
    
	// COMMANDES

	/**
	 * Commande permettant de rajouter l'utilisateur dans la base de données
	 * @param User user
	 * @throws DAOException
	 */
	public void createUser(User user) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			Long maxId = getNextId() + 1;
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_INSERT,
					false,
					maxId, user.getUsername(), user.getPassword(), user.getName(), user.getSurname());
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Creating user failed !");
			}
			user.setId(maxId);
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
		
	}

	/**
	 * Commande permettant de mettre à jour les données de l'utilisateur d'identifiant id
	 * @param User user
	 * @throws DAOException
	 */
	public void updateUser(String name, String surname, String passwd, String username, Long id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					SQL_UPDATE_BY_ID,
					false,
					username, passwd, name, surname, id);
			
			int status = preparedStatement.executeUpdate();
			if (status == 0) {
				throw new DAOException("Updating user failed !");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(preparedStatement, connection);
		}
	}
	
	// OUTILS

	/**
	 * Méthode privée pour récupérer le prochain id de la table User
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
				throw new DAOException("Error while loading max id inside users table.");
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
	}

	/**
	 * Méthode privée permettant d'analyser le résultat d'une requête contenant les données d'un utilisateur
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	protected static User map(ResultSet resultSet) throws SQLException {
		User user = new User();
		user.setId(resultSet.getLong("id"));
		user.setUsername(resultSet.getString("username"));
		user.setName(resultSet.getString("name"));
		user.setSurname(resultSet.getString("surname"));
		user.setPassword(resultSet.getString("passwd"));
		
		return user;
	}
}
