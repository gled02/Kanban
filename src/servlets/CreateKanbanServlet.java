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
import dao.KanbanDAO;
import domain.Kanban;
import domain.User;

/**
 * Servlet permettant de g�rer la cr�ation d'un kanban
 * 	Cette page ce trouve � l'url /createKanban et est associ� � la vue /WEB-INF/views/createKanban.jsp
 * 	Cette page n'est accessible que si l'attribut de session 'user' est d�fini, c�d si un utilisateur
 * 		est actuellement connect�
 */
@WebServlet("/createKanban")
public class CreateKanbanServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 2717808143776847544L;
	private static final String VUE		 	= "/WEB-INF/views/createKanban.jsp";

	private KanbanDAO kanbanDao;

	// COMMANDES

	/**
	 * Commande appel� lorsque la page re�oit des informations � l'aide de la m�thode POST
	 * Cela arrivera lorsque l'utilisateur confirmera la cr�ation du kanban. On utilise donc l'objet KanbanDAO 
	 *		afin d'enregistrer le kanban cr��, et on redirige l'utilisateur vers la page du kanban cr��
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
	        HttpSession session = request.getSession();
	        if (session.getAttribute("user") == null) {
	            response.sendRedirect(request.getContextPath() + "/login");
	        } else {
				request.setCharacterEncoding("UTF-8");
				Kanban kanban = new Kanban();
				kanban.setIdOwner( ((User) session.getAttribute("user")).getId() );
				kanban.setNameKanban(request.getParameter("name"));
				kanban.setPublic(request.getParameter("hidden-check-value").equals("on"));
				kanban.setColumns(request.getParameter("hidden-form-value"));
				kanban.setDescription(request.getParameter("description"));
				try {
					kanbanDao.createKanban(kanban);
					session.setAttribute("kanban", kanban);
					response.sendRedirect(request.getContextPath() + "/kanban");
				} catch (DAOException e) {
					request.setAttribute("infoCreation", "Creation kanban '" + kanban.getNameKanban() + "' failed !");
					doGet(request, response);
				}
	        }
        }
	}

	/**
	 * Commande appel� lorsqu'une personne souhaite r�cup�rer le contenu de la page /createKanban � l'aide de la m�thode GET
	 * 	On v�rifie si un utilisateur est bien connect� pour acc�der � cette page sinon on le redirige vers la page de connexion
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
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
