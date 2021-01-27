<!-- Vue qui permet d'afficher la page d'accueil où sont affichés les kanbans publics.
	Cette vue est traitée par la servlet WelcomePageServlet. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import ="java.util.List"%>
<%@ page import ="domain.Kanban"%>
<%@page import="java.util.ArrayList" %>
<%@page import="dao.DAOFactory"%>
<%@page import="dao.UserDAO"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Welcome Page</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/printKanbans.css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css" />
		<script src="${pageContext.request.contextPath}/js/menu.js"></script>
	</head>
	<body>
		<ul class="ulMenu">
			<li id="img">
			 	<img src="${pageContext.request.contextPath}/css/images/logo.png" alt="logo"  width="80" height="80"> 
			</li>
			<li id="page">Page d'accueil</li>
			<li id="menuBlock">
				<div class="dropdown">
  					<button onclick="myFunction()" class="dropbtn">Menu</button>
 	 				<div id="myDropdown" class="dropdown-content">
    					<jsp:include page="links.jsp"/>
  					</div>
  				</div>	
  			</li>	
		</ul>
		<div>
    	<% 
 	 		if (request.getAttribute("publicKanbans") != null) {
 				List<Kanban> kanbans = (List<Kanban>) request.getAttribute("publicKanbans");
 	 		 	UserDAO userDao = DAOFactory.getInstance().getUtilisateurDao();
  		%>
   	 			<h3 class="kanbanPrint">La liste des kanbans publics :</h3>
   	 			<div class="divKanbans">
   					<ul class="ulKanbans">
     	 		<%
     		 		for (Kanban kanban : kanbans) { 
     			 %>
        				<li>
          				 	<form action="${pageContext.request.contextPath}/kanban" method="post">
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
   		<%
  			}
  		%>
  		</div>
	</body>
</html>
    

    
