<%@ page contentType="text/html; UTF-8" pageEncoding="UTF-8"%>
<!-- 
	Page permettant de créer un nouveau compte utilisateur
	Contient un formulaire avec 4 champs : username, nom, prenom et mot de passe
 -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Registration</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
		<script src="${pageContext.request.contextPath}/js/menu.js" ></script>
	</head>
	<body>
		<ul class="ulMenu">
			<li id="img">
			 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo"  width="80" height="80"> 
			</li>
			<li id="page">S'enregistrer</li>
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
			Partie contenant le formulaire d'inscription
		 -->
		
		<div id="container">
			<form method="post">
				<h1>S'enregistrer</h1>
				<span>
					<label for="username">Nom de l'utilisateur :</label>
					<input type="text" id="username" placeholder="Nom de l'utilisateur" name="username" required>
				</span>
				<span>
					<label for="name">Prénom :</label>
					<input type="text" id="name" placeholder="Prenom" name="name" required>
				</span>
				<span>
					<label for="surname">Nom :</label>
					<input type="text" id="surname" placeholder="Nom" name="surname" required>
				</span>
				<span>
					<label for="password">Mot de passe :</label>
					<input type="password" id="password" placeholder="Mot de passe" name="password" required>
				</span>
				<span>
					<input type="submit" value="Valider">
				</span>
		
				<!-- 
					On affiche un message à l'utilisateur en fonction de l'erreur renvoyé côté serveur
				 -->
				 
				<%
					if (request.getAttribute("infoRegistration") != null) {
						if(request.getAttribute("infoRegistration").toString().startsWith("Username:")) { %>
							<p style="color:red">Nom d'utilisateur existe.</p>
				<% 	
						}
						if (request.getAttribute("infoRegistration").toString().startsWith("Password")) { %>
							<p style="color:red">Erreur mot de passe.</p>
				<%		}
						if (request.getAttribute("infoRegistration").toString().startsWith("Registration")) { %>
							<p style="color:red">L'enregistrement a échoué.</p>
				<%		}
					}
				%>
			</form>
		</div>
	</body>
</html>
