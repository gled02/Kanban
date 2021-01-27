<!-- Vue qui permet d'afficher le formulaire pour qu'un utilisateur se connecte.
	Cette vue est traitée par la servlet LoginServlet. -->
<%@ page contentType="text/html; UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Login</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
		<script src="${pageContext.request.contextPath}/js/menu.js"></script> 
	</head>
	<body>
		<ul class="ulMenu">
			<li id="img">
			 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo"  width="80" height="80"> 
			</li>
			<li id="page">Connexion</li>
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
				<h1>Connexion</h1>
				<span>
					<label for="username">Nom d'utilisateur :</label>
					<input type="text" id="username" placeholder="Nom d'utilisateur" name="username" required>
				</span>
				<span>
					<label for="password">Mot de passe :</label>
					<input type="password" id="password" placeholder="Mot de passe" name="password" required>
				</span>
				<span>
					<input type="submit" value="Valider">
				</span>
				<%
					if (request.getAttribute("infoLogin") != null) {
						if(request.getAttribute("infoLogin").toString().startsWith("Error")) { %>
							<p style="color:red">Nom d'utilisateur incorrect.</p>
				<% 	
						}
						if (request.getAttribute("infoLogin").toString().startsWith("Data in")) { %>
							<p style="color:red">Mot de passe incorrect.</p>
				<%		}
					}
				%>
			</form>
		</div>
	</body>
</html>
    