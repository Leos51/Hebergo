package training.afpa.cda24060.squatrbnb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import training.afpa.cda24060.squatrbnb.dao.HoteDAO;
import training.afpa.cda24060.squatrbnb.dao.LogementDAO;
import training.afpa.cda24060.squatrbnb.dao.ReservationDAO;
import training.afpa.cda24060.squatrbnb.model.*;
import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.enums.StatutLogement;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
            session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier que l'utilisateur a le rôle HOTE
        if (!utilisateur.hasRole(Role.HOTE)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux hôtes");
            return;
        }

        // Routing
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/dashboard";
        }

        System.out.println("=== HoteServlet GET ===");
        System.out.println("Path: " + path);
        System.out.println("Utilisateur: " + utilisateur.getEmail());

        switch (path) {
            case "/dashboard" -> showDashboard(request, response, utilisateur);
            case "/mes-biens" -> showMesLogements(request, response, utilisateur);
            case "/reservations" -> showReservations(request, response, utilisateur);
            default -> handleGetSubRoute(request, response, utilisateur, path);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (!utilisateur.hasRole(Role.HOTE)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String path = request.getPathInfo();
        System.out.println("=== HoteServlet POST ===");
        System.out.println("Path: " + path);


        // Traitement des actions POST
        if (path != null) {
            if (path.equals("/logement/nouveau")) {
                ajouterLogement(request, response, utilisateur);
            } else if (path.matches("/logement/\\d+/modifier")) {
                modifierLogement(request, response, utilisateur);
            } else if (path.matches("/logement/\\d+/statut")) {
                changerStatutLogement(request, response, utilisateur);
            } else if (path.matches("/logement/\\d+/supprimer")) {
                supprimerLogement(request, response, utilisateur);
            } else {
                response.sendError(404);
            }
        } else {
            response.sendError(404);
        }
    }

    // ==========================================
    // GESTION DES SOUS-ROUTES GET
    // ==========================================

    private void handleGetSubRoute(HttpServletRequest request, HttpServletResponse response,
                                   Utilisateur utilisateur, String path)
            throws ServletException, IOException {

        if (path.equals("/logement/nouveau")) {
            afficherFormulaireAjout(request, response, utilisateur);
        } else if (path.matches("/logement/\\d+")) {
            afficherDetailLogement(request, response, utilisateur, path);
        } else if (path.matches("/logement/\\d+/modifier")) {
            afficherFormulaireModification(request, response, utilisateur, path);
        } else {
            showDashboard(request, response, utilisateur);
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

        try{
            // Charger les statistiques avec la période
            Map<String, Object> stats = hoteDAO.getStats(hoteId, periode);
            System.out.println("Stats chargées: " + stats);
            request.setAttribute("stats", stats);
        }catch(Exception e){
            request.setAttribute("stats", Map.of());
        }

        request.setAttribute("periodeSelectionnee", periode);


        // Charger les réservations récentes
        try {
            List<Reservation> reservationsRecentes = reservationDAO.findRecentesByHote(hoteId, 5);
            request.setAttribute("reservationsRecentes", reservationsRecentes);
        } catch (Exception e) {
            System.err.println("Erreur chargement réservations: " + e.getMessage());
            request.setAttribute("reservationsRecentes", List.of());
        }

        request.getRequestDispatcher("/WEB-INF/views/hote/dashboard.jsp")
                .forward(request, response);
    }

    // ==========================================
    // LISTE DES LOGEMENTS
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
        request.setAttribute("types", types);

        System.out.println("logements trouvés: " + logements.size());

        request.getRequestDispatcher("/WEB-INF/views/hote/mes-biens.jsp")
                .forward(request, response);
    }

    // ==========================================
    // RÉSERVATIONS
    // ==========================================

    private void showReservations(HttpServletRequest request, HttpServletResponse response,
                                  Utilisateur utilisateur) throws ServletException, IOException {

        Long hoteId = utilisateur.getId();
        String statut = request.getParameter("statut");
        String logementIdStr = request.getParameter("logementId");

        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (NumberFormatException ignored) {}

        int pageSize = 20;

        List<Reservation> reservations;
        int total;
        try {
            Long logementId = (logementIdStr != null && !logementIdStr.isBlank())
                    ? Long.parseLong(logementIdStr) : null;
            reservations = reservationDAO.findByHote(hoteId, statut, logementId, pageSize, (page - 1) * pageSize);
            total = reservationDAO.countByHote(hoteId, statut, logementId);
        } catch (Exception e) {
            e.printStackTrace();
            reservations = List.of();
            total = 0;
        }

        List<Logement> mesLogements;
        try {
            mesLogements = logementDAO.findByHoteId(hoteId);
        } catch (Exception e) {
            e.printStackTrace();
            mesLogements = List.of();
        }

        int nbEnAttente;
        try {
            nbEnAttente = reservationDAO.countByHoteAndStatut(hoteId, "EN_ATTENTE");
        } catch (Exception e) {
            e.printStackTrace();
            nbEnAttente = 0;
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("mesLogements", mesLogements);
        request.setAttribute("nbEnAttente", nbEnAttente);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", (int) Math.ceil((double) total / pageSize));

        request.getRequestDispatcher("/WEB-INF/views/hote/reservations.jsp")
                .forward(request, response);
    }

    // ==========================================
    // GESTION DES RÉSERVATIONS
    // ==========================================

    /**
     * Confirmer une réservation
     */
    private void confirmerReservation(HttpServletRequest request, HttpServletResponse response,
                                      Utilisateur utilisateur) throws IOException {

        Long reservationId = extraireIdDepuisPath(request.getPathInfo());
        if (reservationId == null) {
            response.sendError(400, "ID invalide");
            return;
        }

        try {
            // Vérifier que la réservation appartient bien à un logement de cet hôte
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation == null) {
                response.sendError(404, "Réservation non trouvée");
                return;
            }

            // Vérifier la propriété du logement
            Logement logement = logementDAO.findById(reservation.getLogementId()).orElse(null);
            if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
                response.sendError(403, "Accès refusé");
                return;
            }

            // Confirmer la réservation
            boolean success = reservationDAO.confirmer(reservationId);

            if (success) {
                request.getSession().setAttribute("flash", "Réservation confirmée avec succès");
            } else {
                request.getSession().setAttribute("flash", "Échec de la confirmation");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("flash", "Erreur: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/hote/reservations");
    }

    /**
     * Annuler une réservation (par l'hôte)
     */
    private void annulerReservation(HttpServletRequest request, HttpServletResponse response,
                                    Utilisateur utilisateur) throws IOException {

        Long reservationId = extraireIdDepuisPath(request.getPathInfo());
        String motif = request.getParameter("motif");

        if (reservationId == null) {
            response.sendError(400, "ID invalide");
            return;
        }

        try {
            // Vérifier la propriété
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation == null) {
                response.sendError(404, "Réservation non trouvée");
                return;
            }

            Logement logement = logementDAO.findById(reservation.getLogementId()).orElse(null);
            if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
                response.sendError(403, "Accès refusé");
                return;
            }

            // Annuler
            boolean success = reservationDAO.annuler(reservationId, motif, "HOTE");

            if (success) {
                request.getSession().setAttribute("flash", "Réservation annulée");
            } else {
                request.getSession().setAttribute("flash", "Échec de l'annulation");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("flash", "Erreur: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/hote/reservations");
    }

    // ==========================================
    // FORMULAIRE AJOUT LOGEMENT
    // ==========================================

    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response,
                                         Utilisateur utilisateur) throws ServletException, IOException {

        List<TypeLogement> types = logementDAO.findAllTypes();
        List<Equipement> equipements = logementDAO.findAllEquipements();

        request.setAttribute("types", types);
        request.setAttribute("equipements", equipements);

        request.getRequestDispatcher("/WEB-INF/views/hote/nouveau-logement.jsp")
                .forward(request, response);
    }

    // ==========================================
    // AJOUTER LOGEMENT
    // ==========================================

    private void ajouterLogement(HttpServletRequest request, HttpServletResponse response,
                                 Utilisateur utilisateur) throws ServletException, IOException {

        try {
            // Validation et création du logement
            Logement logement = construireLogementDepuisFormulaire(request, utilisateur.getId());

            // Sauvegarder le logement
            Long logementId = logementDAO.createComplete(logement, utilisateur.getId());

            if (logementId != null) {
                // Gérer les photos
                List<PhotoLogement> photos = traiterPhotosUpload(request, logementId);

                for (PhotoLogement photo : photos) {
                    logementDAO.addPhoto(photo, utilisateur.getId());
                }

                request.getSession().setAttribute("flash", "Logement ajouté avec succès !");
                response.sendRedirect(request.getContextPath() + "/hote/mes-biens");
            } else {
                request.setAttribute("erreur", "Échec de la création du logement");
                afficherFormulaireAjout(request, response, utilisateur);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur: " + e.getMessage());
            afficherFormulaireAjout(request, response, utilisateur);
        }
    }

    // ==========================================
    // FORMULAIRE MODIFICATION LOGEMENT
    // ==========================================

    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response,
                                                Utilisateur utilisateur, String path)
            throws ServletException, IOException {

        Long logementId = extraireIdDepuisPath(path);
        if (logementId == null) {
            response.sendError(400, "ID invalide");
            return;
        }
        try {
            Logement logement = logementDAO.findById(logementId).orElse(null);

            if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
                response.sendError(404, "Logement non trouvé");
                return;
            }
            List<TypeLogement> types = logementDAO.findAllTypes();
            List<Equipement> equipements = logementDAO.findAllEquipements();

            request.setAttribute("logement", logement);
            request.setAttribute("types", types);
            request.setAttribute("equipements", equipements);

            request.getRequestDispatcher("/WEB-INF/views/hote/modifier-logement.jsp")
                    .forward(request, response);

        }catch (Exception e) {
            response.sendError(500, "Erreur serveur: " + e.getMessage());
        }
    }

    // ==========================================
    // MODIFIER LOGEMENT
    // ==========================================

    private void modifierLogement(HttpServletRequest request, HttpServletResponse response,
                                  Utilisateur utilisateur) throws ServletException, IOException {

        Long logementId = extraireIdDepuisPath(request.getPathInfo());
        if (logementId == null) {
            response.sendError(400, "ID invalide");
            return;
        }

        try {
            // 1. Construire le logement avec les nouvelles données
            Logement logement = construireLogementDepuisFormulaire(request, utilisateur.getId());
            logement.setId(logementId);

            // 2. Mettre à jour avec la nouvelle méthode updateComplete()
            boolean success = logementDAO.updateComplete(logement, utilisateur.getId());

            if (success) {
                // 3. Gérer les nouvelles photos
                List<PhotoLogement> photos = traiterPhotosUpload(request, logementId);

                for (PhotoLogement photo : photos) {
                    logementDAO.addPhoto(photo, utilisateur.getId());
                }

                request.getSession().setAttribute("flash", "Logement modifié avec succès !");
                response.sendRedirect(request.getContextPath() + "/hote/mes-biens");
            } else {
                request.setAttribute("erreur", "Échec de la modification");
                afficherFormulaireModification(request, response, utilisateur, request.getPathInfo());
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur: " + e.getMessage());
            afficherFormulaireModification(request, response, utilisateur, request.getPathInfo());
        }
    }

    // ==========================================
    // CHANGER STATUT LOGEMENT
    // ==========================================

    private void changerStatutLogement(HttpServletRequest request, HttpServletResponse response,
                                       Utilisateur utilisateur) throws IOException {

        Long logementId = extraireIdDepuisPath(request.getPathInfo());
        String statut = request.getParameter("statut");

        if (logementId == null || statut == null) {
            response.sendError(400, "Paramètres invalides");
            return;
        }

        try {
            Logement logement = logementDAO.findById(logementId).orElse(null);

            if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
                response.sendError(403, "Accès refusé");
                return;
            }

            boolean success = logementDAO.updateStatut(logementId, statut);

            if (success) {
                request.getSession().setAttribute("flash", "Statut mis à jour avec succès");
            } else {
                request.getSession().setAttribute("flash", "Échec de la mise à jour");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("flash", "Erreur: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/hote/mes-biens");
    }

    // ==========================================
    // SUPPRIMER LOGEMENT
    // ==========================================

    private void supprimerLogement(HttpServletRequest request, HttpServletResponse response,
                                   Utilisateur utilisateur) throws IOException {

        Long logementId = extraireIdDepuisPath(request.getPathInfo());

        if (logementId == null) {
            response.sendError(400, "ID invalide");
            return;
        }

        try {
            // Option 1: Suppression sécurisée (vérifie les réservations)
            String action = request.getParameter("action");
            boolean success;

            if ("delete".equals(action)) {
                success = logementDAO.deleteSecure(logementId, utilisateur.getId());
                request.getSession().setAttribute("flash", "Logement supprimé avec succès");
            } else {
                // Option 2: Archivage (soft delete) - par défaut
                success = logementDAO.archive(logementId, utilisateur.getId());
                request.getSession().setAttribute("flash", "Logement archivé avec succès");
            }

            if (!success) {
                request.getSession().setAttribute("flash", "Échec de l'opération");
            }

        } catch (SQLException e) {
            request.getSession().setAttribute("flash", "Erreur: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/hote/mes-biens");
    }

    // ==========================================
    // AFFICHER DÉTAIL LOGEMENT
    // ==========================================


    private void afficherDetailLogement(HttpServletRequest request, HttpServletResponse response,
                                        Utilisateur utilisateur, String path)
            throws ServletException, IOException {

        Long logementId = extraireIdDepuisPath(path);
        if (logementId == null) {
            response.sendError(400, "ID invalide");
            return;
        }

        try {
            Logement logement = logementDAO.findById(logementId).orElse(null);

            if (logement == null || !logement.getHoteId().equals(utilisateur.getId())) {
                response.sendError(404, "Logement non trouvé");
                return;
            }

            request.setAttribute("logement", logement);
            request.getRequestDispatcher("/WEB-INF/views/hote/detail-logement.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            response.sendError(500, "Erreur serveur");
        }
    }





    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    private Logement construireLogementDepuisFormulaire(HttpServletRequest request, Long hoteId) {
        Logement logement = new Logement();

        logement.setHoteId(hoteId);
        logement.setTypeLogementId(parseLong(request.getParameter("typeLogementId")));
        logement.setTitre(request.getParameter("titre"));
        logement.setDescription(request.getParameter("description"));
        logement.setNbChambres(parseInt(request.getParameter("nbChambres")));
        logement.setNbLits(parseInt(request.getParameter("nbLits")));
        logement.setNbSallesBain(parseInt(request.getParameter("nbSallesBain")));
        logement.setCapaciteMax(parseInt(request.getParameter("capaciteMax")));
        logement.setSuperficie(parseBigDecimal(request.getParameter("superficie")));
        logement.setPrixNuit(parseBigDecimal(request.getParameter("prixNuit")));
        logement.setFraisMenage(parseBigDecimal(request.getParameter("fraisMenage")));
        logement.setHeureArrivee(request.getParameter("heureArrivee"));
        logement.setHeureDepart(request.getParameter("heureDepart"));
        logement.setReglementInterieur(request.getParameter("reglementInterieur"));
        logement.setDelaiAnnulation(parseInt(request.getParameter("delaiAnnulation")));

        // Adresse
        AdresseBien adresse = new AdresseBien();
        adresse.setAdresse(request.getParameter("adresse"));
        adresse.setCodePostal(request.getParameter("codePostal"));
        adresse.setVille(request.getParameter("ville"));
        adresse.setRegion(request.getParameter("region"));
        adresse.setPays(request.getParameter("pays"));
        adresse.setLatitude(parseBigDecimal(request.getParameter("latitude")));
        adresse.setLongitude(parseBigDecimal(request.getParameter("longitude")));
        logement.setAdresse(adresse);

        // Équipements
        String[] equipementIds = request.getParameterValues("equipements");
        if (equipementIds != null) {
            List<Long> ids = new ArrayList<>();
            for (String id : equipementIds) {
                try {
                    ids.add(parseLong(id));
                } catch (NumberFormatException ignored) {}
            }
            logement.setEquipementIds(ids);
        }

        // Statut
        boolean publierDirectement = "on".equals(request.getParameter("publierDirectement"));
        logement.setStatut(publierDirectement ? StatutLogement.DISPONIBLE : StatutLogement.BROUILLON);

        return logement;
    }


    /**
     * Traiter l'upload des photos et retourner une liste de PhotoLogement
     */
    private List<PhotoLogement> traiterPhotosUpload(HttpServletRequest request, Long logementId)
            throws IOException, ServletException {

        List<PhotoLogement> photos = new ArrayList<>();
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;

        // Créer le répertoire s'il n'existe pas
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        int ordre = 0;
        for (Part part : request.getParts()) {
            if ("photos".equals(part.getName()) && part.getSize() > 0) {
                try {
                    // Valider le type de fichier
                    String contentType = part.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        continue;
                    }

                    // Générer un nom de fichier unique
                    String originalFilename = getSubmittedFileName(part);
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = "logement_" + logementId + "_" +
                            System.currentTimeMillis() + "_" +
                            UUID.randomUUID().toString() + extension;

                    // Sauvegarder le fichier
                    Path filePath = Paths.get(uploadPath, uniqueFilename);
                    Files.copy(part.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Créer l'objet PhotoLogement
                    PhotoLogement photo = new PhotoLogement();
                    photo.setLogementId(logementId);
                    photo.setUrl("/uploads/logements/" + uniqueFilename);
                    photo.setEstPrincipale(ordre == 0); // Première photo = principale
                    photo.setOrdre(ordre++);
                    photos.add(photo);

                } catch (IOException e) {
                    System.err.println("Erreur upload photo: " + e.getMessage());
                }
            }
        }

        return photos;
    }

    /**
     * Extraire le nom de fichier soumis depuis une Part
     */
    private String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "unknown";
    }

    /**
     * Extraire l'ID depuis le path
     */
    private Long extraireIdDepuisPath(String path) {
        if (path == null) return null;
        String[] parts = path.split("/");
        for (String part : parts) {
            try {
                return Long.parseLong(part);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }


    // Méthodes de parsing avec gestion des erreurs

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