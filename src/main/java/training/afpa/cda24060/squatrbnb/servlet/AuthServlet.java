package training.afpa.cda24060.squatrbnb.servlet;

import training.afpa.cda24060.squatrbnb.dao.UtilisateurDAO;
import training.afpa.cda24060.squatrbnb.model.*;
import training.afpa.cda24060.squatrbnb.model.enums.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import training.afpa.cda24060.squatrbnb.utilitaires.PasswordUtil;

import java.io.IOException;

import static training.afpa.cda24060.squatrbnb.model.enums.Role.HOTE;


/**
 * Servlet d'authentification
 * Gère login, inscription, logout
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/*"})
public class AuthServlet extends HttpServlet {
    
    private UtilisateurDAO utilisateurDAO;
    
    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "/";
        
        switch (pathInfo) {
            case "/login" -> showLoginForm(request, response);
            case "/register" -> showRegisterForm(request, response);
            case "/logout" -> logout(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "/";
        
        switch (pathInfo) {
            case "/login" -> processLogin(request, response);
            case "/register" -> processRegister(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }
    
    /**
     * Affiche le formulaire de connexion
     */
    private void showLoginForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Si déjà connecté, rediriger vers le dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utilisateur") != null) {
            Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
            response.sendRedirect(request.getContextPath() + user.getDashboardUrl());
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
    
    /**
     * Affiche le formulaire d'inscription
     */
    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Passer les rôles disponibles pour le formulaire
        request.setAttribute("roles", new Role[]{Role.LOCATAIRE, HOTE});
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }
    
    /**
     * Traite la connexion
     */
    private void processLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");
        String remember = request.getParameter("remember");
        
        // Validation
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            request.setAttribute("error", "Email et mot de passe requis");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Authentification
        UtilisateurDAO.AuthResult result = utilisateurDAO.authentifier(email, motDePasse);
        
        if (result.success()) {
            Utilisateur utilisateur = result.utilisateur();
            
            // Créer la session
            HttpSession session = request.getSession(true);
            session.setAttribute("utilisateur", utilisateur);
            session.setAttribute("utilisateurId", utilisateur.getId());
            session.setAttribute("roles", utilisateur.getRoles());
            session.setAttribute("isHote", utilisateur.hasRole(HOTE));
            
            // Attributs pratiques selon le type d'utilisateur
            if (utilisateur instanceof Hote hote) {
                session.setAttribute("hote", hote);
            } else if (utilisateur instanceof Locataire locataire) {
                session.setAttribute("locataire", locataire);
            } else if (utilisateur instanceof HoteLocataire hl) {
                session.setAttribute("hoteLocataire", hl);
            } else if (utilisateur instanceof Admin admin) {
                session.setAttribute("admin", admin);
            }

            
            // Durée de session
            if ("on".equals(remember)) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 jours
            } else {
                session.setMaxInactiveInterval(30 * 60); // 30 min
            }
            
            log("Connexion: " + email + " - Type: " + utilisateur.getClass().getSimpleName() + 
                " - Rôles: " + utilisateur.getRolesAsString());

//                    response.sendRedirect(request.getContextPath() + "/biens");

            // Redirection
//            response.sendRedirect("/hote/mes-biens");

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            System.out.println("=== DEBUG LOGIN ===");
            System.out.println("redirectAfterLogin: " + redirectUrl);
            System.out.println("getDashboardUrl(): " + result.utilisateur().getDashboardUrl());
            System.out.println("Classe utilisateur: " + result.utilisateur().getClass().getSimpleName());
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                System.out.println("Redirection vers: " + redirectUrl);
                response.sendRedirect(redirectUrl);
            } else {
                String dashboard = request.getContextPath() + result.utilisateur().getDashboardUrl();
                System.out.println("Redirection vers dashboard: " + dashboard);
                response.sendRedirect(dashboard);
            }
            
        } else {
            // Échec
            String errorMessage = switch (result.message()) {
                case "EMAIL_INCONNU" -> "Aucun compte avec cet email";
                case "MOT_DE_PASSE_INCORRECT" -> "Mot de passe incorrect";
                case "COMPTE_DESACTIVE" -> "Ce compte a été désactivé";
                default -> "Erreur de connexion";
            };
            
            request.setAttribute("error", errorMessage);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Traite l'inscription
     */
    private void processRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Récupération des paramètres
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");
        String confirmMotDePasse = request.getParameter("confirmMotDePasse");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String telephone = request.getParameter("telephone");
        String roleStr = request.getParameter("role");

        // Validation
        StringBuilder errors = new StringBuilder();
        
        if (email == null || email.isBlank()) {
            errors.append("Email requis. ");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("Email invalide. ");
        } else if (utilisateurDAO.emailExists(email)) {
            errors.append("Cet email est déjà utilisé. ");
        }
        
        if (motDePasse == null || motDePasse.length() < 2) {
            errors.append("Mot de passe minimum 2 caractères. ");
        } else if (!motDePasse.equals(confirmMotDePasse)) {
            errors.append("Les mots de passe ne correspondent pas. ");
        }
        
        if (nom == null || nom.isBlank()) {
            errors.append("Nom requis. ");
        }
        
        if (prenom == null || prenom.isBlank()) {
            errors.append("Prénom requis. ");
        }
        
        // Parser le rôle
        Role role;
        try {
            role = Role.fromCode(roleStr);
            if (role == Role.ADMIN) {
                role = Role.LOCATAIRE;
            }
        } catch (Exception e) {
            role = Role.LOCATAIRE;
        }
        
        // Si erreurs, retourner au formulaire
        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString().trim());
            request.setAttribute("email", email);
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            request.setAttribute("telephone", telephone);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }
        

        // Hash du mot de passe
        String motDePasseHash = PasswordUtil.hashpw(motDePasse);
        
        // Inscription
        UtilisateurDAO.InscriptionResult result = utilisateurDAO.inscrire(
            email, motDePasseHash, nom, prenom, telephone, role
        );
        
        if (result.success()) {
            // Connexion automatique
            UtilisateurDAO.AuthResult authResult = utilisateurDAO.authentifier(email, motDePasseHash);
            
            if (authResult.success()) {
                Utilisateur utilisateur = authResult.utilisateur();
                
                HttpSession session = request.getSession(true);
                session.setAttribute("utilisateur", utilisateur);
                session.setAttribute("utilisateurId", utilisateur.getId());
                session.setAttribute("roles", utilisateur.getRoles());
                session.setMaxInactiveInterval(30 * 60);
                
                session.setAttribute("flash", "Bienvenue " + prenom + " ! Votre compte a été créé.");
                
                log("Inscription: " + email + " - Rôle: " + role);
                
                response.sendRedirect(request.getContextPath() + utilisateur.getDashboardUrl());
            } else {
                // Inscription OK mais connexion échouée
                request.setAttribute("success", "Compte créé ! Vous pouvez maintenant vous connecter.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
            
        } else {
            request.setAttribute("error", "Erreur: " + result.message());
            request.setAttribute("email", email);
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            request.setAttribute("roles", new Role[]{Role.LOCATAIRE, HOTE});
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
    
    /**
     * Déconnexion
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
            if (user != null) {
                log("Déconnexion: " + user.getEmail());
            }
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/auth/login?logout=1");
    }
    

}
