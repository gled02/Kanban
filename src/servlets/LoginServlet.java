package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import domain.User;
import dao.UserDAO;
import dao.DAOException;
import dao.DAOFactory;

/**
 * Servlet permettant � un utilisateur de se connecter.
 * Cette page se trouve � l'URL /login et est associ� � la vue /WEB-INF/views/login.jsp.
 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e ou 
 * 		vers la page /profile une fois que l'utilisateur a valid� le formulaire et cr�e la
 * 		variable de session 'user'.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = -6048149517101668716L;

	// Attribut qui contient le lien vers la page .jsp qui correspond � la servlet.
	private static final String VUE		 = "/WEB-INF/views/login.jsp";
	
	// Attribut qui permet d'avoir acc�s � la table USER.
	private UserDAO userDao;
	
	// COMMANDES
	
	/**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type post dans la page login.jsp.
	 * Cette m�thode permet � un utilisateur de se connecter. Si la base de donn�es n'est pas configur�e, 
	 * 		l'utilisateur est dirig� vers la page de configuration. L'attribut infoLogin est d�fini pour afficher
	 * 		les erreurs.
	 * @pre <pre>
	 * 		DAOFactory.dbIsValidate() </pre>
     * @post <pre>
     * 		userDao.getUser(request.getParameter("username"), request.getParameter("password"))
     * 		session.setAttribute("user",  user)
     * 		response.sendRedirect(request.getContextPath() + "/profile") </pre>
     * @exception DAOException <pre>
     * 		si probl�me cot� base de donn�es -> m�thode doGet appel�e </pre>	
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();

			request.setCharacterEncoding("UTF-8");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			try {
				password = userDao.verifyPassword(username, password);
				if (password == null) {
		            request.setAttribute("infoLogin", "Data in login form was invalid, the password is not correct !");
		            doGet(request, response);
				} else {
					User user = userDao.getUser(username, password);
					HttpSession session = request.getSession();
					session.setAttribute("user",  user);
		            response.sendRedirect(request.getContextPath() + "/profile");
				}
			} catch (DAOException e) {
				request.setAttribute("infoLogin", "Error : " + e);
				doGet(request, response);
			}
		}
	}

	/**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type get dans la page login.jsp.
	 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e ou vers 
	 * 		la page /login.jsp sinon.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();
			request.setCharacterEncoding("UTF-8");
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
		}
	}
}
