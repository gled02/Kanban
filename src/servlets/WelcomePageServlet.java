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
 * Servlet permettant de g�rer la page d'accueil qui contient la liste des kanbans publics.
 * Cette page se trouve � l'URL /welcomePage et est associ� � la vue /WEB-INF/views/welcomePage.jsp.
 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e, 
 * 		vers la page /kanban lorsqu'un utilisateur clique sur un kanban pour voir son contenu. 
 */
@WebServlet("/welcomePage")
public class WelcomePageServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;

	// Attribut qui contient le lien vers la page .jsp qui correspond � la servlet.
	private static final String VUE		 	= "/WEB-INF/views/welcomePage.jsp";
	private static final String VUE_KANBAN	= "/WEB-INF/views/kanban.jsp";
	
	// Attributs qui permettent d'avoir acc�s aux tables KANBAN et TASK.
	private KanbanDAO kanbanDao;
    private TaskDAO taskDao; 
   
    // COMMANDES
    
    /**
	 * M�thode appel�e par le serveur pour traiter la requ�te de type post dans la page welcomePage.jsp.
	 * Cette m�thode permet de g�rer la liste des kanbans publics. Si la base de donn�es 
	 * 		n'est pas configur�e, l'utilisateur est dirig� vers la page de configuration.
	 * Sinon, si l'utilisateur a cliqu� sur un kanban nous r�cup�rons l'identifiant du kanban
	 * et nous cr�ons les attributs 'kanban' et 'task' pour avoir acc�s au kanban choisi et pour avoir acc�s � l'ensemble
	 * des t�ches contenues dans le kanban. R�direction vers la page /kanban.jsp.
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
	 * M�thode appel�e par le serveur pour traiter la requ�te de type get dans la page welcomePage.jsp.
	 * Redirige vers la page de configuration si la base de donn�es n'est pas configur�e ou vers 
	 * 		la page /profile.jsp. sinon.
	 * D�finit l'attribut publicKanbans qui correspond � la liste des kanbans publics.
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
