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
 * Servlet permettant de gérer la création d'un compte utilisateur
 * Cette page ce trouve à l'url /registration et est associé à la vue /WEB-INF/views/registration.jsp
 */
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = -771795245773946656L;

	private static final String VUE		 = "/WEB-INF/views/registration.jsp";
	
	private UserDAO userDao;

	// COMMANDES

	/**
	 * Commande appelé lorsque la page reçoit des informations à l'aide de la méthode POST
	 * Cela arrivera lorsque l'utilisateur confirmera la création de son compte.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();
			request.setCharacterEncoding("UTF-8");
			String username = request.getParameter("username");
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String password = request.getParameter("password");
			
			// Dans un premier temps on vérifie si l'utilisateur de nom username existe déjà
			//	Si c'est le cas on affiche un message d'erreur
			try {
				if (userDao.userExist(username)) {
					request.setAttribute("infoRegistration", "Username: " + username + " already exist.");
					doGet(request, response);
				} else {
					
					// Si l'utilisateur n'existe pas, on hash le mot de passe
					password = userDao.encodePassword(username, password);
					if (password == null) {
						request.setAttribute("infoRegistration", "Password error");
						doGet(request, response);
					} else {
						
						// Ensuite on stocke le bean correspondant à l'utilisateur dans la session et on ajoute
						// 		cette utilisateur dans la base de données
						// Pour finir on le redirige vers sa page profile
						User user = new User();
						user.setUsername(username);
						user.setName(name);
						user.setSurname(surname);
						user.setPassword(password);
						userDao.createUser(user);
						HttpSession session = request.getSession();
						session.setAttribute("user",  user);
						response.sendRedirect(request.getContextPath() + "/profile");
					}
				}
			} catch (DAOException e) {
	            request.setAttribute("infoRegistration", "Registration failed" + e);
	            doGet(request, response);
			}
		}
	}

	/**
	 * Commande appelé lorsqu'une personne souhaite récupérer le contenu de la page /registration à l'aide de la méthode GET
	 * Elle sera appelé lorsqu'un utilisateur souhaite créer un compte
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			request.setCharacterEncoding("UTF-8");
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
		}
	}
}
