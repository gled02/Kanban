<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 
	Page permettant d'afficher les informations d'un kanban
	Contient plusieurs informations, que ce soit :
		L'ensemble des colonnes du kanban 
		Les tâches attribués aux colonnes
		La possibilité d'ajout/suppression/mise à jour de tâche
	Cette page n'est accessible que lorsque l'utilisateur est connecté
 -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<%@ page import ="domain.*"%>
<%@page import="dao.DAOFactory"%>
<%@page import="dao.GuestDAO"%>
<%@page import="dao.UserDAO"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Kanban Page</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script src="http://code.jquery.com/jquery-1.10.2.js"></script> 
		<script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/popup.css" />
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/kanban.css" />
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
		<script src="${pageContext.request.contextPath}/js/menu.js"></script>
	    <script src="${pageContext.request.contextPath}/js/textareaParser.js"></script>
	    
	    <!-- 
	    	Script javascript permettant de gérer la modification des tâches. Il met à jour les champs de la popup
	    		qui sera affiché avec les champs de la tâche qu'on souhaite modifier afin d'utiliser une seule popup
	    		pour la modification de toutes les tâches
	     -->
	    
		<%
		String sizeTextArea = request.getAttribute("sizeTextArea") != null ? (String) request.getAttribute("sizeTextArea") : "";
		User currentUser = session.getAttribute("user") != null ? (User) session.getAttribute("user") : null;
		if (currentUser != null) {%>
		<script>
		function setUpdateTask(id) {
			var owner = "<%= (session.getAttribute("kanban") == null ? 
					null : 
					DAOFactory.getInstance().getUtilisateurDao().getUser(((Kanban) session.getAttribute("kanban")).getIdOwner()).getUsername()) %>";
			var user = "<%= (currentUser == null ? null : currentUser.getUsername()) %>";
			var guests = new Array();
			<%
			if (session.getAttribute("kanban") != null) {
				List<String> guests = DAOFactory.getInstance().getGuestDao().getListGuestsUsername((Kanban) session.getAttribute("kanban"));
				for (String guest : guests) { 
					%>
			guests.push("<%= guest %>");
					<%
				}
			}
			%>
			
			var desc = document.getElementById("task-description-" + id).innerHTML;;
			var assign = (document.getElementById("task-assign-" + id).innerHTML).substring(14);
			var date = (document.getElementById("task-date-" + id).innerHTML).substring(7);
			var column = document.getElementById("task-column-" + id).innerHTML;
			var notAssigned = (assign == "Pas assignée" ? true : false);
			var notDefined = (date == "Pas définie" ? true : false);
	
			document.getElementById("assignment-description").value = placeTagText(desc);
			document.getElementById("assignment-" + (notAssigned ? "null" : assign)).selected = true;
			document.getElementById("column-" + column).selected = true;
			document.getElementById("assignment-deadline").valueAsDate = new Date(notDefined ? "null" : date);
			document.getElementById("assignment-hidden").value = id;
			
			if (user != owner) {
				document.getElementById("assignment-" + owner).disabled = false;
				guests.forEach(blockFieldUpdate);
				document.getElementById("assignment-" + user).disabled = false;
				
				if (!notAssigned && assign != user) {
					document.getElementById("assignment-description").disabled = true;
					document.getElementById("column-description").disabled = true;
					document.getElementById("assignment-deadline").disabled = true;
					document.getElementById("assignment-hidden").disabled = true;
				}
			}
			document.getElementById("modal-wrapper-update").style.display='block'
		}
		
		function blockFieldUpdate(item) {
			document.getElementById("assignment-" + item).disabled = true;
		}
		
		function validateAction(text, id) {
	        if (confirm(text)) {
	            document.getElementById(id).submit();
	        }
	    }
		</script>
		<%
		}
		%>
	</head>
	
	<body>
		<% 
	 	if (session.getAttribute("kanban") != null) {
	 		Kanban kanban = (Kanban) session.getAttribute("kanban");
	 		GuestDAO guestDAO = DAOFactory.getInstance().getGuestDao();
	 		List<User> guests = guestDAO.getListGuests(kanban);
	 		List<String> guestsUsername = guestDAO.getListGuestsUsername(kanban);
			UserDAO userDao = DAOFactory.getInstance().getUtilisateurDao();
	 		User kanbanOwner = userDao.getUser(kanban.getIdOwner());
		  	%>
		  	<ul class="ulMenu">
				<li id="img">
				 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo" width="80" height="80"> 
				</li>
				<li id="page">Kanban  : <%= kanban.getNameKanban() %></li>
				<li id="menuBlock">
					<div class="dropdown">
	  					<button onclick="myFunction()" class="dropbtn">Menu</button>
	 	 				<div id="myDropdown" class="dropdown-content">
	    					<jsp:include page="links.jsp"/>
	  					</div>
	  				</div>	
	  			</li>	
			</ul>
			
			<!-- 
				Partie contenant l'ensemble des colonnes du kanbans
			-->
			
			<div class="containers">
		 		<% 
		 		for (String column : kanban.getListColumns()) {	
			  		%>
			  		<div class="container">
			  			<h2><%= column %></h2>
			 			<% 
			  			if (currentUser == null) { 
			  				%><ul class="tasksPublic"><%  
			  			} else { 
			  				%><ul class="tasks connectedSortable"><% 
			  			}
			  				
			 	 		if (request.getAttribute("tasks") != null) {
					       	List<Task> tasks = (List<Task>) request.getAttribute("tasks");
			     		 	for (Task task : tasks) { 
			     		 		if (task.getNameColumn().equals(column)) {
					     		 	%>
			
									<!-- 
										Dans chaque colonne, on récupère les tâches associés puis on les ajoute dedans
									-->
									
									<li class="task">
										<p id="task-description-<%= task.getId() %>"><%= task.getDescription() %></p>
										<script>
										document.getElementById("task-description-<%= task.getId() %>").innerHTML = parseText("<%= task.getDescription() %>");
										</script>
					        			<ul>
					        				<li id="task-date-<%= task.getId() %>">Date : <%= task.getDate() == null ? "Pas définie" : task.getDate() %></li>
					        				<li id="task-id-<%= task.getId() %>">Id : <%= task.getId() %></li>
					        				<li style="display:none" id="task-column-<%= task.getId() %>"><%= task.getNameColumn() %></li>
				        					<%
				        					String val = null;
				        					if (task.getIdAssign() == 0) {
				        						val = "Pas assignée";
				        					} else {
				        						val = userDao.getUser(task.getIdAssign()).getUsername() ;
				        					}
				        					%>
					        				<li id="task-assign-<%= task.getId() %>">Responsable : <%= val %></li>
					        			</ul>
			
										<!-- 
											Si l'utilisateur est connecté, fait partie des invités et que la tâche lui ai attribué alors on
												lui ajoute les bouton de suppression et mise à jour de la tâche
											Si l'utilisateur fait partie des invité, on lui ajoute le bouton d'ajout de tâche
										-->
										
					        			<%
	
					        			if (currentUser != null && 
					        					((kanban.getIdOwner() == currentUser.getId()) || 
					        					 (task.getIdAssign() == 0 && guestsUsername.contains(currentUser.getUsername())) ||
					        					 (task.getIdAssign() == currentUser.getId()))) { %>
					        				<div class="buttons">
						        				 <form id="delete-<%= task.getId() %>" action="" method="post">
						        					<input type="hidden" name="delete-task" value="<%= task.getId() %>">
						        					<input type="button" value="Supprimer" onClick='validateAction("Etes-vous sûr de vouloir supprimer cette tâche ?\nCette action sera irréversible", "delete-<%= task.getId() %>")'>
						        				</form> <%
										}
					        			if (currentUser != null && 
					        					((kanban.getIdOwner() == currentUser.getId()) || 
					        					 (task.getIdAssign() == 0 && guestsUsername.contains(currentUser.getUsername())) ||
					        					 (task.getIdAssign() == currentUser.getId()))) { %>
						        				<button onClick="setUpdateTask(<%= task.getId() %>)">Mettre à jour</button>
						        			</div> <%
										}
					        			%>
					        		</li>
					        		<%
			  					}
			     		 	}
			  	 		}
			  			%>
						</ul>
			
						<!-- 
							Popup d'ajout de tâche. Une popup est spécifique à une colonne
						-->
						
						<%
						if (currentUser != null && (kanban.getIdOwner() == currentUser.getId() || guestsUsername.contains(currentUser.getUsername()))) {
							%>
							<button onclick="document.getElementById('modal-wrapper-<%= column %>').style.display='block'" id="addTaksButton">Ajouter une tâche</button>
							
							<div id="modal-wrapper-<%= column %>" class="modal">
								<form class="modal-content animate" method="post">
									<div class="data-container">
										<span onclick="document.getElementById('modal-wrapper-<%= column %>').style.display='none'" class="close" title="Close popup">&times;</span>
										<h1 style="text-align: center">Ajouter une tâche</h1>
									</div>
									
									<div class="form-container">
										<span class="assign-span">
											<label for="assignment-description-<%= column %>">Description <span id="assignement-description-information-<%= column %>"></span> : </label><br/>
											<textarea maxlength='<%= sizeTextArea %>' id="assignment-description-<%= column %>" placeholder="Description" name="description" required></textarea>
											<script>
											var textarea = document.getElementById("assignment-description-<%= column %>");
											
											textarea.addEventListener("input", function(){
											    var maxlength = this.getAttribute("maxlength");
											    var currentLength = this.value.length;
										    	document.getElementById("assignement-description-information-<%= column %>").innerHTML = "(" + (maxlength - currentLength) + " caractères restants ) ";
											});
											</script>
										</span>
										<span class="assign-span">
											<label for="assign-<%= column%>">Choisir la personne responsable de la tâche : </label>
											<select id="assign-<%= column%>" name="assignment">
												<option value="">Pas assignée</option>
												<optgroup label="Gestionnaire">
													<option value="<%= kanbanOwner.getUsername() %>"><%= kanbanOwner.getUsername() %></option>
												</optgroup>
												<optgroup label="Invités">
													<%
													for (User guest : guests) {
														%>
														<option value="<%= guest.getUsername()%>"><%= guest.getUsername() %></option>
														<%
													}
													%>
												</optgroup>
											</select>
										</span>
										<input id="add-task-date-<%= column %>" type="date" placeholder="Deadline" name="deadline">
										<script>document.getElementById("add-task-date-<%= column %>").valueAsDate = new Date()</script>
										<input type="hidden" name="column" value="<%= column %>">
										<button type="submit">Ajouter</button>
									</div>
								</form>
							</div>
						<% 
						}
						%>
					</div>
					<%	
		 		}
		 		%>
		 		
				<!-- 
					Création de la popup de modification des informations d'une tâche
					Les informations contenus dans son formulaire sont mise à jour par le script au début, en mettant à jour leur
						valeur en fonction de la tâche à mettre à jour
				-->
		 		
		 		<div id="modal-wrapper-update" class="modal">
					<form class="modal-content animate" method="post">
						<div class="data-container">
							<span onclick="document.getElementById('modal-wrapper-update').style.display='none'" class="close" title="Close popup">&times;</span>
							<h1 style="text-align: center">Modifier la tâche</h1>
						</div>
						
						<div class="form-container">
							<span class="assign-span">
								<label for="assignment-description">Description <span id="assignement-description-information"></span> : <br/>
									<span style="font-style : oblique;">balises ** pour mettre en gras</span><br/>
									<span style="font-style : oblique;">balises __ pour souligner</span><br/>
								</label><br/>
										
								<textarea maxlength='<%= sizeTextArea %>' id="assignment-description" placeholder="Description" name="description" required></textarea>
								<script>
								var textarea = document.getElementById("assignment-description")
			
								textarea.addEventListener("input", function(){
								    var maxlength = this.getAttribute("maxlength");
								    var currentLength = this.value.length;
							    	document.getElementById("assignement-description-information").innerHTML = "(" + (maxlength - currentLength) + " caractères restants ) ";
								});
								</script>
							</span>
							<span class="assign-span">
								<label for="assign-update">Choisir la personne responsable de la tâche : </label>
								<select id="assign-update" name="assignment">
									<option id="assignment-null" value="">Pas assignée</option>
									<optgroup label="Gestionnaire">
										<option id="assignment-<%= kanbanOwner.getUsername() %>"
												value="<%= kanbanOwner.getUsername() %>"><%= kanbanOwner.getUsername() %></option>
									</optgroup>
									<optgroup label="Invités">
										<%
										for (User guest : guests) {
											%>
											<option id="assignment-<%= guest.getUsername()%>"
													value="<%= guest.getUsername()%>"><%= guest.getUsername() %></option>
											<%
										}
										%>
									</optgroup>
								</select>
							</span>
							<span class="column-span">
								<label for="column-update">Choisir une colonne : </label>
								<select id="column-update" name="choose-column">
									<optgroup label="Colonnes">
										<%
										for (String column : kanban.getListColumns()) {
											%>
											<option id="column-<%= column%>"
													value="<%= column%>"><%= column %></option>
											<%
										}
										%>
									</optgroup>
								</select>
							</span>
							<input id="assignment-deadline" type="date" placeholder="Deadline" name="deadline" value="">
							<input id="assignment-hidden" type="hidden" name="update-task">
							<button type="submit">Modifier</button>
						</div>
					</form>
				</div>
		  	</div>
		  	
			<!-- 
				Partie d'ajout et suppression d'un invité. Cette partie n'est affiché que si l'utilisateur connecté
					est le gestionnaire du kanban
			-->
		  	
		   	<%
		   	
		   	if (currentUser != null && kanbanOwner.getUsername().equals(currentUser.getUsername())) { %>
		   	<div class="formsInvite">
		   		<form id="form-invite-user" class="invite-user" method="post">
		   			<button type="submit">Inviter un utilisateur</button>
					<input type="text" placeholder="Invité" name="invite-user" required>
		   		</form>
		   		<form id="form-remove-user" class="remove-user" method="post">
		   			<button type="button" onClick='validateAction("Etes-vous sûr de vouloir retirer cette utilisateur des invités ?\nCette action sera irréversible et retira la liste des attributions !", "form-remove-user")'>Retirer un utilisateur</button>
					<select id="assign-update" name="remove-user" required>
						<%
						for (User guest : guests) {
							%>
							<option value="<%= guest.getUsername()%>"><%= guest.getUsername() %></option>
							<%
						}
						%>
					</select>
		   		</form>
		   	</div>	
		   	<%	
		   	}
	  	}
	  	if (request.getAttribute("infoAddGuest") != null) { %>
			<p style="text-align: center;"><%= request.getAttribute("infoAddGuest") %></p>
		<%
		}
	  	if (request.getAttribute("infoRemoveGuest") != null) { %>
			<p style="text-align: center;"><%= request.getAttribute("infoRemoveGuest") %></p>
		<% 
		}
	  	if (request.getAttribute("infoRemoveTask") != null) { %>
			<p style="text-align: center;"><%= request.getAttribute("infoRemoveTask") %></p>
		<% 
		}
	  	if (request.getAttribute("infoUpdateTask") != null) { %>
			<p style="text-align: center;"><%= request.getAttribute("infoUpdateTask") %></p>
		<% 
		}
	  	if (request.getAttribute("infoAddTask") != null) { %>
			<p style="text-align: center;"><%= request.getAttribute("infoAddTask") %></p>
		<% 
		}
	  	%>
	</body>
</html>