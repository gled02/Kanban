package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Classe permettant de gérer les connexions et la configuration à la base
 * 		de données
 *	Spécifie l'ensemble des noms, attributs et contraintes des tables
 *		contenus dans la base de données, en vérifiant leurs présences
 *		au démarrage de l'application. Si elles ne sont pas présentes, 
 *		alors cette classe crée les tables par elle-même
 */
public class DAOFactory {
	
	/*
			--------- INFORMATIONS DES TABLES UTILISEES ---------
	*/
	
	// Liste des tables
	// Table des utilisateurs 
	public static final String TABLE_USER			  = "users";
	// Table contenant la liste des kanbans
	public static final String TABLE_KANBAN			  = "kanbans";
	// Table contenant la liste des tasks
	public static final String TABLE_TASK			  = "tasks";
	// Table des données des invitations
	public static final String TABLE_GUEST			  = "guests";
	// Taille des descriptions
	public static final String SIZE_TEXTAREA		  = "300";
	
	// Information des colonnes des tables
	//		Chaque colonne est défini tel qu'un nom et un type
	//		Si type == VARCHAR, alors il faut définir une taille

	private static final String[][] TABLE_USER_ROWS	  = {
			{"id", 				"INT"}, 					// Spécifique à la manipulation interne des données
			{"username", 		"VARCHAR",		"32"}, 		// Pseudo des uitlisateurs
			{"passwd", 			"VARCHAR",		"512"},
			{"name",			"VARCHAR",		"32"},
			{"surname",			"VARCHAR",		"32"}
	};
	private static final String[][] TABLE_KANBAN_ROWS = {
			{"id", 				"INT"},						// Spécifique à la manipulation interne des données
			{"id_owner", 		"INT"}, 
			{"name_kanban", 	"VARCHAR",		"32"},
			{"columns",			"VARCHAR",		"256"},
			{"is_public", 		"BOOLEAN"},
			{"description",		"VARCHAR",		SIZE_TEXTAREA}
	};
	private static final String[][] TABLE_TASK_ROWS = {
			{"id", 				"INT"},						// Spécifique à la manipulation interne des données
			{"id_kanban",		"INT"},
			{"description", 	"VARCHAR",		SIZE_TEXTAREA},
			{"id_assign",		"INT"},
			{"max_date",		"DATE"},
			{"name_column",		"VARCHAR", 		"32"}
	};
	private static final String[][] TABLE_GUEST_ROWS = {
			{"id",				"INT"},
			{"id_kanban",		"INT"},
			{"id_user",			"INT"}
	};

	// Contraintes des tables
	private static final String[] CONS_USER_PK = {
			"ALTER TABLE users ADD CONSTRAINT PK_USER PRIMARY KEY(id, username)"
	};
	private static final String[] CONS_KANBAN_PK = {
			"ALTER TABLE kanbans ADD CONSTRAINT PK_KANBAN PRIMARY KEY(id, id_owner, name_kanban)",
			"ALTER TABLE kanbans ADD FOREIGN KEY FK_KANBAN_OWNER (id_owner) REFERENCES users(id)"
	};
	private static final String[] CONS_TASK_PK = {
			"ALTER TABLE tasks ADD CONSTRAINT PK_TASK PRIMARY KEY(id)",
			"ALTER TABLE tasks ADD FOREIGN KEY FK_TASK_KABAN (id_kanban) REFERENCES kanbans(id)",					// TODO modifier le nom de la contrainte en FK_TASK_KANBAN
			"ALTER TABLE tasks ADD FOREIGN KEY FK_TASK_OWNER (id_assign) REFERENCES users(id)"
	};
	private static final String[] CONS_GUEST_PK = {
			"ALTER TABLE guests ADD CONSTRAINT PK_GUEST PRIMARY KEY(id)",
			"ALTER TABLE guests ADD FOREIGN KEY FK_GUEST_KANBAN (id_kanban) REFERENCES kanbans(id)",
			"ALTER TABLE guests ADD FOREIGN KEY FK_GUEST_USER (id_user) REFERENCES users(id)"
	};
	
	// Liste des tables 
	//		Etant défini tel qu'un nom, les informations des colonnes, et des contraintes
	private static final Object[][] LIST_TABLES		  = {
			{TABLE_USER, 	TABLE_USER_ROWS, 	CONS_USER_PK}, 
			{TABLE_KANBAN, 	TABLE_KANBAN_ROWS,	CONS_KANBAN_PK},
			{TABLE_TASK, 	TABLE_TASK_ROWS,	CONS_TASK_PK},
			{TABLE_GUEST, 	TABLE_GUEST_ROWS,	CONS_GUEST_PK}
	};
	
	private static final boolean VERIFY_TABLE = true;
	
	/*
			--------- INFORMATIONS DU DAOFACTORY FILE --------- 
	*/

	// nom de l'attribut dans les servlets contenant le DAOFactory
	public static final String NAME_ATT 			  = "daofactory";
	
	// Propriété du fichier dao
	public static final String FILE_PROP			  = "/dao/dao.properties";
	private static final String PROP_URL			  = "url";
	private static final String PROP_DRIVER			  = "driver";
	private static final String PROP_USER			  = "user";
	private static final String PROP_PASSWORD		  = "password";
	private static boolean is_validate 				  = false;
	
	/*
			--------- INFORMATIONS DE L'OBJET DAOFACTORY --------- 
	*/
	
	private String url;
	private String user;
	private String password;
	
	DAOFactory(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
    
	/**
	 * Renvoie un objet DAO pour les utilisateurs
	 * @return {@link dao.UserDAO}
	 */
	public UserDAO getUtilisateurDao() {
		return new UserDAO(this);
	}
    
	/**
	 * Renvoie un objet DAO pour les kanbans
	 * @return {@link dao.KanbanDAO}
	 */
	public KanbanDAO getKanbanDao() {
		return new KanbanDAO(this);
	}

	/**
	 * Renvoie un objet DAO pour les taches
	 * @return {@link dao.TaskDAO}
	 */
	public TaskDAO getTaskDao() {
		return new TaskDAO(this);
	}

	/**
	 * Renvoie un objet DAO pour les invitations
	 * @return {@link dao.TaskDAO}
	 */
	public GuestDAO getGuestDao() {
		return new GuestDAO(this);
	}
	
	/**
	 * Renvoie vrai uniquement si la configuration d'accès à la base
	 * 	de données à été correctement faite, et validé
	 */
	public static boolean dbIsValidate() {
		return DAOFactory.is_validate;
	}
	
	/**
	 * Permet de valider la configuration à la base de donnée
	 * TODO : la vérification que la configuration d'accès est correcte doit être faite
	 * 	ici
	 */
	public static void setValidationdb(boolean bool) {
		//if (DAOFactory.verifyConfiguration()) {
			DAOFactory.is_validate = bool;
			if (bool) {
		    	try {
		    		if (VERIFY_TABLE) {
		    			DAOFactory instance = DAOFactory.getInstance();
		            	verifyTables(instance);
		    		}
		    	} catch (DAOConfigurationException e) {
		    		throw e;
		    	}
			}
		//}
	}
	
	/**
	 * Méthode de vérification de la bonne configuration d'accès à la base de données
	 */
	public static boolean verifyConfiguration() {
    	// Properties permet de lire un fichier contenant des lignes sous la forme
    	//		key = value
    	//	et permet de récupérer par la suite les propriétés à l'aide de getProperty(key) 
    	Properties properties = new Properties();
    	String fileUrl;
    	String fileUser;
    	String filePassword;
    	
    	// On charge le fichier dans un InputStream pour éviter de devoir donner le
    	//		chemin absolu et gérer une exception
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream fileProp = classLoader.getResourceAsStream(FILE_PROP);
    	if (fileProp == null) {
    		return false;
    	}
    	
    	try {
    		properties.load(fileProp);
    		fileUrl = properties.getProperty(PROP_URL);
    		fileUser = properties.getProperty(PROP_USER);
    		filePassword = properties.getProperty(PROP_PASSWORD);
        	return verifyConfiguration(
        			fileUrl.split("//")[1].split("/")[1],
        			fileUrl.split("//")[1].split(":")[0],
        			fileUser,
        			filePassword);
    	} catch (IOException e) {
    		return false;
    	}
	}

	/**
	 * Méthode de vérification de la bonne configuration d'accès à la base de données
	 */
	public static boolean verifyConfiguration(String scheme, String adress, String user, String password) {

    	// On crée une instance de l'accès à la base de donnée et on vérifie que les tables
    	//	sont bien présentes.
    	DAOFactory instance = new DAOFactory("jdbc:mysql://" + adress + ":3306/" + scheme, user, password);
    	try {
    		instance.getConnection();
    	} catch (SQLException e) {
    		return false;
    	}
		return true;
	}
	
    /**
     * Méthode chargée de récupérer les informations de connexion à la base de
     * données, charger le driver JDBC et retourner une instance de la Factory
     * @return {@link dao.DAOFactory}
     * @throws DAOConfigurationException
     */
    public static DAOFactory getInstance() throws DAOConfigurationException {
    	
    	// Properties permet de lire un fichier contenant des lignes sous la forme
    	//		key = value
    	//	et permet de récupérer par la suite les propriétés à l'aide de getProperty(key) 
    	Properties properties = new Properties();
    	String fileUrl;
    	String fileDriver;
    	String fileUser;
    	String filePassword;
    	
    	// On charge le fichier dans un InputStream pour éviter de devoir donner le
    	//		chemin absolu et gérer une exception
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	InputStream fileProp = classLoader.getResourceAsStream(FILE_PROP);
    	if (fileProp == null) {
    		throw new DAOConfigurationException("File '" + FILE_PROP + "' not founded.");
    	}
    	
    	try {
    		properties.load(fileProp);
    		fileUrl = properties.getProperty(PROP_URL);
    		fileDriver = properties.getProperty(PROP_DRIVER);
    		fileUser = properties.getProperty(PROP_USER);
    		filePassword = properties.getProperty(PROP_PASSWORD);
    	} catch (IOException e) {
    		throw new DAOConfigurationException("Cannot load file '" + FILE_PROP + "'.", e);
    	}
    	
    	// On charge le driver permettant de se connecter à la base de donnée
    	try {
    		Class.forName(fileDriver);
    	} catch (ClassNotFoundException e) {
    		throw new DAOConfigurationException("Driver not found in classpath.", e);
    	}

    	// On crée une instance de l'accès à la base de donnée et on vérifie que les tables
    	//	sont bien présentes.
    	DAOFactory instance = new DAOFactory(fileUrl, fileUser, filePassword);
		return instance;
    }
    
    /**
     * Renvoie une connexion à la base de donnée.
     * @return {@link Connection}
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
    	return DriverManager.getConnection(url, user, password);
    }
    
    /**
     * Méthode privée vérifiant que la base de donnée contient bien les tables.
     * @param factory
     * @throws DAOConfigurationException
     */
    private static void verifyTables(DAOFactory factory) throws DAOConfigurationException {
    	try {
        	Connection connection = factory.getConnection();
        	DatabaseMetaData meta = connection.getMetaData();
        	for (Object[] dataTable : LIST_TABLES) {
        		
        		// dataColumns[.][0] correspond au nom  de la colonne
        		// dataColumns[.][1] correspond au type de la colonne
        		// dataColumns[.][2] correspond à la taille du varchar
        		String nameTable 		= (String) 	   dataTable[0];
        		String[][] dataColumns 	= (String[][]) dataTable[1];
        		String[] consTable		= (dataTable.length == 3 ? (String[]) dataTable[2] : null);
        		
        		// Récupère les métadonnées de la table
            	ResultSet table = meta.getTables(null, null, nameTable, null);
            	
            	if (!table.next()) {
            		// Si la table n'existe pas, on la créer
            		
            		createTable(nameTable, dataColumns, consTable, factory);
            	} else {
            		// Si la table existe, on vérifie que les colonnes sont les bonnes
            		
            		// On récupère les données des colonnes existantes
            		ResultSet columns = meta.getColumns(null, null, nameTable, null);
            		int index = 0;
            		while(columns.next()) {
            			
            			// Vérifie le nom de la colonne
            			if (!columns.getString("COLUMN_NAME").equals(dataColumns[index][0])) {
            				break;
            			}
            			
            			// Vérifie le type de la colonne
            			if (!columns.getString("TYPE_NAME").equals(dataColumns[index][1])) {
            				if (!(columns.getString("TYPE_NAME").equals("BIT") &&
            						columns.getInt("COLUMN_SIZE") == 1 && 
            						dataColumns[index][1].equals("BOOLEAN"))) {
                				break;
            				}
            			}
            			
            			// Lorsque le type est VARCHAR, vérifie que la taille est bonne
            			if (columns.getString("TYPE_NAME").equals("VARCHAR") &&
            					!Integer.toString(columns.getInt("COLUMN_SIZE")).equals(dataColumns[index][2])) {
            				break;
            			}
            			++index;
            		}
            		
            		// Si le nombre de colonne correspondante est différent du nombre de colonne nécessaire, alors
            		//		les colonnes ne correspondent pas aux paramètres de la table
            		if (index != dataColumns.length) {
            			throw new DAOConfigurationException("Table " + nameTable + "does not contains the right columns " + index);
            		}
            	}
        	}
    	} catch (SQLException e) {
    		throw new DAOConfigurationException("Cannot verify tables required !" + e);
    	}
    }
	
    /**
	 * Méthode privée permettant de créer en table de nom 'name' et de colonnes columns
	 * 	columns doit être un tableau de tableau contenant un nom, un type, et sa taille si il s'agit d'un VARCHAR
	 * 	constraint permet de définir une contrainte
	 * @param name nom de la table
	 * @param columns les données des colonnes (nom, type, taille)
	 * @param constraints les alterations de la table (contraintes)
	 * @param factory l'objet contenant la connexion à la bdd
	 * @throws DAOException
	 */
    // 
    private static void createTable(String name, String[][] columns, String[] constraints, DAOFactory factory) throws DAOException {
    	Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			String request = String.format("CREATE TABLE %s(", name);
			if (columns[0][1].equals("VARCHAR")) {
				request += String.format("%s VARCHAR(%s)", columns[0][0], columns[0][2]);
			} else {
				request += String.format("%s %s", columns[0][0], columns[0][1]);
			}
			for (int i = 1; i < columns.length; ++i) {
				if (columns[i][1].equals("VARCHAR")) {
					request += String.format(", %s VARCHAR(%s)", columns[i][0], columns[i][2]);
				} else {
					request += String.format(", %s %s", columns[i][0], columns[i][1]);
				}
			}
			request += ");";
			
			// Crée la table
			connection = factory.getConnection();
			preparedStatement = DAOFactory.initializePreparedRequest(
					connection, 
					request,
					false);
			
			preparedStatement.execute();
			DAOFactory.close(preparedStatement);
			
			// Ajoute les contraintes
			if (constraints != null) {
				for (String constraint : constraints) {
					preparedStatement = DAOFactory.initializePreparedRequest(
							connection, 
							constraint,
							false);
					preparedStatement.execute();
					DAOFactory.close(preparedStatement);
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			DAOFactory.close(resultSet, preparedStatement, connection);
		}
    }
	
	/* 			METHODES STATIC EXTERNES			*/
	
	public static PreparedStatement initializePreparedRequest(Connection connexion, String sql,
			boolean returnGeneratedKeys, Object... objets) throws SQLException {
		PreparedStatement preparedStatement = connexion.prepareStatement(sql, 
				returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		for (int i=0; i < objets.length; ++i) {
			preparedStatement.setObject(i + 1, objets[i]);
		}
		
		return preparedStatement;
	}
	
	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				System.out.println("Closing resultSet failed : " + e.getMessage());
			}
		}
	}
	
	public static void close(Connection connection) {
		if (connection  != null) {
			try {
				connection .close();
			} catch (SQLException e) {
				System.out.println("Closing connection failed : " + e.getMessage());
			}
		}
	}
	
	public static void close(Statement statement) {
		if (statement  != null) {
			try {
				statement .close();
			} catch (SQLException e) {
				System.out.println("Closing statement failed : " + e.getMessage());
			}
		}
	}
	
	public static void close(Statement statement, Connection connection) {
		close(statement);
		close(connection);
	}
	
	public static void close(ResultSet resultSet, Statement statement, Connection connection) {
		close(resultSet);
		close(connection);
		close(connection);
	}
}
