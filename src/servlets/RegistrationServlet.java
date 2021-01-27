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
 * Servlet permettant de g�rer la cr�ation d'un compte utilisateur
 * Cette page ce trouve � l'url /registration et est associ� � la vue /WEB-INF/views/registration.jsp
 */
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = -771795245773946656L;

	private static final String VUE		 = "/WEB-INF/views/registration.jsp";
	
	private UserDAO userDao;

	// COMMANDES

	/**
	 * Commande appel� lorsque la page re�oit des informations � l'aide de la m�thode POST
	 * Cela arrivera lorsque l'utilisateur confirmera la cr�ation de son compte.
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
			
			// Dans un premier temps on v�rifie si l'utilisateur de nom username existe d�j�
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
						
						// Ensuite on stocke le bean correspondant � l'utilisateur dans la session et on ajoute
						// 		cette utilisateur dans la base de donn�es
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
	 * Commande appel� lorsqu'une personne souhaite r�cup�rer le contenu de la page /registration � l'aide de la m�thode GET
	 * Elle sera appel� lorsqu'un utilisateur souhaite cr�er un compte
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
