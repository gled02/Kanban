package dao;

/**
 * Classe d'exception permettant de spécifier une exception lié à la configuration
 * 		d'accès à la base de données
 */
public class DAOConfigurationException extends RuntimeException {
   
	// ATTRIBUTS 
	
	private static final long serialVersionUID = 1047828793942551100L;

	// CONSTRUCTEURS
	
    public DAOConfigurationException(String message) {
        super(message);
    }

    public DAOConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOConfigurationException(Throwable cause) {
        super(cause);
    }
}
