package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.*;
import domain.Kanban;
import domain.Task;
import domain.User;

/**
 * Servlet permettant à un utilisateur d'avoir accès à son compte utilisateur. 
 * Cette page se trouve à l'URL /profile et est associé à la vue /WEB-INF/views/profile.jsp.
 * Cette page n'est accessible que si la variable de session 'user' est définie, c'est-à-dire si l'utilisateur
 * 		est connecté. Dans cette page sont affichés toutes les informations (liste des kanbans, des tâches etc).
 * Redirige vers la page de connexion si l'utilisateur n'est pas connecté ou vers la page de configuration
 * 		si la base de données n'est pas configurée.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
	
	// Attribut qui contient le lien vers la page .jsp qui correspond à la servlet.
	private static final String VUE		 = "/WEB-INF/views/profile.jsp";
	
	// Attributs qui permettent d'avoir accès aux tables USER, KANBAN et TASK.
	private KanbanDAO kanbanDao;
	private TaskDAO taskDao;
	private GuestDAO guestDao;

    // COMMANDES
    
	/**
	 * Méthode appelée par le serveur pour traiter la requête de type post dans la page profile.jsp.
	 * Cette méthode permet à un utilisateur d'avoir accès à son compte utilisateur. Si la base de données 
	 * 		n'est pas configurée, l'utilisateur est dirigé vers la page de configuration.
	 * Sinon, nous testons si l'utilisateur a cliqué sur le bouton pour supprimer un kanban. Si c'est le cas
	 * 		nous supprimons les tâches et les utilisateurs liés au kanban que l'utilisateur veut supprimer et après
	 * 		nous supprimons le kanban. L'attribut infoDeleteKanbanest défini pour vérifier si la suppression passe bien.
	 * 		Méthode doGet appelée.
	 * @pre <pre>
	 * 		DAOFactory.dbIsValidate() </pre>
     * @exception DAOException <pre>
     * 		si problème coté base de données </pre>	
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
			this.taskDao = DAOFactory.getInstance().getTaskDao();
			this.guestDao = DAOFactory.getInstance().getGuestDao();
			request.setCharacterEncoding("UTF-8");
			if (request.getParameter("delete-kanban") != null) {
				try {
					List <Task> tasks = taskDao.getKanbanTasks(kanbanDao.getKanban(Long.valueOf(request.getParameter("delete-kanban"))));
					if (tasks.size() > 0) {
						taskDao.deleteTaskByKanban(kanbanDao.getKanban(Long.valueOf(request.getParameter("delete-kanban"))));
					}
					List <User> users = guestDao.getListGuests(kanbanDao.getKanban(Long.valueOf(request.getParameter("delete-kanban"))));
					if (users.size() > 0) {
						guestDao.deleteKanbanGuests(kanbanDao.getKanban(Long.valueOf(request.getParameter("delete-kanban"))));
					}
					kanbanDao.deleteKanban(kanbanDao.getKanban(Long.valueOf(request.getParameter("delete-kanban"))));
					request.setAttribute("infoDeleteKanban", "Successful deletion of the kanban !");
				} catch (DAOException e) {
					 request.setAttribute("infoDeleteKanban", "Error" + e);
				}
			}
			doGet(request, response);
		}
	}
	
	/**
	 * Méthode appelée par le serveur pour traiter la requête de type get dans la page profile.jsp.
	 * Redirige vers la page de configuration si la base de données n'est pas configurée ou vers 
	 * 		la page /profile.jsp. sinon.
	 * Définit les attributs kanbans, kanbans-guest et allTasks qui correspondent à la liste des kanbans d'un utilisateur,
	 * la liste des kanbans où il est invité et la liste de toutes ses tâches.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
			this.taskDao = DAOFactory.getInstance().getTaskDao();
			this.guestDao = DAOFactory.getInstance().getGuestDao();
			HttpSession session = request.getSession();
	        if (session.getAttribute("user") == null) {
	            response.sendRedirect(request.getContextPath() + "/login");
	        } else {
	        	request.setCharacterEncoding("UTF-8");
	        	User user = (User) session.getAttribute("user");	
	        	
	        	List<Kanban> kanban = kanbanDao.getKanbanByOwner(user);
	        	List<Kanban> kanbanGuests = guestDao.getListKanbanGuest(user);
	        	List<Task> allTasks = taskDao.getAssignedTasks(user);
	        	
	        	request.setAttribute("kanbans", kanban);
	        	request.setAttribute("kanbans-guests", kanbanGuests);
	        	request.setAttribute("allTasks", allTasks);
	    		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	        }
		}
	}
}
