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
 * Servlet permettant de gérer l'affichage des informations d'un kanban
 * Cette page ce trouve à l'url /kanban et est associé à la vue /WEB-INF/views/kanban.jsp
 * Cette page n'est accessible que si les attribut de session 'user' et 'kanban' sont définis, càd 
 * 		si l'utilisateur est connecté et qu'il a choisi d'aller sur la page d'information d'un kanban
 * 		en particulier
 * Redirige vers la page de conenxion si l'utilisateur n'est pas connecté
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
	 * Commande appelé lorsque la page reçoit des informations à l'aide de la méthode POST
	 * On vérifie si un utilisateur est bien connecté pour accéder à cette page sinon on le redirige vers la page de connexion
	 * Cela arrivera sur différente situation :
	 * 		1) On arrive pour la première fois sur la page et on souhaite récupérer les informations du kanban
	 * 		2) Le gestionnaire du kanban ajoute un invité
	 * 		3) Le gestionnaire du kanban supprime un invité
	 * 		4) On supprime une tâche du kanban
	 * 		5) On met à jour les informations d'une tâche du kanban
	 * 		6) On crée une nouvelle tâche dans la kanban courant
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
				
				// Si aucun kanban n'est présent, cela signifie que la requête post permettant 
				//		d'initialiser la page n'as pas été correctement effectué
				//	Effet :
				//															-->		infoKanban = Aucun kanban n'as été trouvé !
				if (request.getSession().getAttribute("kanban") == null) {
					request.setAttribute("infoAddTask", "Aucun kanban n'as été trouvé !");
				
					
				// Ajout d'un invité au kanban
				// 	Conditions :
				//		Le nom de la personne existe						-->		infoAddGuest = Cette utilisateur n'existe pas !
				//		La personne n'est pas le gestionnaire du kanban		-->		infoAddGuest = Vous ne pouvez pas vous inviter vous même !
				//		La personne n'est pas déjà invité					-->		infoAddGuest = Cette utilisateur est déjà invité !
				//	Retour :
				//		échec												-->		infoAddGuest = Invitation échoué !
				//		succès												-->		infoAddGuest = Invitation réussie !
				} else if (request.getSession().getAttribute("user") != null && request.getParameter("invite-user") != null 
						&& !request.getParameter("invite-user").equals("")) {
					List<String> guests = guestDao.getListGuestsUsername( (Kanban)request.getSession().getAttribute("kanban"));
	
					// Si le nom d'utilisateur n'existe pas
					if (!userDao.userExist(request.getParameter("invite-user"))) {
						request.setAttribute("infoAddGuest", "Cette utilisateur n'existe pas !");
						
					// Si il s'agit du gestionnaire
					} else if ( ((User) userDao.getUser(((Kanban) request.getSession().getAttribute("kanban")).getIdOwner()))
							.getUsername().equals(request.getParameter("invite-user")) ) {
						request.setAttribute("infoAddGuest", "Vous ne pouvez pas vous inviter vous même !");
					
					} else {
						User userInvited = userDao.getUser(userDao.getUserId(request.getParameter("invite-user")));
						
						// Si la personne est déjà invité
						if (guests.contains(userInvited.getUsername())) {
							request.setAttribute("infoAddGuest", "Cette utilisateur est déjà invité !");
							
						// Sinon on essaie d'ajouter la personne dans la liste des invités du kanban
						} else {
							try {
								guestDao.addGuest( (Kanban)request.getSession().getAttribute("kanban"), userInvited);
							} catch (DAOException e) {
								request.setAttribute("infoAddGuest", "Invitation échoué !");
							}
							request.setAttribute("infoAddGuest", "Invitation réussie !");
						}
					}
				
					
				// Suppression d'un invité au kanban
				// 	Conditions :
				//		aucune car le nom des personnes seront forcément des gens qui auront été invité
				//	Effet :
				//		Suppression des assignations de la personne (deviennent en 'Not defined')
				//	Retour :
				//		échec assignation des tâches						-->		infoRemoveGuest = Suppresion de l'assignation de l'utilisateur échoué !
				//		échec suppression des invités						-->		infoRemoveGuest = Suppression de l'invité échoué !
				//		succès suppression									-->		infoRemoveGuest = Suppression de l'invité réussie !
				} else if (request.getSession().getAttribute("user") != null && request.getParameter("remove-user") != null
						&& !request.getParameter("remove-user").equals("")) {
					
					// on récupère l'utilisateur a retirer des invités, ainsi que les tâches qui lui était attribué dans ce kanban
					User userDeleted = userDao.getUser(userDao.getUserId(request.getParameter("remove-user")));
					List<Task> tasks = taskDao.getKanbanAssignedTasks( (Kanban)request.getSession().getAttribute("kanban"), userDeleted);
					boolean error = false;
					// On supprime l'ensemble des assignations de l'utilisateur
					try {
						
						// Pour retirer l'assignation, on met à jour la tâche en changeant uniquement 'assign' en mettant null
						for (Task task : tasks) {
							taskDao.updateAssignTask(null, task.getId());
						}
					} catch (DAOException e) {
						error = true;
						request.setAttribute("infoRemoveGuest", "Suppresion de l'assignation de l'utilisateur échoué !");
					}
					
					// On supprime l'utilisateur des invités si il n'y a pas eu d'erreur lors de la suppression des assignements
					if (!error) {
						try {
							guestDao.deleteKanbanGuest( (Kanban)request.getSession().getAttribute("kanban"), userDeleted);
							request.setAttribute("infoRemoveGuest", "Suppression de l'invité réussie !");
						} catch (DAOException e) {
							request.setAttribute("infoRemoveGuest", "Suppression de l'invité échoué !");
						}
					}
					
				// Suppression d'une tâche
				//	Retour :
				//		échec 												-->		infoRemoveTask = Suppression de la tâche échoué !
				//		succès												-->		infoRemoveTask = Suppression de la tâche réussie !
				} else if (request.getParameter("delete-task") != null) {
					try {
						taskDao.deleteTask(taskDao.getTask(Long.valueOf(request.getParameter("delete-task"))));
						request.setAttribute("infoRemoveTask", "Suppression de la tâche réussie !");
					} catch (DAOException e) {
						request.setAttribute("infoRemoveTask", "Suppression de la tâche échoué !");
					}
					
					
				// Mise à jour d'une tâche
				// 	Retour:
				//		échec tâche inconnue								-->		infoUpdateTask = Champ caché modifié, numéro de tâche inconnue !
				//		échec assignation									-->		infoUpdateTask = Erreur de mise à jour : Assignation à une personne non existante !
				//		échec date											-->		infoUpdateTask = Erreur de mise à jour : Date invalide !
				//		échec 												-->		infoUpdateTask = Mise à jour de la tâche échoué !
				//		succès												-->		infoUpdateTask = Mise à jour de la tâche réussie !
				} else if (request.getParameter("update-task") != null) {
					String description = request.getParameter("description");
					Long id = null;
					Long assign = null;
					Date date = null;
					String column = null;
					
					// On récupère le numéro de tâche à modifier
					try {
						id = Long.valueOf(request.getParameter("update-task"));
					} catch (NumberFormatException e) {
						request.setAttribute("infoUpdateTask", "Champ caché modifié, numéro de tâche inconnue !");
						doGet(request, response);
					}
					
					// On récupère le nom de la colonne où assigner la tâche
					if (request.getParameter("choose-column") != null) {
						column = request.getParameter("choose-column");
					} else {
						column = null;
					}
					
					// On récupère la nouvelle assignation de la tâche
					if (request.getParameter("assignment") != null && !request.getParameter("assignment").equals("")) {
						try {
							assign = userDao.getUserId(request.getParameter("assignment"));
						} catch (DAOException e) {
							request.setAttribute("infoUpdateTask", "Erreur de mise à jour : Assignation à une personne non existante !");
							doGet(request, response);
						}
					} else {
						assign = null;
					}
					
					// On récupère la nouvelle date limite de la tâche
					if (request.getParameter("deadline") != null && !request.getParameter("deadline").equals("null") && !request.getParameter("deadline").equals("")) {
						String[] dateFields = request.getParameter("deadline").split("-");
						if (dateFields.length == 3) {
							Calendar cal = Calendar.getInstance();
							cal.set(Integer.valueOf(dateFields[0]), Integer.valueOf(dateFields[1]) - 1, Integer.valueOf(dateFields[2]));
							date = new Date(cal.getTime().getTime());
						} else {
							request.setAttribute("infoUpdateTask", "Erreur de mise à jour : Date invalide !");
							doGet(request, response);
						}
					} else {
						date = null;
					}
					
					// On met à jour la tâche
					try {
						taskDao.updateTask(description, assign, date, column, id);
						request.setAttribute("infoUpdateTask", "Mise à jour de la tâche réussie !");
					} catch (DAOException e) {
						request.setAttribute("infoUpdateTask", "Mise à jour de la tâche échoué !");
					}
				
				
				// Ajout d'un nouvelle tâche
				// Retour :
				//		échec kanban										-->		infoAddTask = Erreur création : Kanban courant invalide !
				//		échec assignation									-->		infoAddTask = Erreur création : Assignation à une personne non existante !
				//		échec date											-->		infoAddTask = Erreur création : Date invalide !
				//		échec 												-->		infoAddTask = Ajout de la tâche échoué !
				//		succès												-->		infoAddTask = Ajout de la tâche réussie !
				} else if (request.getParameter("column") != null) {
					
					// création de l'objet de la tâche et sauvegarde des champs n'occurant aucune erreur
					Task task = new Task();
					task.setDescription(request.getParameter("description"));
					task.setNameColumn(request.getParameter("column"));
					
					// Assignation de la tâche au kanban actuelle
					if (request.getSession().getAttribute("kanban") != null) {
						task.setIdKanban( ((Kanban) request.getSession().getAttribute("kanban")).getId() );
					} else {
						request.setAttribute("infoAddTask", "Erreur création : Kanban courant invalide !");
					}
					
					// Enregistre l'assignation de la tâche
					if (request.getParameter("assignment") != null && !request.getParameter("assignment").equals("")) {
						try {
							task.setIdAssign(userDao.getUserId(request.getParameter("assignment")));
						} catch (DAOException e) {
							request.setAttribute("infoAddTask", "Erreur création : Assignation à une personne non existante !");
							doGet(request, response);
						}
					} else {
						task.setIdAssign(null);
					}
					
					// Enregistre la date limite de la tâche
					if (request.getParameter("deadline") != null && !request.getParameter("deadline").equals("")) {
						String[] dateFields = request.getParameter("deadline").split("-");
						if (dateFields.length == 3) {
							Calendar cal = Calendar.getInstance();
							cal.set(Integer.valueOf(dateFields[0]), Integer.valueOf(dateFields[1]) - 1, Integer.valueOf(dateFields[2]));
							task.setDate(new Date(cal.getTime().getTime()));
						} else {
							request.setAttribute("infoAddTask", "Erreur création : Date invalide !");
							doGet(request, response);
						}
					} else {
						task.setDate(null);
					}
	
					try {
						taskDao.createTask(task);
						request.setAttribute("infoAddTask", "Ajout de la tâche réussie !");
					} catch (DAOException e) {
						request.setAttribute("infoAddTask", "Ajout de la tâche échoué !");
					}
				}
				
				// On enregistre la liste des tâches du kanban dans la variable 'tasks'
				List<Task> tasks = taskDao.getKanbanTasks((Kanban)request.getSession().getAttribute("kanban"));
				request.setAttribute("tasks", tasks);
				request.setAttribute("sizeTextArea", DAOFactory.SIZE_TEXTAREA);
				doGet(request, response);
			}
		}
	}

	/**
	 * Commande appelé lorsqu'une personne souhaite récupérer le contenu de la page /kanban à l'aide de la méthode GET
	 * On vérifie si un utilisateur est bien connecté pour accéder à cette page sinon on le redirige vers la page de connexion
	 * Cette commande sera utilisé lors de la première visite sur la page. L'attribut 'kanban' dans la session devra être présente
	 * 		sinon l'utilisateur sera redirigé vers la page d'accueil
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!DAOFactory.dbIsValidate()) {
	        response.sendRedirect(request.getContextPath() + "/configuration");
		} else {
			this.kanbanDao = DAOFactory.getInstance().getKanbanDao();
			this.taskDao = DAOFactory.getInstance().getTaskDao();
			this.userDao = DAOFactory.getInstance().getUtilisateurDao();
			this.guestDao = DAOFactory.getInstance().getGuestDao();
					
			// Si aucune kanban n'est enregistré pour être affiché, redirection vers la page d'accueil
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
