package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.DAOFactory;
import dao.KanbanDAO;
import dao.TaskDAO;
import domain.Kanban;
import domain.Task;

/**
 * Servlet permettant de gérer la page d'accueil qui contient la liste des kanbans publics.
 * Cette page se trouve à l'URL /welcomePage et est associé à la vue /WEB-INF/views/welcomePage.jsp.
 * Redirige vers la page de configuration si la base de données n'est pas configurée, 
 * 		vers la page /kanban lorsqu'un utilisateur clique sur un kanban pour voir son contenu. 
 */
@WebServlet("/welcomePage")
public class WelcomePageServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;

	// Attribut qui contient le lien vers la page .jsp qui correspond à la servlet.
	private static final String VUE		 	= "/WEB-INF/views/welcomePage.jsp";
	private static final String VUE_KANBAN	= "/WEB-INF/views/kanban.jsp";
	
	// Attributs qui permettent d'avoir accès aux tables KANBAN et TASK.
	private KanbanDAO kanbanDao;
    private TaskDAO taskDao; 
   
    // COMMANDES
    
    /**
	 * Méthode appelée par le serveur pour traiter la requête de type post dans la page welcomePage.jsp.
	 * Cette méthode permet de gérer la liste des kanbans publics. Si la base de données 
	 * 		n'est pas configurée, l'utilisateur est dirigé vers la page de configuration.
	 * Sinon, si l'utilisateur a cliqué sur un kanban nous récupérons l'identifiant du kanban
	 * et nous créons les attributs 'kanban' et 'task' pour avoir accès au kanban choisi et pour avoir accès à l'ensemble
	 * des tâches contenues dans le kanban. Rédirection vers la page /kanban.jsp.
	 * @pre <pre>
	 * 		DAOFactory.dbIsValidate() </pre>
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			request.setCharacterEncoding("UTF-8");
			Kanban kanban = kanbanDao.getKanban(Long.valueOf(request.getParameter("kanban-value")));
			request.getSession().setAttribute("kanban", kanban);
			List<Task> tasks = taskDao.getKanbanTasks(kanban);
			request.setAttribute("tasks", tasks);
			this.getServletContext().getRequestDispatcher(VUE_KANBAN).forward(request, response);
		}
	}

	/**
	 * Méthode appelée par le serveur pour traiter la requête de type get dans la page welcomePage.jsp.
	 * Redirige vers la page de configuration si la base de données n'est pas configurée ou vers 
	 * 		la page /profile.jsp. sinon.
	 * Définit l'attribut publicKanbans qui correspond à la liste des kanbans publics.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
			List<Kanban> kanbans = kanbanDao.getPublicKanbans();
			request.setAttribute("publicKanbans", kanbans);
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
		}
	}
}
