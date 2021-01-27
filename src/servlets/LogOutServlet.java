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
 * Servlet permettant � un utilisateur de se d�connecter.
 * Cette page se trouve � l'URL /logout et elle est associ�e � aucune vue.
 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e, 
 * 		sinon d�truit les variables de session et redirige vers la page d'accueil.
 */
@WebServlet("/logout")
public class LogOutServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
    
    // COMMANDES
    
	/**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type post.
	 * Cette m�thode permet � un utilisateur de se d�connecter. Si la base de donn�es n'est pas configur�e, 
	 * 		l'utilisateur est dirig� vers la page de configuration.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			doGet(request, response);
		}
	}

	/**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type get.
	 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e ou vers 
	 * 		la page d'accueil sinon en d�truisant avant les variables de session.
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
