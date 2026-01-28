package training.afpa.cda24060.squatrbnb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import training.afpa.cda24060.squatrbnb.dao.LogementDAO;
import training.afpa.cda24060.squatrbnb.model.Logement;
import training.afpa.cda24060.squatrbnb.model.Utilisateur;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
/**
 * Servlet pour la gestion des logements
 * URLs :
 * - GET  /logement              → Liste
 * - GET  /logement/nouveau      → Formulaire ajout
 * - POST /logement/nouveau      → Créer
 * - GET  /logement/{id}         → Détail
 * - GET  /logement/{id}/modifier → Formulaire modification
 * - POST /logement/{id}/modifier → Modifier
 * - POST /logement/{id}/supprimer → Supprimer
 */
@WebServlet(name = "LogementServlet", urlPatterns = {"/logements", "/logement"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1 MB
        maxFileSize = 10 * 1024 * 1024,    // 10 MB
        maxRequestSize = 50 * 1024 * 1024  // 50 MB
)
public class LogementServlet extends HttpServlet {

    private LogementDAO logementDAO;

    private static final String UPLOAD_DIR = "upload/logements";

    @Override
    public void init() throws ServletException {
        logementDAO = new LogementDAO();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = getPath(request);
        System.out.println("LogementServlet GET: " + path);


        switch (path) {
            case "/logements" -> listeLogements(request, response);
            case "/logement" -> detailLogement(request, response);
            default -> response.sendError(404);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = getPath(request);
        System.out.println("POST: " + path);

        String action = request.getParameter("action");

        if ("supprimer".equals(action)) {
//            supprimerLogement(request, response);
        } else {
            response.sendError(404);
        }

    }



    // ==========================================
    // LISTE DES LOGEMENTS
    // ==========================================

    private void listeLogements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Paramètres de recherche
        String ville = request.getParameter("ville");
        String typeId = request.getParameter("type");
        String prixMax = request.getParameter("prixMax");
        String capaciteMin = request.getParameter("capacite");

        try {
            List<Logement> logements;

            // Si des filtres sont appliqués
            if (hasFilters(ville, typeId, prixMax, capaciteMin)) {
                // Utiliser la recherche avec filtres
                logements = logementDAO.searchLogements(ville, typeId, prixMax, capaciteMin);
            } else {
                // Sinon, afficher tous les logements disponibles
                logements = logementDAO.findAllDisponibles();
            }

            // Récupérer les types pour les filtres
            request.setAttribute("types", logementDAO.findAllTypes());
            request.setAttribute("listeLogements", logements);
            request.setAttribute("ville", ville);
            request.setAttribute("typeId", typeId);
            request.setAttribute("prixMax", prixMax);
            request.setAttribute("capacite", capaciteMin);

            request.getRequestDispatcher("/WEB-INF/views/logement/liste-logements.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            log("Erreur lors de la récupération des logements", e);
            request.setAttribute("erreur", "Une erreur est survenue lors de la recherche");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp")
                    .forward(request, response);
        }
    }



    /**
     * DÉTAIL D'UN LOGEMENT
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void detailLogement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(400, "ID du logement manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);

            // Récupérer le logement complet (avec photos, équipements, etc.)
            Logement logement = logementDAO.findById(id).orElse(null);

            if (logement == null) {
                response.sendError(404, "Logement non trouvé");
                return;
            }

            // Vérifier que le logement est disponible (sauf si hôte connecté)
            if (!"DISPONIBLE".equals(logement.getStatut().name()) &&
                    !isOwner(request, logement)) {
                response.sendError(404, "Logement non disponible");
                return;
            }

            // Récupérer les logements similaires
            try {
                List<Logement> similaires = logementDAO.findSimilaires(id, 4);
                request.setAttribute("logementsSimilaires", similaires);
            } catch (SQLException e) {
                log("Erreur lors de la récupération des logements similaires", e);
                // Non bloquant
            }

            request.setAttribute("logement", logement);
            request.getRequestDispatcher("/WEB-INF/views/logement/detail-logement.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(400, "ID invalide");
        } catch (Exception e) {
            log("Erreur lors de la récupération du logement", e);
            response.sendError(500, "Erreur serveur");
        }
    }
    // ==========================================
    // SUPPRESSION
    // ==========================================

//    private void supprimerLogement(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//
//        String idParam = request.getParameter("id");
//
//        if (idParam == null || idParam.isEmpty()) {
//            response.sendError(400, "ID manquant");
//            return;
//        }
//
//        try {
//            Long id = Long.parseLong(idParam);
//
//            // Archiver plutôt que supprimer définitivement
//            boolean success = logementDAO.archive(id, utilisateur.getId() );
//
//            if (success) {
//                request.getSession().setAttribute("flash", "Logement archivé avec succès");
//                response.sendRedirect(request.getContextPath() + "/hote/mes-biens");
//            } else {
//                response.sendError(500, "Échec de l'archivage");
//            }
//
//        } catch (NumberFormatException e) {
//            response.sendError(400, "ID invalide");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Extraire le path de la requête
     * @param request
     * @return path
     */
    private String getPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        return uri.substring(ctx.length());
    }

    /**
     * Vérifier si des filtres sont appliqués
     * @param params
     * @return boolean
     */
    private boolean hasFilters(String... params) {
        for (String param : params) {
            if (param != null && !param.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifier si l'utilisateur connecté est le propriétaire du logement
     * @param request
     * @param logement
     * @return boolean
     */
    private boolean isOwner(HttpServletRequest request, Logement logement) {
        var session = request.getSession(false);
        if (session == null) return false;

        var utilisateur = session.getAttribute("utilisateur");
        if (utilisateur == null) return false;

        // Cast en Utilisateur et vérifier l'ID
        try {
            Utilisateur user = (Utilisateur) utilisateur;
            return logement.getHoteId().equals(user.getId());
        } catch (ClassCastException e) {
            return false;
        }
    }


    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
