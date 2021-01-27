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
 * Servlet permettant � un utilisateur d'avoir acc�s � son compte utilisateur. 
 * Cette page se trouve � l'URL /profile et est associ� � la vue /WEB-INF/views/profile.jsp.
 * Cette page n'est accessible que si la variable de session 'user' est d�finie, c'est-�-dire si l'utilisateur
 * 		est connect�. Dans cette page sont affich�s toutes les informations (liste des kanbans, des t�ches etc).
 * Redirige vers la page de connexion si l'utilisateur n'est pas connect� ou vers la page de configuration
 * 		si la base de donn�es n'est pas configur�e.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
	
	// Attribut qui contient le lien vers la page .jsp qui correspond � la servlet.
	private static final String VUE		 = "/WEB-INF/views/profile.jsp";
	
	// Attributs qui permettent d'avoir acc�s aux tables USER, KANBAN et TASK.
	private KanbanDAO kanbanDao;
	private TaskDAO taskDao;
	private GuestDAO guestDao;

    // COMMANDES
    
	/**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type post dans la page profile.jsp.
	 * Cette m�thode permet � un utilisateur d'avoir acc�s � son compte utilisateur. Si la base de donn�es 
	 * 		n'est pas configur�e, l'utilisateur est dirig� vers la page de configuration.
	 * Sinon, nous testons si l'utilisateur a cliqu� sur le bouton pour supprimer un kanban. Si c'est le cas
	 * 		nous supprimons les t�ches et les utilisateurs li�s au kanban que l'utilisateur veut supprimer et apr�s
	 * 		nous supprimons le kanban. L'attribut infoDeleteKanbanest d�fini pour v�rifier si la suppression passe bien.
	 * 		M�thode doGet appel�e.
	 * @pre <pre>
	 * 		DAOFactory.dbIsValidate() </pre>
     * @exception DAOException <pre>
     * 		si probl�me cot� base de donn�es </pre>	
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
	 * M�thode appel�e par le serveur pour traiter la requ�te de type get dans la page profile.jsp.
	 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e ou vers 
	 * 		la page /profile.jsp. sinon.
	 * D�finit les attributs kanbans, kanbans-guest et allTasks qui correspondent � la liste des kanbans d'un utilisateur,
	 * la liste des kanbans o� il est invit� et la liste de toutes ses t�ches.
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
