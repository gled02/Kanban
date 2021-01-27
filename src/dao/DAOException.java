package dao;

/**
 * Classe d'exception permettant de spécifier une exception lors de la
 * 		manipulation de connexion et de requête à la base de données
 */
public class DAOException extends RuntimeException {
    
	// ATTRIBUTS 
	
	private static final long serialVersionUID = -6766320787209749020L;

	// CONSTRUCTEURS
	
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }
}
