<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 
	Page permettant d'initialiser la configuration de la connexion à la base de données
	Contient un formulaire, avec 4 champs
	Aucun affichage à l'aide de css n'est fait car il ne s'agit que d'une page de configuration qui sera 
		utilisé qu'une seule fois après le déploiement
 -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Database Configuration</title>
	</head>
	
	<body>
        <form id="form" action="" method="POST">
            <label>Scheme : <input type="text" name="scheme" placeholder="Scheme of database" 
            	value="<%= (request.getAttribute("conf_scheme") != null ? request.getAttribute("conf_scheme") : "")%>" required></label><br>
            <label>Address : <input type="text" name="adress" placeholder="Adress of database" 
            	value="<%= (request.getAttribute("conf_adress") != null ? request.getAttribute("conf_adress") : "")%>" required></label><br>
            <label>Username : <input type="text" name="username" placeholder="Username" 
            	value="<%= (request.getAttribute("conf_username") != null ? request.getAttribute("conf_username") : "")%>" required></label><br>
            <label>Password : <input type="password" name="password" placeholder="Password" 
            	value="<%= (request.getAttribute("conf_password") != null ? request.getAttribute("conf_password") : "")%>" required></label><br>
	        <%
	        if (request.getAttribute("conf") != null) {%>
	        <input type="hidden" name="type" value="validate">
            <input type="submit" value="Save database">
	        <%
	        } else {%>
	        <input type="hidden" name="type" value="check">
            <input type="submit" value="Check configuration">
	        <%
	        }
	        %>
        </form>
        <div>
        	${infoConfiguration }
        </div>
	</body>
</html>