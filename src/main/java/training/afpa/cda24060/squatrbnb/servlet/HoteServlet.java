package training.afpa.cda24060.squatrbnb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import training.afpa.cda24060.squatrbnb.dao.HoteDAO;

import training.afpa.cda24060.squatrbnb.dao.LogementDAO;
import training.afpa.cda24060.squatrbnb.dao.ReservationDAO;
import training.afpa.cda24060.squatrbnb.model.Logement;
import training.afpa.cda24060.squatrbnb.model.Reservation;
import training.afpa.cda24060.squatrbnb.model.TypeLogement;
import training.afpa.cda24060.squatrbnb.model.enums.Role;

import training.afpa.cda24060.squatrbnb.model.Utilisateur;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "HoteServlet", urlPatterns = {"/hote/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 10 * 1024 * 1024,       // 10 MB
        maxRequestSize = 50 * 1024 * 1024     // 50 MB
)
public class HoteServlet extends HttpServlet {

    private HoteDAO hoteDAO;
    private LogementDAO logementDAO;
    private ReservationDAO reservationDAO;

    // Chemin pour stocker les photos uploadées
    private static final String UPLOAD_DIRECTORY = "uploads/logements";

    @Override
    public void init() throws ServletException {
        hoteDAO = new HoteDAO();
        logementDAO = new LogementDAO();
        reservationDAO = new ReservationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier authentification
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            // Sauvegarder l'URL demandée pour redirection après login
            session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier que l'utilisateur a le rôle HOTE
        if (!utilisateur.hasRole(Role.valueOf("HOTE"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux hôtes");
            return;
        }

        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/dashboard";
        }

        System.out.println("=== HoteServlet ===");
        System.out.println("Path: " + path);
        System.out.println("Utilisateur: " + utilisateur.getEmail());

        switch (path) {
            case "/dashboard" -> showDashboard(request, response, utilisateur);
            case "/mes-biens" -> showMesLogements(request, response, utilisateur);
            case "/logement/*" -> detailLogement(request, response, Long.valueOf(extractIdFromPath(path,2)));
            case "/reservations" -> showReservations(request, response, utilisateur);
            case "/calendrier" -> showCalendrier(request, response, utilisateur);
            case "/messages" -> showMessages(request, response, utilisateur);
            case "/avis" -> showAvis(request, response, utilisateur);
            case "/revenus" -> showRevenus(request, response, utilisateur);
            default -> {
                // Gestion des sous-routes comme /logement/123/modifier
                if (path.startsWith("/logement/")) {
                    handlelogement(request, response, utilisateur, path);
                } else if (path.startsWith("/reservation/")) {
//                    handleReservation(request, response, utilisateur, path);
                } else {
                    showDashboard(request, response, utilisateur);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        // Vérifier authentification
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (!utilisateur.hasRole(Role.valueOf("HOTE"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Traitement des actions POST
        if (path != null) {
            if (path.equals("/logement/nouveau") || path.matches("/logement/\\d+/modifier")) {
//                savelogement(request, response, utilisateur);
            } else if (path.matches("/reservation/\\d+/accepter")) {
//                accepterReservation(request, response, utilisateur, path);
            } else if (path.matches("/reservation/\\d+/refuser")) {
//                refuserReservation(request, response, utilisateur, path);
            } else {
                doGet(request, response);
            }
        } else {
            doGet(request, response);
        }
    }

    // ==========================================
    // DASHBOARD
    // ==========================================

    private void showDashboard(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {

        Long hoteId = utilisateur.getId();

        // Récupérer la période depuis le paramètre (défaut: mois)
        String periode = request.getParameter("periode");
        if (periode == null || periode.isBlank()) {
            periode = "mois";
        }

        System.out.println("=== showDashboard ===");
        System.out.println("HoteId: " + hoteId + ", Periode: " + periode);

        // Charger les statistiques avec la période
        Map<String, Object> stats = HoteDAO.getStats(hoteId, periode);
        request.setAttribute("stats", stats);
        request.setAttribute("periodeSelectionnee", periode);

        System.out.println("Stats chargées: " + stats);

        // Charger les réservations récentes
        try {
//            List<Reservation> reservationsRecentes = reservationDAO.findRecentesByHote(hoteId, 5);
//            request.setAttribute("reservationsRecentes", reservationsRecentes);
        } catch (Exception e) {
            System.err.println("Erreur chargement réservations: " + e.getMessage());
            request.setAttribute("reservationsRecentes", List.of());
        }

        request.getRequestDispatcher("/WEB-INF/views/hote/dashboard.jsp").forward(request, response);
    }
    // ==========================================
    // MES logementS
    // ==========================================

    private void showMesLogements(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {

        Long hoteId = utilisateur.getId();

        // Récupérer les filtres
        String statut = request.getParameter("statut");
        String type = request.getParameter("type");
        String search = request.getParameter("q");

        System.out.println("=== showMeslogements ===");
        System.out.println("Filtres - statut: " + statut + ", type: " + type + ", search: " + search);

        // Charger les logements
        List<Logement> logements;
        try {
            if (statut != null || type != null || (search != null && !search.isBlank())) {
                logements = logementDAO.findByHoteWithFilters(hoteId, statut, type, search);
            } else {
                logements = logementDAO.findByHoteId(hoteId);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement logements: " + e.getMessage());
            logements = List.of();
        }
        // Charger les types pour le filtre
        List<TypeLogement> types = logementDAO.findAllTypes();

        request.setAttribute("logements", logements);
//        request.setAttribute("types", types);

        System.out.println("logements trouvés: " + logements.size());

        request.getRequestDispatcher("/WEB-INF/views/hote/mes-biens.jsp").forward(request, response);
    }

    // ==========================================
    // RÉSERVATIONS
    // ==========================================

    private void showReservations(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {

        Long hoteId = utilisateur.getId();

        // Filtres
        String statut = request.getParameter("statut");
        String logementIdStr = request.getParameter("logementId");
        String dateDebut = request.getParameter("dateDebut");
        String dateFin = request.getParameter("dateFin");
        String search = request.getParameter("q");

        // Pagination
        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (NumberFormatException ignored) {}

        int pageSize = 20;

        System.out.println("=== showReservations ===");
        System.out.println("Page: " + page + ", Statut: " + statut);

        // Charger les réservations
        List<Reservation> reservations;
        int total;
        try {
            Long logementId = (logementIdStr != null && !logementIdStr.isBlank()) ? Long.parseLong(logementIdStr) : null;
            reservations = reservationDAO.findByHote(hoteId, statut, logementId, pageSize, (page - 1) * pageSize);
            total = reservationDAO.countByHote(hoteId, statut, logementId);
        } catch (Exception e) {
            System.err.println("Erreur chargement réservations: " + e.getMessage());
            reservations = List.of();
            total = 0;
        }

        // Charger la liste des logements pour le filtre
        List<Logement> meslogements;
        try {
            meslogements = logementDAO.findByHoteId(hoteId);
        } catch (Exception e) {
            meslogements = List.of();
        }

        // Compter les réservations en attente
        int nbEnAttente;
        try {
            nbEnAttente = reservationDAO.countByHoteAndStatut(hoteId, "EN_ATTENTE");
        } catch (Exception e) {
            nbEnAttente = 0;
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("meslogements", meslogements);
        request.setAttribute("nbEnAttente", nbEnAttente);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", (int) Math.ceil((double) total / pageSize));

        // Conserver les paramètres pour la pagination
        StringBuilder queryString = new StringBuilder();
        if (statut != null) queryString.append("statut=").append(statut).append("&");
        if (logementIdStr != null) queryString.append("logementId=").append(logementIdStr).append("&");
        request.setAttribute("queryString", queryString.toString());

        request.getRequestDispatcher("/WEB-INF/views/hote/reservations.jsp").forward(request, response);
    }

    // ==========================================
    // AUTRES PAGES
    // ==========================================

    private void showCalendrier(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {
        // TODO: implémenter
        request.getRequestDispatcher("/WEB-INF/views/hote/calendrier.jsp").forward(request, response);
    }

    private void showMessages(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {
        // TODO: implémenter
        request.getRequestDispatcher("/WEB-INF/views/hote/messages.jsp").forward(request, response);
    }

    private void showAvis(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {
        // TODO: implémenter
        request.getRequestDispatcher("/WEB-INF/views/hote/avis.jsp").forward(request, response);
    }

    private void showRevenus(HttpServletRequest request, HttpServletResponse response, Utilisateur utilisateur)
            throws ServletException, IOException {
        // TODO: implémenter
        request.getRequestDispatcher("/WEB-INF/views/hote/revenus.jsp").forward(request, response);
    }

    // ==========================================
    // GESTION DES logements
    // ==========================================


    private void detailLogement(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {

        if (id == null) {
            response.sendError(404);
            return;
        }

        Logement logement = logementDAO.findById(id).orElse(null);

        if (logement == null) {
            response.sendError(404, "Logement non trouvé");
            return;
        }

        request.setAttribute("logement", logement);
        request.getRequestDispatcher("/WEB-INF/views/logement/detail-logement.jsp").forward(request, response);
    }

    private void handlelogement(HttpServletRequest request, HttpServletResponse response,
                            Utilisateur utilisateur, String path) throws ServletException, IOException {

        // /logement/nouveau
        if (path.equals("/logement/nouveau")) {
            showFormLogement(request, response, utilisateur, null);
            return;
        }

        // /logement/123/modifier ou /logement/123
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            try {
                Long logementId = (long) Integer.parseInt(parts[2]);

                if (parts.length == 3 || parts[3].equals("modifier")) {
                    showFormLogement(request, response, utilisateur, logementId);
                } else if (parts[3].equals("supprimer")) {
                    supprimerlogement(request, response, utilisateur, logementId);
                } else if (parts[3].equals("statut")) {
                    changerStatutlogement(request, response, utilisateur, logementId);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID logement invalide");
            }
        }
    }

    private void showFormLogement(HttpServletRequest request, HttpServletResponse response,
                              Utilisateur utilisateur, Long logementId) throws ServletException, IOException {

        if (logementId != null) {
            // Mode édition
            Logement logement = logementDAO.findById(logementId).orElse(null);
            if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("logement", logement);
        }

        // Charger les types de logements, équipements, etc.
//         request.setAttribute("typeslogement", TypelogementDAO.findAll());
//         request.setAttribute("equipements", EquipementDAO.findAll());

        request.getRequestDispatcher("/WEB-INF/views/hote/ajouter-logement.jsp").forward(request, response);
    }

    private void savelogement(HttpServletRequest request, HttpServletResponse response,
                          Utilisateur utilisateur) throws ServletException, IOException {
        // TODO: implémenter la sauvegarde
        response.sendRedirect(request.getContextPath() + "/hote/mes-logements");
    }

    private void supprimerlogement(HttpServletRequest request, HttpServletResponse response,
                               Utilisateur utilisateur, Long logementId) throws IOException {

        Logement logement = logementDAO.findById(logementId).orElse(null);
        if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Supprimer ou archiver le logement
        logementDAO.archiver(logementId);

        request.getSession().setAttribute("flash", "logement supprimé avec succès");
        response.sendRedirect(request.getContextPath() + "/hote/mes-logements");
    }

    private void changerStatutlogement(HttpServletRequest request, HttpServletResponse response,
                                   Utilisateur utilisateur, Long logementId) throws IOException {

        String statut = request.getParameter("statut");

        Logement logement = logementDAO.findById(logementId).orElse(null);
        if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        logementDAO.updateStatut(logementId, statut);

        request.getSession().setAttribute("flash", "Statut mis à jour");
        response.sendRedirect(request.getContextPath() + "/hote/mes-logements");
    }



    // ==========================================
    // UTILITAIRES
    // ==========================================

    private Integer extractIdFromPath(String path, int position) {
        String[] parts = path.split("/");
        if (parts.length > position) {
            try {
                return Integer.parseInt(parts[position]);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}