<!-- Vue qui permet d'afficher le formulaire pour changer les données d'un utilisateur 
	Cette vue est traitée par la servlet ChangeUserDataServlet. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Utilisateur - Configuration</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
		<script src="${pageContext.request.contextPath}/js/menu.js"></script>
	</head>
	<body>
		<ul class="ulMenu">
				<li id="img">
				 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo"  width="80" height="80"> 
				</li>
				<li id="page">Changer les donneés</li>
				<li id="menuBlock">
					<div class="dropdown">
	  					<button onclick="myFunction()" class="dropbtn">Menu</button>
	 	 				<div id="myDropdown" class="dropdown-content">
	    					<jsp:include page="links.jsp"/>
	  					</div>
	  				</div>	
	  			</li>	
		</ul>
		<div id="container">
			<form method="post">
				<span>
					<label for="username">Nouveau nom d'utilisateur :</label>
					<input type="text" id="username" placeholder="Nouveau nom d'utilisateur" name="username">
				</span>
				<span>
					<label for="name">Nouveau prénom :</label>
					<input type="text" id="name" placeholder="Nouveau prenom" name="name">
				</span>
				<span>
					<label for="surname">Nouveau nom :</label>
					<input type="text" id="surname" placeholder="Nouveau nom" name="surname">
				</span>
				<span>
					<label for="password">Nouveau mot de passe :</label>
					<input type="password" id="password" placeholder="Nouveau mot de passe" name="password">
				</span>
				<span>
					<input type="submit" value="Valider">
				</span>
				<%
					if (request.getAttribute("infoUpdateUser") != null) {
						if(request.getAttribute("infoUpdateUser").toString().startsWith("Updating")) { %>
							<p style="color:red">Erreur pendant la mise à jour des données.</p>
				<% 	
						}	
					}
				%>
			</form>
		</div>	
	</body>
</html>