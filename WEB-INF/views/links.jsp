<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import ="domain.*"%>
	<%
		if (session.getAttribute("user") != null) {
     %>
     		<a href="logout">Déconnexion</a>
     <%
            if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/profile")) { %>
            		<a href="welcomePage">Page d'accueil</a>
           <% 
            } 
            if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/welcomePage")) { %>
               		<a href="profile">Profile</a>
           <% 
            }
            if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/kanban")) { %>
               		<a href="profile">Profile</a>
           <% 
            } else { 
               	if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/profile")) { %> 
               		 	<a href="createKanban">Créer un Kanban</a>
               		 	<a href="changeUserData">Modifier vos données</a>
               	<% 
               	}
            }
            if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/createKanban") ||
              	request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/changeUserData")) { %>
               		 <a href="profile">Revenir en arrière</a>
            <% 
            }
         } else { 
              if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/welcomePage") || 
            	  request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/kanban") || 
            	  request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/")) { %>
            	 	<a href="login">Connexion</a>
  	             	<a href="registration">Nouveau membre?</a>
  	             	<% 
  	             		if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/kanban")) { %>
  	             			<a href="welcomePage">Revenir en arrière</a>
  	             		<% 
  	             		} 
              } else if (request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/login") ||
               			 request.getAttribute("javax.servlet.forward.request_uri").toString().endsWith("/registration")) { %>
               				<a href="welcomePage">Page d'accueil</a>
             <% 	  
              }
         }
     %>