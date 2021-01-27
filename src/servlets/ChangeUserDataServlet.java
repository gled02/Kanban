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
 * Servlet permettant de changer les donn�es d'un utilisateur.
 * Cette page se trouve � l'URL /changeUserData et est associ� � la vue /WEB-INF/views/changeUserData.jsp.
 * Cette page n'est accessible que si la variable de session 'user' est d�finie, c'est-�-dire si l'utilisateur
 * 		est connect�.
 * Redirige vers la page de connexion si l'utilisateur n'est pas connect�, vers la page de configuration
 * 		si la base de donn�es n'est pas configur�e ou vers la page /profile une fois que l'utilisateur a valid� 
 * 		le formulaire et met � jour la variable de session 'user'.
 */
@WebServlet("/changeUserData")
public class ChangeUserDataServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
	
	// Attribut qui contient le lien vers la page .jsp qui correspond � la servlet.
	private static final String VUE		 	= "/WEB-INF/views/changeUserData.jsp";
	
	// Attribut qui permet d'avoir acc�s � la table USER.
	private UserDAO userDao;

	// COMMANDES
    
	/**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type post dans la page changeUserData.jsp.
	 * Cette m�thode permet de changer les donn�es d'un utilisateur connect�. Si l'utilisateur n'est pas connect�, 
	 * 		il est dirig� vers la page de login. Si la base de donn�es n'est pas configur�e, l'utilisateur est dirig�
	 * 		vers la page de configuration. L'attribut infoUpdateUser est d�fini pour afficher les erreurs.
	 * Les donn�es vont changer dans le cas o� l'utilisateur a rentr� quelque chose
	 * 		dans les champs du formulaire et la variable de session est mise � jour.
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
     * 		si probl�me cot� base de donn�es -> m�thode doGet appel�e 
     * 		si probl�me avec l'encodage du mot de passe -> m�thode doGet appel�e </pre>	
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
	 * M�thode appel�e par le serveur pour traiter la requ�te de type get dans la page changeUserData.jsp.
	 * Redirige vers la page de connexion si l'utilisateur n'est pas connect�, vers la page de configuration
	 * 		si la base de donn�es n'est pas configur�e ou vers la page /changeUserData.jsp si l'utilisateur est
	 * 		connect� et si la base de donn�es est configur�e.
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
