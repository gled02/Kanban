<!-- Vue qui permet d'afficher le compte d'un utilisateur.
	Cette vue est traitée par la servlet ProfileServlet.
	Elle contient les informations pour les kanbans donc l'utilisateur est gestionnaire,
	pour les kanbans où l'utilisateur est connecté et l'ensemble de ses taches dans les deux cas. -->
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="domain.Kanban"%>
<%@page import="dao.*"%>
<%@page import="domain.*"%>
<%@page import="java.util.*"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Profile</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/printKanbans.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
	<script src="${pageContext.request.contextPath}/js/menu.js"></script>
    <script src="${pageContext.request.contextPath}/js/textareaParser.js"></script>
	<script>
		function validateAction(text, id) {
    		if (confirm(text)) {
      	 	 document.getElementById(id).submit();
    		}
		}
	</script>
	<style>
		select {
			height : 25px;
			width : 250px;
		}
	</style>
</head>
<body>
	<%
		User user = session.getAttribute("user") != null ? (User) session.getAttribute("user") : null;
	%>
	<ul class="ulMenu">
			<li id="img">
			 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo"  width="80" height="80"> 
			</li>
			<li id="page"><%=user.getName()%> <%=user.getSurname()%>, Vous êtes connecté(e) en tant que <%=user.getUsername() %></li>
			<li id="menuBlock">
				<div class="dropdown">
  					<button onclick="myFunction()" class="dropbtn">Menu</button>
 	 				<div id="myDropdown" class="dropdown-content">
    					<jsp:include page="links.jsp"/>
  					</div>
  				</div>	
  			</li>	
	</ul>
	<% 
		if (request.getAttribute("kanbans") != null) {
			List<Kanban> kanbans = (List<Kanban>) request.getAttribute("kanbans");
 	 		UserDAO userDao = DAOFactory.getInstance().getUtilisateurDao();
	%>
	<div class="selection-display">
		  <p>Sélectionnez un affichage:</p>
		  <input type="radio" id="kanbans-gest" name="display-div" value="kanbans-gest" onclick="handleClick(this);" >
		  <label for="kanbans-gest">Liste des kanbans gestionnaire</label><br>
		  <input type="radio" id="kanbans-guest" name="display-div" value="kanbans-guest" onclick="handleClick(this);" >
		  <label for="kanbans-guest">Liste des kanbans invités</label><br>
		  <input type="radio" id="tasks" name="display-div" value="tasks" onclick="handleClick(this);" >
		  <label for="tasks">Liste des tâches</label><br>
		  <input type="radio" id="affected-tasks" name="display-div" value="affected-tasks" onclick="handleClick(this);" >
		  <label for="affected-tasks">Liste des tâches affectées</label><br>
	</div>
	<div class="element-profile-select" id="profile-kanbans-gest" style="display: none;">
	  	<h3 style="font-style: italic; text-align: center;">La liste des kanbans que vous gérez :</h3>
	  	<div class="divKanbans">
	  	 	<ul class="ulKanbans">
	     		<%
	     			Collections.sort(kanbans, new Comparator<Kanban>() {
	     	    		@Override
	     	   			public int compare(Kanban left, Kanban right) {
	     	        		return left.getNameKanban().compareTo(right.getNameKanban());
	     	   			}
	     			});
	     			for (Kanban kanban : kanbans) { 
	     		%>
				<li>
	        		<form action="${pageContext.request.contextPath}/kanban" method= "post">
	        			<input type="submit" value="<%= kanban.getNameKanban() %>"/>
	        			<input type="hidden" name="kanban-value"  value="<%= kanban.getId() %>" />
					</form>	
					<ul class="ulDataKanbans">
						<li>Par : <%= userDao.getUser(kanban.getIdOwner()).getName()%></li>
						<li>Description : <%= kanban.getDescription() %></li>
						<li> 
							<form id="delete-<%= kanban.getId() %>" action="" method="post">
								<input type="hidden" name="delete-kanban" value="<%= kanban.getId() %>">
								<input type="button" value="Delete" onClick='validateAction("Etes-vous sûr de vouloir supprimer ce kanban ?\nCette action sera irréversible", "delete-<%= kanban.getId() %>")'>
							</form>
						</li>
					</ul>
				</li>
	   			<%
	      			}
	   			%>
	  	 	</ul>
	  	 </div>	
	</div>
   <%
  		}
  	%>
  	<% 
 	 	if (request.getAttribute("kanbans-guests")  != null) {
 			List<Kanban> kanbansGuests = (List<Kanban>) request.getAttribute("kanbans-guests");
 	 		 UserDAO userDao = DAOFactory.getInstance().getUtilisateurDao();
  	%>
	<div class="element-profile-select" id="profile-kanbans-guest" style="display: none;">
	  	<h3 style="font-style: italic; text-align: center;">La liste des kanbans auxquels vous participez :</h3>
	  	<div class="divKanbans">
	   		<ul class="ulKanbans">
	    		<%
	     			Collections.sort(kanbansGuests, new Comparator<Kanban>() {
	 	    			@Override
	 	   				public int compare(Kanban left, Kanban right) {
	 	        			return left.getNameKanban().compareTo(right.getNameKanban());
	 	   				}
	 			 	});
	     			for (Kanban kanban : kanbansGuests) { 
	     		%>
	       	 	<li>
	        		<form action="${pageContext.request.contextPath}/kanban" method= "post">
	           			<input type="submit" value="<%= kanban.getNameKanban() %>"/>
	        			<input type="hidden" name="kanban-value"  value="<%= kanban.getId() %>" />
	        		</form>	
	        		<ul class="ulDataKanbans">
	        			<li>Par : <%= userDao.getUser(kanban.getIdOwner()).getName()%></li>
	        			<li>Description : <%= kanban.getDescription() %></li>
	        		</ul>
	        	</li>
	   			<%
	      			}
	   			%>
	   		</ul>
	   	</div>
	</div>
	<%
  		}
  	%>
  	<% 
		TaskDAO taskDao = DAOFactory.getInstance().getTaskDao();
		KanbanDAO kanbanDao = DAOFactory.getInstance().getKanbanDao();
  		if (request.getAttribute("allTasks") != null) {
			List<Task> allTasks = (List<Task>) request.getAttribute("allTasks");
		
  	%>
	<div class="element-profile-select" id="profile-tasks" style="display: none;">
	  	<form action="" method="post">
	  		<table>
	  			<tr>
	  				<th align="center" colspan="6"><h3 style="font-style: italic;">La liste des tâches qui vous sont affectées globalement :</h3></th>
	  			</tr>
	  			<tr>
	  				<th align="right">Taches : </th>
	  				<td>
	  					<select name="tasks" id="tasks" onchange="this.form.submit();">
	  						<option value="0">Choisissez une tache</option>
	  						<%
	  							if (allTasks != null) { 
	  								Collections.sort(allTasks, new Comparator<Task>() {
	  					 	    		@Override
	  					 	   			public int compare(Task left, Task right) {
	  					 	        		return left.getId().compareTo(right.getId());
	  					 	   			}
	  					 			 });
	  								for (Task task : allTasks) {
			  							if (request.getParameter("tasks") != null &&
			  									Long.valueOf(request.getParameter("tasks")) == task.getId()) {
			  							%>
	  							<option value="<%=task.getId()%>" selected><%= kanbanDao.getKanban(task.getIdKanban()).getNameKanban()%> : tâche numéro <%=task.getId()%></option>
	  									<%} else { %>
	  							<option value="<%=task.getId()%>"><%= kanbanDao.getKanban(task.getIdKanban()).getNameKanban()%> : tâche numéro <%=task.getId()%></option><%
	  									}
	  								}
	  							}
	  						%>
	  					</select>
	  				</td>
	  			</tr>
	  		</table>
	  		<%
	  			if (request.getParameter("tasks") != null &&  Long.valueOf(request.getParameter("tasks")) != 0) { 
	  		%>
	  		<div>
	  			<p style="text-align: center;"> Tache choisie : <%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getId()%> </p>
	  			<p style="text-align: center;"> Les informations concernant la tache choisie : </p>
	  			<div class="divKanbans">
	  				<ul class="ulKanbans">
						<li>ID : <%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getId()%></li>
						<li id="choosed-task-<%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getId()%>"></li>
						<script>
						document.getElementById("choosed-task-<%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getId()%>").innerHTML = parseText("Description : <%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getDescription()%>");
						</script>
						<li>Date : <%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getDate() == null ? "Pas définie" : taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getDate()%></li>
						<li>Kanban : <%=kanbanDao.getKanban(taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getIdKanban()).getNameKanban()%></li>
						<li>Colonne : <%=taskDao.getTask(Long.valueOf(request.getParameter("tasks"))).getNameColumn()%></li>
					<%  
						}
	     			%>
	  				</ul>
	  			</div>
	  		</div>	
		</form>
	</div>
  	<% 
  		}
  	
  		List<Kanban> allKanbans = new ArrayList<Kanban>();
		if (request.getAttribute("kanbans") != null) {
			List<Kanban> kanbans = (List<Kanban>) request.getAttribute("kanbans");
  			allKanbans.addAll(kanbans);
  		}
 	 	if (request.getAttribute("kanbans-guests")  != null) {
 			List<Kanban> kanbansGuests = (List<Kanban>) request.getAttribute("kanbans-guests");
  			allKanbans.addAll(kanbansGuests);
  		}
  	%>
	<div class="element-profile-select" id="profile-affected-tasks" style="display: none;">
	  	<form action="" method="post">
	  		<table>
	  			<tr>
	  				<th align="center" colspan="6"><h3 style="font-style: italic;">La liste des tâches qui vous sont affectées pour un kanban donné : </h3></th>
	  			</tr>
	  			<tr>
	  				<th align="right">Kanbans : </th>
	  				<td>
	  					<select name="kanbans" id="kanbans" onchange="this.form.submit();">
	  						<option value="0">Choisissez un kanban</option>
	  						<% 
	  							if (allKanbans != null) { 
	  								for (Kanban kanban : allKanbans) { 
			  							if (request.getParameter("kanbans") != null && kanbanDao.getKanban(Long.valueOf(request.getParameter("kanbans"))).getId() == kanban.getId()) {
			  							%>
  								<option value="<%=kanban.getId()%>" selected><%=kanban.getNameKanban() %></option>
	  									<%} else { %>
  								<option value="<%=kanban.getId()%>"><%=kanban.getNameKanban() %></option><%
	  									}
	  								}
	  							}
	  						%>
	  					</select>
	  				</td>
	  			</tr>
	  		</table>
	  		<%
	  			if (request.getParameter("kanbans") != null && Long.valueOf(request.getParameter("kanbans")) != 0) { 
	  		%>
	  		<div>
	  			<p style="text-align: center;"> Kanban choisi : <%=kanbanDao.getKanban(Long.valueOf(request.getParameter("kanbans"))).getNameKanban()%> </p>
	  			<p style="text-align: center;"> La liste des taches : </p>
	  			<div class="divKanbans">
	  				<ul class="ulKanbans">
	  					<% 
	 						List<Task> tasksByKanban = taskDao.getKanbanAssignedTasks(kanbanDao.getKanban(Long.valueOf(request.getParameter("kanbans"))), (User) session.getAttribute("user"));
	 						Collections.sort(tasksByKanban, new Comparator<Task>() {
			 	    			@Override
			 	   				public int compare(Task left, Task right) {
			 	    				if (left.getDate() == null && right.getDate() == null) {
			 	    					return 0;
			 	    				} else if (left.getDate() == null) {
			 	    					return -1;
			 	    				} else if (right.getDate() == null) {
			 	    					return 1;
			 	    				} else {
				 	        			return left.getDate().compareTo(right.getDate());
			 	    				}
			 	   				}
			 				});
							for (Task task : tasksByKanban) { %>
								<li>ID : <%= task.getId() %>
									<ul class="ulDataKanbans">
										<li id="desc-task-<%= task.getId()%>"></li>
										<script>
										document.getElementById("desc-task-<%= task.getId()%>").innerHTML = parseText("Description : <%= task.getDescription() %>");
										</script>
										<li>Date : <%= task.getDate() == null ? "Pas définie" : task.getDate()%></li>
									</ul>
								</li>
					<%  
							}
	     				}
	     			%>
	  				</ul>
	  			</div>
	  		</div>
	  	</form>
  	</div>
  	<div id="divInfoDelete" style="display: flex; justify-content: center;">
  	<%
		if (request.getAttribute("infoDeleteKanban") != null) {
			if(request.getAttribute("infoDeleteKanban").toString().startsWith("Error")) { %>
				<p style="color:red; text-align=center; font-family: Georgia, serif; font-size: 20px; font-style: italic;">Erreur pendant la suppression.</p>
		<% 	
			}
			if (request.getAttribute("infoDeleteKanban").toString().startsWith("Successful")) { %>
				<p style="color:green; text-align=center; font-family: Georgia, serif; font-size: 20px; font-style: italic;">Kanban supprimé avec succès.</p>
		<% 	}
		}
	%>	
	</div>
	<script>
	var selectedDiv = "<%= (request.getParameter("tasks") != null ? "profile-tasks" : (request.getParameter("kanbans") != null ? "profile-affected-tasks" : null)) %>";
	<%
	if (request.getParameter("tasks") != null) {
	%>
	document.getElementById("profile-tasks").style.display ='block';
	document.getElementById("tasks").checked = true;
	<%
	} else if (request.getParameter("kanbans") != null) {
	%>
	document.getElementById("profile-affected-tasks").style.display ='block';
	document.getElementById("affected-tasks").checked = true;
	<%
	}
	%>
	function handleClick(rad) {
		document.getElementById("divInfoDelete").style.display = 'none';
		switch(rad.value) {
			case "kanbans-gest":
				if (selectedDiv != "null" && selectedDiv != "profile-kanbans-gest") {
					document.getElementById(selectedDiv).style.display ='none';
				}
				if (selectedDiv != "profile-kanbans-gest") {
					document.getElementById("profile-kanbans-gest").style.display ='block';
					selectedDiv = "profile-kanbans-gest";
				}
				break;
			case "kanbans-guest":
				if (selectedDiv != "null" && selectedDiv != "profile-kanbans-guest") {
					document.getElementById(selectedDiv).style.display ='none';
				}
				if (selectedDiv != "profile-kanbans-guest") {
					document.getElementById("profile-kanbans-guest").style.display ='block';
					selectedDiv = "profile-kanbans-guest";
				}
				break;
			case "tasks":
				if (selectedDiv != "null" && selectedDiv != "profile-tasks") {
					document.getElementById(selectedDiv).style.display ='none';
				}
				if (selectedDiv != "profile-tasks") {
					document.getElementById("profile-tasks").style.display ='block';
					selectedDiv = "profile-tasks";
				}
				break;
			case "affected-tasks":
				if (selectedDiv != "null" && selectedDiv != "profile-affected-tasks") {
					document.getElementById(selectedDiv).style.display ='none';
				}
				if (selectedDiv != "profile-affected-tasks") {
					document.getElementById("profile-affected-tasks").style.display ='block';
					selectedDiv = "profile-affected-tasks";
				}
				break;
		  	default:
			    console.log("La valeur ${rad.value} n'est pas présente !");
		}
				
	}
	</script>
</body>
</html>