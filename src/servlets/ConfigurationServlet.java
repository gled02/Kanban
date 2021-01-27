package servlets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOFactory;

/**
 * Servlet permettant de gérer la page de configuration de connexion à la base de données
 * 	Cette page ce trouve à l'url /configuration et est associé à la vue /WEB-INF/views/welcomePage.jsp
 */
@WebServlet("/configuration")
public class ConfigurationServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final String VUE		 	= "/WEB-INF/views/configuration.jsp";
	private static final String VUE_HOME 	= "/WEB-INF/views/welcomePage.jsp";
   
    // COMMANDES

	/**
	 * Commande appelé lorsqu'une personne envoie des informations à la page /configuration à l'aide de la méthode POST
	 * 	Cette méthode est utilisé afin de récupérer les données de configuration de connexion à la base de données qui 
	 * 		ce fait en 2 temps:
	 * 				1) Doit vérifier que la configuration donné est correcte
	 * 				2) Enregistrer la configuration dans un fichier
	 * 
	 * La partie vérification de la configuration a été commencé mais n'as malheureusement pas pu être fini.
	 * Cependant, le code le vérifiant a déjà été mis en place sous commentaire, il faut vérifier côté DAOFactory TODO
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("type") != null) {
			
			/*
			 * Partie de vérification de la configuration de la connexion à la base de données
			 */
			if (request.getParameter("type").equals("check")) {
				//if (!DAOFactory.verifyConfiguration(request.getParameter("scheme"), request.getParameter("adress"),
				//									request.getParameter("username"), request.getParameter("password"))) { 
				//	request.setAttribute("infoConfiguration", "Base de donnée non valide");
				//} else {
					request.setAttribute("conf", true);
					request.setAttribute("conf_scheme", request.getParameter("scheme"));
					request.setAttribute("conf_adress", request.getParameter("adress"));
					request.setAttribute("conf_username", request.getParameter("username"));
					request.setAttribute("conf_password", request.getParameter("password"));
					request.setAttribute("infoConfiguration", "récupération de check");
				//}
				this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
				
			/*
			 * Partie de sauvegarde de la configuration dans le dossier /src/dao/dao.properties
			 */
			} else if (request.getParameter("type").equals("validate")) {
				request.setAttribute("conf", true);
				request.removeAttribute("conf_scheme");
				request.removeAttribute("conf_adress");
				request.removeAttribute("conf_username");
				request.removeAttribute("conf_password");
				BufferedWriter bw = null;
				try {
					
					// On récupère le chemin du fichier où enregistrer la configuration (/src/dao/dao.properties
					String path = DAOFactory.class.getResource("dao.properties").getFile();
					File file = new File(path);
					
					// On (re)créer le fichier en lui donnant tout les droits
					if (!file.exists()) {
						file.createNewFile();
					} else {
						file.delete();
						file.createNewFile();
					}
					if (!file.canRead()) {
						file.setReadable(true);
					}
					if (!file.canWrite()) {
						file.setWritable(true);
					}
						
					FileWriter fw = new FileWriter(file);
					bw = new BufferedWriter(fw);

					String scheme   = request.getParameter("scheme");
					String adress   = request.getParameter("adress");
					String username = request.getParameter("username");
					String password = request.getParameter("password");
					
					// On enregistre la configuration sous la forme 
					//		url = jdbc:mysql://adress:3306/scheme
					//		driver = com.mysql.jdbc.Driver
					//		user = username
					//		password = password
					bw.write("url = jdbc:mysql://" + adress + ":3306/" + scheme);
					bw.newLine();
					bw.write("driver = com.mysql.jdbc.Driver");
					bw.newLine();
					bw.write("user = " + username);
					bw.newLine();
					bw.write("password = " + password);
				} catch (IOException e) {
					// TODO
					request.setAttribute("infoConfiguration", "erreur écrite de la configuration : " + e);
				} finally {
					try {
						// Si tout s'est bien passé, on ferme les instances des objets et on met à jour
						//	DAOFactory en disant que la base de donnée à bien été vérifié.
						// 	A pour conséquence de faire renvoyer vrai par DAOFactory.dbIsValide()
						if (bw != null) {
							bw.close();
						}
						DAOFactory.setValidationdb(true);
					} catch (Exception e) {
						// TODO
						request.setAttribute("infoConfiguration", "erreur fermeture de la configuration : " + e);
					}
					response.sendRedirect(request.getContextPath() + "/welcomePage");
				}
			}
		}
	}

	/**
	 * Commande appelé lorsqu'une personne souhaite récupérer le contenu de la page /configuration à l'aide de la méthode GET
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.getServletContext().getRequestDispatcher(VUE).forward(request, response);
	}
}
