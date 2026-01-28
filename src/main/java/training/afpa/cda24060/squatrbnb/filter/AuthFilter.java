package training.afpa.cda24060.squatrbnb.filter;

import jakarta.servlet.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import training.afpa.cda24060.squatrbnb.model.Utilisateur;
import training.afpa.cda24060.squatrbnb.model.enums.Role;

import java.io.IOException;

@WebFilter(urlPatterns = {"/hote/*", "/locataire/*", "/admin/*", "/compte/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        Utilisateur utilisateur = (session != null)
                ? (Utilisateur) session.getAttribute("utilisateur")
                : null;

        if (utilisateur == null) {
            // Non connecté → sauvegarder l'URL demandée et rediriger vers login
            String requestedUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestedUrl += "?" + queryString;
            }

            // Sauvegarder pour après le login
            request.getSession(true).setAttribute("redirectAfterLogin", requestedUrl);

            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Vérifier les rôles selon l'URL...
        String path = request.getRequestURI();

        if (path.contains("/hote/") && !utilisateur.hasRole(Role.HOTE)) {
            response.sendError(403, "Accès réservé aux hôtes");
            return;
        }

        if (path.contains("/locataire/") && !utilisateur.hasRole(Role.LOCATAIRE)) {
            response.sendError(403, "Accès réservé aux locataires");
            return;
        }

        if (path.contains("/admin/") && !utilisateur.hasRole(Role.ADMIN)) {
            response.sendError(403, "Accès réservé aux administrateurs");
            return;
        }

        // OK, continuer
        chain.doFilter(request, response);
    }
}


