package training.afpa.cda24060.squatrbnb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import training.afpa.cda24060.squatrbnb.dao.LogementDAO;
import training.afpa.cda24060.squatrbnb.model.Logement;
import training.afpa.cda24060.squatrbnb.model.TypeLogement;

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
@WebServlet(name="LogementServlet", urlPatterns = { "/logements", "/logement", "/ajouter-logement", "/modifier-logement", "/supprimer-logement"})
public class LogementServlet extends HttpServlet {
    private LogementDAO logementDAO;
    @Override
    public void init() throws ServletException {
        logementDAO = new LogementDAO();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = getPath(request);;


        System.out.println("GET: " + path);

        switch (path) {
//            case "/" -> listeLogements(request, response);
            case "/logements" -> listeLogements(request, response);
            case "/logement" -> detailLogement(request, response);
            case "/ajouter-logement" -> formulaireAjout(request, response);
//            case "/modifier-logement" -> formulaireModification(request, response);
//            case "/supprimer-logement" -> supprimerLogement(request, response);
            default -> response.sendError(404);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = getPath(request);
        System.out.println("POST: " + path);

        switch (path) {
//            case "/ajouter-logement" -> ajouterLogement(request, response);
//            case "/modifier-logement" -> modifierLogement(request, response);
            default -> response.sendError(404);
        }
    }

    private String getPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        return uri.substring(ctx.length());
    }

    // ==========================================
    // ACCUEIL
    // ==========================================

    private void accueil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        List<Logement> populaires = logementDAO.findPopulaires(4);
//        request.setAttribute("logementsPopulaires", populaires);
//        request.setAttribute("nbLogements", logementDAO.countDisponibles());

        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }

    // ==========================================
    // LISTE DES LOGEMENTS
    // ==========================================

    private void listeLogements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("listeLogements");

        String ville = request.getParameter("ville");
        List<Logement> logements;

        if (ville != null && !ville.trim().isEmpty()) {
            logements = logementDAO.findByVille(ville.trim());
        } else {
            logements = logementDAO.findAllDisponibles();
        }

        request.setAttribute("listeLogements", logements);
        request.getRequestDispatcher("/WEB-INF/views/logement/liste-logements.jsp").forward(request, response);
    }

    // ==========================================
    // DÉTAIL D'UN LOGEMENT
    // ==========================================

    private void detailLogement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        System.out.println("idParam: " + idParam);
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.parseLong(idParam);
                Logement logement = logementDAO.findById(id).orElse(null);
                request.setAttribute("logement", logement);
            } catch (NumberFormatException e) {
                System.err.println("ID invalide: " + idParam);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/logement/detail-logement.jsp").forward(request, response);
    }

    // ==========================================
    // FORMULAIRE D'AJOUT
    // ==========================================

    private void formulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<TypeLogement> types = logementDAO.findAllTypes();
        request.setAttribute("types", types);

        request.getRequestDispatcher("/WEB-INF/views/hote/ajouter-logement.jsp").forward(request, response);
    }


    // ==========================================
    // FORMULAIRE DE SUPPRESSION
    // ==========================================
    private void supprimerLogement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.parseLong(idParam);
                logementDAO.archiver(id);

            }catch (Exception e) {
                System.err.println("ID invalide: " + idParam);
                throw new RuntimeException(e);
            }
        } else {
            response.sendError(400, "Id logement manquant");
        }
    }

    // ==========================================
    // UTILITAIRES
    // ==========================================

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return Long.parseLong(value.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return new BigDecimal(value.trim()); }
        catch (NumberFormatException e) { return null; }
    }

}
