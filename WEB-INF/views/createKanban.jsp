<%@ page contentType="text/html; UTF-8" pageEncoding="UTF-8"%>
<!-- 
	Page permettant de créer un nouveau kanban
	Contient un formulaire, avec 4 informations différentes, le nom, la description, une checkbox
		afin de rendre public ou non le kanban, et la liste des colonnes du kanban
	Cette page n'est accessible que lorsque l'utilisateur est connecté
	Le nombre maximum de colonne possible d'un kanban est de 7
 -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Create a Kanban | ${user.username}</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
		<script src="${pageContext.request.contextPath}/js/menu.js"></script>
		<script>
			numberColumn = 2;
			countMax = 7;
		</script>
	</head>
	<body>
		<ul class="ulMenu">
				<li id="img">
				 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo"  width="80" height="80"> 
				</li>
				<li id="page">Créer un kanban</li>
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
			Description du formulaire de la création du kanban 
		 -->
		 
		<div id="container">
			<form method="post">
				<div>
					<label for="name">Nom du kanban :</label>
					<input type="text" id="name" placeholder="Nom du kanban" name="name" required>
				</div>
				<div>
					<label for="name">Description :</label>
					<input type="text" id="description" placeholder="Description" name="description" required>
				</div>
				<div>
					<label for="public">Est-il public? :</label>
					<input type="checkbox" id="public">
				</div>
				<div>
					<label for="list-column">Colonnes : </label>
					<ul id="list-columns">
						<li id="list-column-first">
							<input type="text" id="element-list-first" value="Stories" disabled>
						</li>
						<li id="list-column-last">
							<input type="text" id="element-list-last" value="Terminees" disabled>
						</li>
					</ul>
					<input type="hidden" id="hidden-form-value" name="hidden-form-value">
					<input type="hidden" id="hidden-check-value" name="hidden-check-value">
					<input type="button" id="btn-add-column" value="Nouvelle colonne" onclick="addColumn()">
				</div>
				<div>
					<input type="submit" id="submit-form" value="Valider">
				</div>
		
				<!--
					Description du script javascript permettant d'ajouter une colonne, et vérifier si
						il y a au maximum 7 colonnes
				 -->
		 
				<script>
					function addColumn() {
						if (numberColumn < countMax) {
							// Element de la liste
							var li = document.createElement("li");
							li.setAttribute("id", "list-column-" + numberColumn);
					
							// Champs à rentrer
							var input = document.createElement("input");
							input.setAttribute("type", "text");
							input.setAttribute("id", "element-list-" + numberColumn);
							input.required = true;
							li.appendChild(input);
					
							// Button pour supprimer la ligne
							var del = document.createElement("input");
							del.setAttribute("type", "button");
							del.setAttribute("value", "Supprimer");
							del.setAttribute("onclick", "deleteColumn(" + numberColumn + ")");
							li.appendChild(del);
					
							document.getElementById("list-columns").insertBefore(
									li, document.getElementById("list-column-last"));
							numberColumn += 1;
						} else {
						 	document.getElementById("btn-add-column").setAttribute("disabled", "disabled");
							alert("Vous ne pouvez plus ajouter des colonnes. Le nombre maximal de colonnes est " + countMax);
						}
					}
				
					function deleteColumn(nb) {
						if (numberColumn == countMax) {
							document.getElementById("btn-add-column").removeAttribute("disabled");
						}
						numberColumn -= 1;
						document.getElementById("list-columns").removeChild(document.getElementById("list-column-" + nb));
					}
				
					document.getElementById("submit-form").onclick = function() {
						var value = document.getElementById("element-list-first").value;
						var children = document.getElementById("list-columns").children;
						for (i = 1; i < children.length; ++i) {
							value += "," + children[i].children[0].value;
						}
						document.getElementById("hidden-form-value").value = value;
						document.getElementById("hidden-check-value").value = (document.getElementById("public").checked ? "on" : "off");
					}
				</script>
				<%
					if (request.getAttribute("infoCreation") != null) {
						if(request.getAttribute("infoCreation").toString().startsWith("Creation")) { %>
							<p style="color:red">Erreur pendant la création du kanban.</p>
				<% 	
						}
					}
				%>
			</form>
		</div>
	</body>
</html>
    

    
