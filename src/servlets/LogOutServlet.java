package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.DAOFactory;

/**
 * Servlet permettant à un utilisateur de se déconnecter.
 * Cette page se trouve à l'URL /logout et elle est associée à aucune vue.
 * Redirige vers la page de configuration si la base de données n'est pas configurée, 
 * 		sinon détruit les variables de session et redirige vers la page d'accueil.
 */
@WebServlet("/logout")
public class LogOutServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
    
    // COMMANDES
    
	/**
	 * Méthode appelée par le serveur pour traiter la requête de type post.
	 * Cette méthode permet à un utilisateur de se déconnecter. Si la base de données n'est pas configurée, 
	 * 		l'utilisateur est dirigé vers la page de configuration.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			doGet(request, response);
		}
	}

	/**
	 * Méthode appelée par le serveur pour traiter la requête de type get.
	 * Redirige vers la page de configuration si la base de données n'est pas configurée ou vers 
	 * 		la page d'accueil sinon en détruisant avant les variables de session.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
	        HttpSession session = request.getSession();
	        if (session != null) {
	            session.invalidate();
	        }
	        response.sendRedirect(request.getContextPath() + "/welcomePage");
		}
	}
}
