package servlets;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.*;
import domain.*;

/**
 * Servlet permettant de g�rer l'affichage des informations d'un kanban
 * Cette page ce trouve � l'url /kanban et est associ� � la vue /WEB-INF/views/kanban.jsp
 * Cette page n'est accessible que si les attribut de session 'user' et 'kanban' sont d�finis, c�d 
 * 		si l'utilisateur est connect� et qu'il a choisi d'aller sur la page d'information d'un kanban
 * 		en particulier
 * Redirige vers la page de conenxion si l'utilisateur n'est pas connect�
 */
@WebServlet("/kanban")
public class KanbanServlet extends HttpServlet {
	
	// ATTRIBUTS
	
	private static final long serialVersionUID = 1L;
    
	private static final String VUE		 = "/WEB-INF/views/kanban.jsp";
	
	private KanbanDAO kanbanDao;
	private TaskDAO taskDao;
	private UserDAO userDao;
	private GuestDAO guestDao;

    // COMMANDES

	/**
	 * Commande appel� lorsque la page re�oit des informations � l'aide de la m�thode POST
	 * On v�rifie si un utilisateur est bien connect� pour acc�der � cette page sinon on le redirige vers la page de connexion
	 * Cela arrivera sur diff�rente situation :
	 * 		1) On arrive pour la premi�re fois sur la page et on souhaite r�cup�rer les informations du kanban
	 * 		2) Le gestionnaire du kanban ajoute un invit�
	 * 		3) Le gestionnaire du kanban supprime un invit�
	 * 		4) On supprime une t�che du kanban
	 * 		5) On met � jour les informations d'une t�che du kanban
	 * 		6) On cr�e une nouvelle t�che dans la kanban courant
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
			this.taskDao = DAOFactory.getInstance().getTaskDao();
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();
			this.guestDao = DAOFactory.getInstance().getGuestDao();
			// Permet l'initialisation de la page d'affichage du kanban
			//	Le formulaire contenant le parametre 'kanban-value' vient de la page /welcomePage ou /profil
			if (request.getParameter("kanban-value") != null) {
				request.setCharacterEncoding("UTF-8");
				Kanban kanban = kanbanDao.getKanban(Long.valueOf(request.getParameter("kanban-value")));
				request.getSession().setAttribute("kanban", kanban);
				List<Task> tasks = taskDao.getKanbanTasks(kanban);
				request.setAttribute("tasks", tasks);
				request.setAttribute("sizeTextArea", DAOFactory.SIZE_TEXTAREA);
				this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
				
			} else {
				
				// Si aucun kanban n'est pr�sent, cela signifie que la requ�te post permettant 
				//		d'initialiser la page n'as pas �t� correctement effectu�
				//	Effet :
				//															-->		infoKanban = Aucun kanban n'as �t� trouv� !
				if (request.getSession().getAttribute("kanban") == null) {
					request.setAttribute("infoAddTask", "Aucun kanban n'as �t� trouv� !");
				
					
				// Ajout d'un invit� au kanban
				// 	Conditions :
				//		Le nom de la personne existe						-->		infoAddGuest = Cette utilisateur n'existe pas !
				//		La personne n'est pas le gestionnaire du kanban		-->		infoAddGuest = Vous ne pouvez pas vous inviter vous m�me !
				//		La personne n'est pas d�j� invit�					-->		infoAddGuest = Cette utilisateur est d�j� invit� !
				//	Retour :
				//		�chec												-->		infoAddGuest = Invitation �chou� !
				//		succ�s												-->		infoAddGuest = Invitation r�ussie !
				} else if (request.getSession().getAttribute("user") != null && request.getParameter("invite-user") != null 
						&& !request.getParameter("invite-user").equals("")) {
					List<String> guests = guestDao.getListGuestsUsername( (Kanban)request.getSession().getAttribute("kanban"));
	
					// Si le nom d'utilisateur n'existe pas
					if (!userDao.userExist(request.getParameter("invite-user"))) {
						request.setAttribute("infoAddGuest", "Cette utilisateur n'existe pas !");
						
					// Si il s'agit du gestionnaire
					} else if ( ((User) userDao.getUser(((Kanban) request.getSession().getAttribute("kanban")).getIdOwner()))
							.getUsername().equals(request.getParameter("invite-user")) ) {
						request.setAttribute("infoAddGuest", "Vous ne pouvez pas vous inviter vous m�me !");
					
					} else {
						User userInvited = userDao.getUser(userDao.getUserId(request.getParameter("invite-user")));
						
						// Si la personne est d�j� invit�
						if (guests.contains(userInvited.getUsername())) {
							request.setAttribute("infoAddGuest", "Cette utilisateur est d�j� invit� !");
							
						// Sinon on essaie d'ajouter la personne dans la liste des invit�s du kanban
						} else {
							try {
								guestDao.addGuest( (Kanban)request.getSession().getAttribute("kanban"), userInvited);
							} catch (DAOException e) {
								request.setAttribute("infoAddGuest", "Invitation �chou� !");
							}
							request.setAttribute("infoAddGuest", "Invitation r�ussie !");
						}
					}
				
					
				// Suppression d'un invit� au kanban
				// 	Conditions :
				//		aucune car le nom des personnes seront forc�ment des gens qui auront �t� invit�
				//	Effet :
				//		Suppression des assignations de la personne (deviennent en 'Not defined')
				//	Retour :
				//		�chec assignation des t�ches						-->		infoRemoveGuest = Suppresion de l'assignation de l'utilisateur �chou� !
				//		�chec suppression des invit�s						-->		infoRemoveGuest = Suppression de l'invit� �chou� !
				//		succ�s suppression									-->		infoRemoveGuest = Suppression de l'invit� r�ussie !
				} else if (request.getSession().getAttribute("user") != null && request.getParameter("remove-user") != null
						&& !request.getParameter("remove-user").equals("")) {
					
					// on r�cup�re l'utilisateur a retirer des invit�s, ainsi que les t�ches qui lui �tait attribu� dans ce kanban
					User userDeleted = userDao.getUser(userDao.getUserId(request.getParameter("remove-user")));
					List<Task> tasks = taskDao.getKanbanAssignedTasks( (Kanban)request.getSession().getAttribute("kanban"), userDeleted);
					boolean error = false;
					// On supprime l'ensemble des assignations de l'utilisateur
					try {
						
						// Pour retirer l'assignation, on met � jour la t�che en changeant uniquement 'assign' en mettant null
						for (Task task : tasks) {
							taskDao.updateAssignTask(null, task.getId());
						}
					} catch (DAOException e) {
						error = true;
						request.setAttribute("infoRemoveGuest", "Suppresion de l'assignation de l'utilisateur �chou� !");
					}
					
					// On supprime l'utilisateur des invit�s si il n'y a pas eu d'erreur lors de la suppression des assignements
					if (!error) {
						try {
							guestDao.deleteKanbanGuest( (Kanban)request.getSession().getAttribute("kanban"), userDeleted);
							request.setAttribute("infoRemoveGuest", "Suppression de l'invit� r�ussie !");
						} catch (DAOException e) {
							request.setAttribute("infoRemoveGuest", "Suppression de l'invit� �chou� !");
						}
					}
					
				// Suppression d'une t�che
				//	Retour :
				//		�chec 												-->		infoRemoveTask = Suppression de la t�che �chou� !
				//		succ�s												-->		infoRemoveTask = Suppression de la t�che r�ussie !
				} else if (request.getParameter("delete-task") != null) {
					try {
						taskDao.deleteTask(taskDao.getTask(Long.valueOf(request.getParameter("delete-task"))));
						request.setAttribute("infoRemoveTask", "Suppression de la t�che r�ussie !");
					} catch (DAOException e) {
						request.setAttribute("infoRemoveTask", "Suppression de la t�che �chou� !");
					}
					
					
				// Mise � jour d'une t�che
				// 	Retour:
				//		�chec t�che inconnue								-->		infoUpdateTask = Champ cach� modifi�, num�ro de t�che inconnue !
				//		�chec assignation									-->		infoUpdateTask = Erreur de mise � jour : Assignation � une personne non existante !
				//		�chec date											-->		infoUpdateTask = Erreur de mise � jour : Date invalide !
				//		�chec 												-->		infoUpdateTask = Mise � jour de la t�che �chou� !
				//		succ�s												-->		infoUpdateTask = Mise � jour de la t�che r�ussie !
				} else if (request.getParameter("update-task") != null) {
					String description = request.getParameter("description");
					Long id = null;
					Long assign = null;
					Date date = null;
					String column = null;
					
					// On r�cup�re le num�ro de t�che � modifier
					try {
						id = Long.valueOf(request.getParameter("update-task"));
					} catch (NumberFormatException e) {
						request.setAttribute("infoUpdateTask", "Champ cach� modifi�, num�ro de t�che inconnue !");
						doGet(request, response);
					}
					
					// On r�cup�re le nom de la colonne o� assigner la t�che
					if (request.getParameter("choose-column") != null) {
						column = request.getParameter("choose-column");
					} else {
						column = null;
					}
					
					// On r�cup�re la nouvelle assignation de la t�che
					if (request.getParameter("assignment") != null && !request.getParameter("assignment").equals("")) {
						try {
							assign = userDao.getUserId(request.getParameter("assignment"));
						} catch (DAOException e) {
							request.setAttribute("infoUpdateTask", "Erreur de mise � jour : Assignation � une personne non existante !");
							doGet(request, response);
						}
					} else {
						assign = null;
					}
					
					// On r�cup�re la nouvelle date limite de la t�che
					if (request.getParameter("deadline") != null && !request.getParameter("deadline").equals("null") && !request.getParameter("deadline").equals("")) {
						String[] dateFields = request.getParameter("deadline").split("-");
						if (dateFields.length == 3) {
							Calendar cal = Calendar.getInstance();
							cal.set(Integer.valueOf(dateFields[0]), Integer.valueOf(dateFields[1]) - 1, Integer.valueOf(dateFields[2]));
							date = new Date(cal.getTime().getTime());
						} else {
							request.setAttribute("infoUpdateTask", "Erreur de mise � jour : Date invalide !");
							doGet(request, response);
						}
					} else {
						date = null;
					}
					
					// On met � jour la t�che
					try {
						taskDao.updateTask(description, assign, date, column, id);
						request.setAttribute("infoUpdateTask", "Mise � jour de la t�che r�ussie !");
					} catch (DAOException e) {
						request.setAttribute("infoUpdateTask", "Mise � jour de la t�che �chou� !");
					}
				
				
				// Ajout d'un nouvelle t�che
				// Retour :
				//		�chec kanban										-->		infoAddTask = Erreur cr�ation : Kanban courant invalide !
				//		�chec assignation									-->		infoAddTask = Erreur cr�ation : Assignation � une personne non existante !
				//		�chec date											-->		infoAddTask = Erreur cr�ation : Date invalide !
				//		�chec 												-->		infoAddTask = Ajout de la t�che �chou� !
				//		succ�s												-->		infoAddTask = Ajout de la t�che r�ussie !
				} else if (request.getParameter("column") != null) {
					
					// cr�ation de l'objet de la t�che et sauvegarde des champs n'occurant aucune erreur
					Task task = new Task();
					task.setDescription(request.getParameter("description"));
					task.setNameColumn(request.getParameter("column"));
					
					// Assignation de la t�che au kanban actuelle
					if (request.getSession().getAttribute("kanban") != null) {
						task.setIdKanban( ((Kanban) request.getSession().getAttribute("kanban")).getId() );
					} else {
						request.setAttribute("infoAddTask", "Erreur cr�ation : Kanban courant invalide !");
					}
					
					// Enregistre l'assignation de la t�che
					if (request.getParameter("assignment") != null && !request.getParameter("assignment").equals("")) {
						try {
							task.setIdAssign(userDao.getUserId(request.getParameter("assignment")));
						} catch (DAOException e) {
							request.setAttribute("infoAddTask", "Erreur cr�ation : Assignation � une personne non existante !");
							doGet(request, response);
						}
					} else {
						task.setIdAssign(null);
					}
					
					// Enregistre la date limite de la t�che
					if (request.getParameter("deadline") != null && !request.getParameter("deadline").equals("")) {
						String[] dateFields = request.getParameter("deadline").split("-");
						if (dateFields.length == 3) {
							Calendar cal = Calendar.getInstance();
							cal.set(Integer.valueOf(dateFields[0]), Integer.valueOf(dateFields[1]) - 1, Integer.valueOf(dateFields[2]));
							task.setDate(new Date(cal.getTime().getTime()));
						} else {
							request.setAttribute("infoAddTask", "Erreur cr�ation : Date invalide !");
							doGet(request, response);
						}
					} else {
						task.setDate(null);
					}
	
					try {
						taskDao.createTask(task);
						request.setAttribute("infoAddTask", "Ajout de la t�che r�ussie !");
					} catch (DAOException e) {
						request.setAttribute("infoAddTask", "Ajout de la t�che �chou� !");
					}
				}
				
				// On enregistre la liste des t�ches du kanban dans la variable 'tasks'
				List<Task> tasks = taskDao.getKanbanTasks((Kanban)request.getSession().getAttribute("kanban"));
				request.setAttribute("tasks", tasks);
				request.setAttribute("sizeTextArea", DAOFactory.SIZE_TEXTAREA);
				doGet(request, response);
			}
		}
	}

	/**
	 * Commande appel� lorsqu'une personne souhaite r�cup�rer le contenu de la page /kanban � l'aide de la m�thode GET
	 * On v�rifie si un utilisateur est bien connect� pour acc�der � cette page sinon on le redirige vers la page de connexion
	 * Cette commande sera utilis� lors de la premi�re visite sur la page. L'attribut 'kanban' dans la session devra �tre pr�sente
	 * 		sinon l'utilisateur sera redirig� vers la page d'accueil
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
			this.taskDao = DAOFactory.getInstance().getTaskDao();
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();
			this.guestDao = DAOFactory.getInstance().getGuestDao();
					
			// Si aucune kanban n'est enregistr� pour �tre affich�, redirection vers la page d'accueil
			if (request.getSession().getAttribute("kanban") == null) {
				response.sendRedirect(request.getContextPath() + "/welcomePage");
			}
			List<Task> tasks = taskDao.getKanbanTasks((Kanban)request.getSession().getAttribute("kanban"));
			request.setAttribute("tasks", tasks);
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("sizeTextArea", DAOFactory.SIZE_TEXTAREA);
			this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
		}
	}
}
