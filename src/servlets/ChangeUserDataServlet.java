package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DAOException;
import dao.DAOFactory;
import dao.UserDAO;
import domain.User;

/**
 * Servlet permettant de changer les données d'un utilisateur.
 * Cette page se trouve à l'URL /changeUserData et est associé à la vue /WEB-INF/views/changeUserData.jsp.
 * Cette page n'est accessible que si la variable de session 'user' est définie, c'est-à-dire si l'utilisateur
 * 		est connecté.
 * Redirige vers la page de connexion si l'utilisateur n'est pas connecté, vers la page de configuration
 * 		si la base de données n'est pas configurée ou vers la page /profile une fois que l'utilisateur a validé 
 * 		le formulaire et met à jour la variable de session 'user'.
 */
@WebServlet("/changeUserData")
public class ChangeUserDataServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
	
	// Attribut qui contient le lien vers la page .jsp qui correspond à la servlet.
	private static final String VUE		 	= "/WEB-INF/views/changeUserData.jsp";
	
	// Attribut qui permet d'avoir accès à la table USER.
	private UserDAO userDao;

	// COMMANDES
    
	/**
	 * Méthode appelée par le serveur pour traiter la requête de type post dans la page changeUserData.jsp.
	 * Cette méthode permet de changer les données d'un utilisateur connecté. Si l'utilisateur n'est pas connecté, 
	 * 		il est dirigé vers la page de login. Si la base de données n'est pas configurée, l'utilisateur est dirigé
	 * 		vers la page de configuration. L'attribut infoUpdateUser est défini pour afficher les erreurs.
	 * Les données vont changer dans le cas où l'utilisateur a rentré quelque chose
	 * 		dans les champs du formulaire et la variable de session est mise à jour.
	 * @pre <pre>
	 * 		DAOFactory.dbIsValidate()
	 * 		session.getAttribute("user") != null </pre>
     * @post <pre>
     * 		si request.getParameter("name") != null && !request.getParameter("name").equals("")
     * 			user.getName() == request.getParameter("name")
     * 		si request.getParameter("username") != null && !request.getParameter("username").equals("")
     * 			user.getUsername() == request.getParameter("username")
     * 		si request.getParameter("surname") != null && !request.getParameter("surname").equals("")
     * 			user.getSurname() == request.getParameter("surname")
     * 		si request.getParameter("password") != null && !request.getParameter("password").equals("")
     * 			user.getPassword = encodePassword(user.getUsername(), request.getParameter("password") </pre>
     * 		userDao.updateUser(name, surname, password, username, id)
     * 		session.setAttribute("user", user)
     * 		response.sendRedirect(request.getContextPath() + "/profile")
     * @exception DAOException <pre>
     * 		si problème coté base de données -> méthode doGet appelée 
     * 		si problème avec l'encodage du mot de passe -> méthode doGet appelée </pre>	
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();
			HttpSession session = request.getSession();
		    if (session.getAttribute("user") == null) {
		    	response.sendRedirect(request.getContextPath() + "/login");
		    } else {
		    	request.setCharacterEncoding("UTF-8");
		    	User user = (User) session.getAttribute("user");
		    	Long id = user.getId();
				String name = user.getName();
				String surname = user.getSurname();
				String username = user.getUsername();
				String password = user.getPassword();
				
				if (request.getParameter("name") != null && !request.getParameter("name").equals("")) {
					name = request.getParameter("name");
					user.setName(name);
				}
				
				if (request.getParameter("username") != null && !request.getParameter("username").equals("")) {
					username = request.getParameter("username");
					user.setUsername(username);
				}
				
				if (request.getParameter("surname") != null && !request.getParameter("surname").equals("")) {
					surname = request.getParameter("surname");
					user.setSurname(surname);
				}
				
				if (request.getParameter("password") != null && !request.getParameter("password").equals("")) {
					try {
						password = userDao.encodePassword(username, request.getParameter("password"));
						user.setPassword(password);
					} catch (DAOException e) {
			            request.setAttribute("infoUpdateUser", e);
			            doGet(request, response);
					}
				}
				try {
					userDao.updateUser(name, surname, password, username, id);
					session.setAttribute("user", user);
					response.sendRedirect(request.getContextPath() + "/profile");
				} catch (DAOException e) {
					request.setAttribute("infoUpdateUser", "Updating user failed !");
					doGet(request, response);
				}
		    }
		}
	}
	
	/**
	 * Méthode appelée par le serveur pour traiter la requête de type get dans la page changeUserData.jsp.
	 * Redirige vers la page de connexion si l'utilisateur n'est pas connecté, vers la page de configuration
	 * 		si la base de données n'est pas configurée ou vers la page /changeUserData.jsp si l'utilisateur est
	 * 		connecté et si la base de données est configurée.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();

			HttpSession session = request.getSession();
		    if (session.getAttribute("user") == null) {
		    	response.sendRedirect(request.getContextPath() + "/login");
		    } else {
		    	request.setCharacterEncoding("UTF-8");
		    	this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
		    }
		}
	}
}
