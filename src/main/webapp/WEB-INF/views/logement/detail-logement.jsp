
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${logement.titre} - SquatRBnB</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Leaflet pour la carte -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />

    <style>
        :root {
            --primary-color: #FF5A5F;
            --secondary-color: #00A699;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }

        /* Galerie photos */
        .photo-gallery {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            grid-template-rows: repeat(2, 250px);
            gap: 8px;
            margin-bottom: 2rem;
            border-radius: 12px;
            overflow: hidden;
        }

        .photo-gallery img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            cursor: pointer;
            transition: transform 0.3s;
        }

        .photo-gallery img:hover {
            transform: scale(1.05);
        }

        .photo-gallery .main-photo {
            grid-column: 1 / 3;
            grid-row: 1 / 3;
        }

        /* Badge statut */
        .badge-disponible {
            background-color: #00A699;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 500;
        }

        .badge-indisponible {
            background-color: #dc3545;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 500;
        }

        /* Card prix */
        .price-card {
            position: sticky;
            top: 100px;
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 6px 20px rgba(0,0,0,0.08);
        }

        .price-value {
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--primary-color);
        }

        /* Icônes d'équipements */
        .amenity-item {
            display: flex;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .amenity-item:last-child {
            border-bottom: none;
        }

        .amenity-item i {
            width: 24px;
            margin-right: 16px;
            color: #717171;
        }

        /* Carte */
        #map {
            height: 400px;
            border-radius: 12px;
            border: 1px solid #e0e0e0;
        }

        /* Section host */
        .host-card {
            display: flex;
            align-items: center;
            padding: 24px;
            border: 1px solid #e0e0e0;
            border-radius: 12px;
        }

        .host-avatar {
            width: 64px;
            height: 64px;
            border-radius: 50%;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5rem;
            font-weight: bold;
            margin-right: 16px;
        }

        /* Bouton de réservation */
        .btn-reserve {
            background-color: var(--primary-color);
            color: white;
            padding: 14px 24px;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
            border: none;
            width: 100%;
        }

        .btn-reserve:hover {
            background-color: #E4484D;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 90, 95, 0.4);
        }

        /* Section headers */
        .section-header {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #e0e0e0;
        }

        /* Modal galerie */
        .modal-content {
            background-color: #000;
        }

        .modal-body img {
            max-width: 100%;
            height: auto;
        }
    </style>
</head>
<body>




<!-- Contenu principal -->
<div class="container mt-4 mb-5">

    <c:if test="${logement == null}">
        <div class="alert alert-warning text-center">
            <i class="fas fa-exclamation-triangle me-2"></i>
            Logement introuvable
        </div>
        <div class="text-center">
            <a href="${pageContext.request.contextPath}/logements" class="btn btn-primary">
                Retour aux logements
            </a>
        </div>
    </c:if>

    <c:if test="${logement != null}">

        <!-- Titre et localisation -->
        <div class="mb-3">
            <h1 class="h2 mb-2">${logement.titre}</h1>
            <div class="d-flex align-items-center text-muted">
                <c:if test="${logement.noteMoyenne != null && logement.noteMoyenne > 0}">
                    <span class="me-3">
                        <i class="fas fa-star text-warning"></i>
                        <strong><fmt:formatNumber value="${logement.noteMoyenne}" maxFractionDigits="1"/></strong>
                        <span class="small">(${logement.nbAvis} avis)</span>
                    </span>
                </c:if>
                <c:if test="${not empty logement.adresse && not empty logement.adresse.ville}">
                    <span>
                        <i class="fas fa-map-marker-alt me-1"></i>
                        ${logement.adresse.ville}, ${logement.adresse.pays != null ? logement.adresse.pays : 'France'}
                    </span>
                </c:if>
            </div>
        </div>

        <!-- Galerie photos -->
        <c:if test="${not empty logement.photos}">
            <div class="photo-gallery">
                <c:forEach var="photo" items="${logement.photos}" begin="0" end="4" varStatus="status">
                    <img src="${pageContext.request.contextPath}${photo.url}"
                         alt="${logement.titre}"
                         class="${status.index == 0 ? 'main-photo' : ''}"
                         data-bs-toggle="modal"
                         data-bs-target="#galleryModal"
                         data-photo-index="${status.index}">
                </c:forEach>
            </div>

            <c:if test="${logement.photos.size() > 5}">
                <button class="btn btn-outline-dark mb-4" data-bs-toggle="modal" data-bs-target="#galleryModal">
                    <i class="fas fa-images me-2"></i>
                    Voir toutes les photos (${logement.photos.size()})
                </button>
            </c:if>
        </c:if>

        <div class="row">
            <!-- Colonne principale -->
            <div class="col-lg-8">

                <!-- Informations principales -->
                <div class="mb-4 pb-4 border-bottom">
                    <h3 class="h5 mb-3">
                        <c:if test="${not empty logement.typeLogement}">
                            ${logement.typeLogement.libelle}
                        </c:if>
                    </h3>

                    <div class="d-flex gap-4 text-muted">
                        <c:if test="${logement.capaciteMax != null}">
                            <span><i class="fas fa-users me-2"></i>${logement.capaciteMax} voyageurs</span>
                        </c:if>
                        <c:if test="${logement.nbChambres != null && logement.nbChambres > 0}">
                            <span><i class="fas fa-bed me-2"></i>${logement.nbChambres} chambres</span>
                        </c:if>
                        <c:if test="${logement.nbLits != null && logement.nbLits > 0}">
                            <span><i class="fas fa-door-open me-2"></i>${logement.nbLits} lits</span>
                        </c:if>
                        <c:if test="${logement.nbSallesBain != null && logement.nbSallesBain > 0}">
                            <span><i class="fas fa-bath me-2"></i>${logement.nbSallesBain} salles de bain</span>
                        </c:if>
                    </div>
                </div>

                <!-- Description -->
                <div class="mb-4 pb-4 border-bottom">
                    <h3 class="section-header">
                        <i class="fas fa-info-circle me-2"></i>Description
                    </h3>
                    <p style="white-space: pre-line;">${logement.description}</p>
                </div>

                <!-- Équipements -->
                <c:if test="${not empty logement.equipements}">
                    <div class="mb-4 pb-4 border-bottom">
                        <h3 class="section-header">
                            <i class="fas fa-check-circle me-2"></i>Équipements proposés
                        </h3>
                        <div class="row">
                            <c:forEach var="equipement" items="${logement.equipements}">
                                <div class="col-md-6">
                                    <div class="amenity-item">
                                        <i class="${not empty equipement.icone ? equipement.icone : 'fas fa-check'}"></i>
                                        <span>${equipement.nom}</span>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

                <!-- Règlement intérieur -->
                <c:if test="${not empty logement.reglementInterieur}">
                    <div class="mb-4 pb-4 border-bottom">
                        <h3 class="section-header">
                            <i class="fas fa-file-alt me-2"></i>Règlement intérieur
                        </h3>
                        <div class="alert alert-info">
                            <p style="white-space: pre-line; margin-bottom: 0;">${logement.reglementInterieur}</p>
                        </div>
                    </div>
                </c:if>

                <!-- Informations pratiques -->
                <div class="mb-4 pb-4 border-bottom">
                    <h3 class="section-header">
                        <i class="fas fa-clock me-2"></i>Informations pratiques
                    </h3>
                    <div class="row g-3">
                        <c:if test="${not empty logement.heureArrivee}">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center">
                                    <i class="fas fa-sign-in-alt me-3 text-success"></i>
                                    <div>
                                        <strong>Arrivée</strong><br>
                                        <span class="text-muted">À partir de ${logement.heureArrivee}</span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${not empty logement.heureDepart}">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center">
                                    <i class="fas fa-sign-out-alt me-3 text-danger"></i>
                                    <div>
                                        <strong>Départ</strong><br>
                                        <span class="text-muted">Avant ${logement.heureDepart}</span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${logement.delaiAnnulation != null}">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center">
                                    <i class="fas fa-undo me-3 text-warning"></i>
                                    <div>
                                        <strong>Annulation</strong><br>
                                        <span class="text-muted">Gratuite ${logement.delaiAnnulation} jours avant</span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${logement.superficie != null}">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center">
                                    <i class="fas fa-ruler-combined me-3 text-primary"></i>
                                    <div>
                                        <strong>Superficie</strong><br>
                                        <span class="text-muted">${logement.superficie} m²</span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Carte de localisation -->
                <c:if test="${not empty logement.adresse}">
                    <div class="mb-4">
                        <h3 class="section-header">
                            <i class="fas fa-map-marked-alt me-2"></i>Localisation
                        </h3>
                        <div id="map"></div>
                        <p class="text-muted mt-3">
                            <i class="fas fa-info-circle me-2"></i>
                            L'emplacement exact sera communiqué après la réservation
                        </p>
                    </div>
                </c:if>

            </div>

            <!-- Sidebar prix et réservation -->
            <div class="col-lg-4">
                <div class="price-card">

                    <!-- Prix -->
                    <div class="mb-3">
                        <span class="price-value">
                            <fmt:formatNumber value="${logement.prixNuit}" type="number" maxFractionDigits="0"/>€
                        </span>
                        <span class="text-muted">/ nuit</span>
                    </div>

                    <!-- Statut -->
                    <div class="mb-3">
                        <c:choose>
                            <c:when test="${logement.statut == 'DISPONIBLE'}">
                                <span class="badge badge-disponible">
                                    <i class="fas fa-check-circle me-1"></i>Disponible
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-indisponible">
                                    <i class="fas fa-times-circle me-1"></i>Indisponible
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Bouton réserver -->
                    <c:choose>
                        <c:when test="${logement.statut == 'DISPONIBLE'}">
                            <button class="btn btn-reserve mb-3" onclick="reserver()">
                                <i class="fas fa-calendar-check me-2"></i>Réserver
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-reserve mb-3" disabled>
                                <i class="fas fa-ban me-2"></i>Non disponible
                            </button>
                        </c:otherwise>
                    </c:choose>

                    <p class="text-center text-muted small mb-4">
                        Vous ne serez pas débité pour l'instant
                    </p>

                    <!-- Détails tarifaires -->
                    <div class="border-top pt-3">
                        <div class="d-flex justify-content-between mb-2">
                            <span><fmt:formatNumber value="${logement.prixNuit}" type="number"/>€ x 1 nuit</span>
                            <span><fmt:formatNumber value="${logement.prixNuit}" type="number"/>€</span>
                        </div>
                        <c:if test="${logement.fraisMenage != null && logement.fraisMenage > 0}">
                            <div class="d-flex justify-content-between mb-2">
                                <span>Frais de ménage</span>
                                <span><fmt:formatNumber value="${logement.fraisMenage}" type="number"/>€</span>
                            </div>
                        </c:if>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Frais de service</span>
                            <span>-</span>
                        </div>
                        <div class="border-top pt-2 mt-2">
                            <div class="d-flex justify-content-between fw-bold">
                                <span>Total</span>
                                <span>
                                    <fmt:formatNumber value="${logement.prixNuit + (logement.fraisMenage != null ? logement.fraisMenage : 0)}"
                                                      type="number"/>€
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Contact hôte -->
                    <div class="border-top mt-4 pt-4">
                        <p class="small text-muted mb-2">
                            <i class="fas fa-shield-alt me-2"></i>
                            Votre paiement est protégé
                        </p>
                        <p class="small text-muted mb-0">
                            <i class="fas fa-headset me-2"></i>
                            Support client 24/7
                        </p>
                    </div>

                </div>

                <!-- Actions hôte -->
                <c:if test="${not empty sessionScope.utilisateur && sessionScope.utilisateur.hasRole('HOTE') && sessionScope.utilisateur.id == logement.hoteId}">
                    <div class="card mt-3 border-warning">
                        <div class="card-body">
                            <h6 class="card-title">
                                <i class="fas fa-user-shield me-2"></i>Actions hôte
                            </h6>
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/hote/logement/${logement.id}/modifier"
                                   class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-edit me-2"></i>Modifier
                                </a>
                                <a href="${pageContext.request.contextPath}/hote/logement/${logement.id}/stats"
                                   class="btn btn-outline-info btn-sm">
                                    <i class="fas fa-chart-bar me-2"></i>Statistiques
                                </a>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>

    </c:if>
</div>

<!-- Modal Galerie photos -->
<c:if test="${not empty logement.photos}">
    <div class="modal fade" id="galleryModal" tabindex="-1">
        <div class="modal-dialog modal-xl modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header border-0">
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="carouselGallery" class="carousel slide" data-bs-ride="carousel">
                        <div class="carousel-inner">
                            <c:forEach var="photo" items="${logement.photos}" varStatus="status">
                                <div class="carousel-item ${status.index == 0 ? 'active' : ''}">
                                    <img src="${pageContext.request.contextPath}${photo.url}"
                                         class="d-block w-100"
                                         alt="${logement.titre}">
                                    <c:if test="${not empty photo.legende}">
                                        <div class="carousel-caption">
                                            <p>${photo.legende}</p>
                                        </div>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                        <button class="carousel-control-prev" type="button" data-bs-target="#carouselGallery" data-bs-slide="prev">
                            <span class="carousel-control-prev-icon"></span>
                        </button>
                        <button class="carousel-control-next" type="button" data-bs-target="#carouselGallery" data-bs-slide="next">
                            <span class="carousel-control-next-icon"></span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Footer -->
<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

<script>
    // Initialisation de la carte
    <c:if test="${not empty logement.adresse}">
    const lat = ${logement.adresse.latitude != null ? logement.adresse.latitude : 'null'};
    const lng = ${logement.adresse.longitude != null ? logement.adresse.longitude : 'null'};

    if (lat !== null && lng !== null) {
        const map = L.map('map').setView([lat, lng], 14);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);

        // Marqueur approximatif (pas l'adresse exacte)
        L.circle([lat, lng], {
            color: '#FF5A5F',
            fillColor: '#FF5A5F',
            fillOpacity: 0.2,
            radius: 500
        }).addTo(map);
    } else {
        // Géocodage avec Nominatim
        const adresse = "${logement.adresse.ville}, ${logement.adresse.pays}";
        fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(adresse))
            .then(response => response.json())
            .then(data => {
                if (data && data.length > 0) {
                    const latitude = parseFloat(data[0].lat);
                    const longitude = parseFloat(data[0].lon);

                    const map = L.map('map').setView([latitude, longitude], 13);

                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);

                    L.circle([latitude, longitude], {
                        color: '#FF5A5F',
                        fillColor: '#FF5A5F',
                        fillOpacity: 0.2,
                        radius: 800
                    }).addTo(map);
                }
            });
    }
    </c:if>

    // Fonction réserver
    function reserver() {
        <c:choose>
        <c:when test="${not empty sessionScope.utilisateur}">
        window.location.href = '${pageContext.request.contextPath}/reservation/nouvelle?logementId=${logement.id}';
        </c:when>
        <c:otherwise>
        if (confirm('Vous devez être connecté pour réserver. Voulez-vous vous connecter maintenant ?')) {
            window.location.href = '${pageContext.request.contextPath}/auth/login?redirect=' +
                encodeURIComponent('/reservation/nouvelle?logementId=${logement.id}');
        }
        </c:otherwise>
        </c:choose>
    }
</script>

</body>
</html>




















<%--=============================--%>
<%--OLD--%>
<%--=============================--%>

<%--&lt;%&ndash;--%>
<%--  Created by IntelliJ IDEA.--%>
<%--  User: USER--%>
<%--  Date: 09/01/2026--%>
<%--  Time: 09:31--%>
<%--  To change this template use File | Settings | File Templates.--%>
<%--&ndash;%&gt;--%>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>

<%--<!DOCTYPE html>--%>
<%--<html lang="fr">--%>
<%--<head>--%>
<%--    <title>${logement.titre} - SquatRbnB</title>--%>

<%--    <!-- Leaflet -->--%>
<%--    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />--%>
<%--    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>--%>
<%--    <style>--%>

<%--        body {--%>
<%--            font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 30px;--%>
<%--        }--%>

<%--        .logement-card {--%>
<%--            max-width: 800px; margin: auto; background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);--%>
<%--        }--%>
<%--        h1 {--%>
<%--            margin-bottom: 15px;--%>
<%--            text-align: center;--%>
<%--        }--%>

<%--        p {--%>
<%--            margin: 10px 0;--%>
<%--        }--%>

<%--        .prix {--%>
<%--            color: #2e86de;--%>
<%--            font-weight: bold;--%>
<%--            font-size: 1.2em;--%>
<%--        }--%>

<%--        .retour {--%>
<%--            display: block;--%>
<%--            margin-top: 25px;--%>
<%--            text-align: center;--%>
<%--            text-decoration: none;--%>
<%--            color: #2e86de;--%>
<%--            font-weight: bold;--%>
<%--        }--%>

<%--        #map { height: 400px; border-radius: 12px; margin-top: 20px; }--%>
<%--    </style>--%>

<%--</head>--%>

<%--<body>--%>

<%--<!-- 🔍 FORMULAIRE DE RECHERCHE -->--%>
<%--<form action="afficherUnLogement" method="get" style="margin-bottom:30px; text-align:center;">--%>
<%--    <label for="id">Rechercher un logement par ID :</label><br><br>--%>

<%--    <input type="number" name="id" id="id" placeholder="Ex : 1" required>--%>

<%--    <br><br>--%>
<%--    <button type="submit">Rechercher</button>--%>
<%--</form>--%>


<%--<!-- 🏠 AFFICHAGE DU LOGEMENT -->--%>
<%--<c:if test="${logement == null}">--%>
<%--    <p style="text-align:center;">Logement introuvable.</p>--%>
<%--</c:if>--%>

<%--<c:if test="${logement != null}">--%>
<%--    <div class="logement-card">--%>

<%--        <h1>${logement.titre}</h1>--%>

<%--        <p><strong>Description :</strong> ${logement.description}</p>--%>
<%--        <p><strong>Adresse :</strong> ${logement.adresse.adresseFormatee}</p>--%>
<%--        <p><strong>Ville :</strong> ${logement.ville}</p>--%>

<%--        <p class="prix">--%>
<%--            Prix par nuit : ${logement.prixNuit} €--%>
<%--        </p>--%>

<%--        <p><strong>Capacité :</strong> ${logement.capaciteMax} personnes</p>--%>

<%--        <p>--%>
<%--            <strong>Disponible :</strong>--%>
<%--            <c:choose>--%>
<%--                <c:when test="${logement.disponible}">Oui</c:when>--%>
<%--                <c:otherwise>Non</c:otherwise>--%>
<%--            </c:choose>--%>
<%--        </p>--%>
<%--        <c:if test="${sessionScope.utilisateur.hasRole('HOTE') and sessionScope.utilisateurId == logement.hoteId}">--%>
<%--            <a href="modifierLogement?id=${logement.id}">--%>
<%--                Modifier ce logement--%>
<%--            </a>--%>
<%--            <br>--%>
<%--            <a href="/supprimer-logement?id=${logement.id}"--%>
<%--               onclick="return confirm('Voulez-vous vraiment supprimer ce logement ?');">--%>
<%--                Supprimer--%>
<%--            </a>--%>
<%--        </c:if>--%>
<%--        <div id="map"  style="height: 400px"></div>--%>
<%--    </div>--%>
<%--</c:if>--%>
<%--<script>--%>
<%--    <c:if test="${logement != null && logement.adresse != null}">--%>
<%--    // Coordonnées depuis l'objet Adresse--%>
<%--    const lat = ${logement.adresse.latitude != null ? logement.adresse.latitude : null};--%>
<%--    const lng = ${logement.adresse.longitude != null ? logement.adresse.longitude : null};--%>

<%--    const titre = "${logement.titre}";--%>
<%--    const adresse = "${logement.adresse.adresseFormatee}";--%>
<%--    const pays = "~${logement.adresse.pays}"--%>
<%--    const prix = "${logement.prixNuit}";--%>

<%--    // Initialiser la carte--%>
<%--    const map = L.map('map');--%>

<%--    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {--%>
<%--        maxZoom: 19,--%>
<%--        attribution: '© OpenStreetMap'--%>
<%--    }).addTo(map);--%>

<%--    // Si coordonnées en BDD--%>
<%--    if (lat !== null && lng !== null) {--%>
<%--        map.setView([lat, lng], 15);--%>
<%--        L.marker([lat, lng])--%>
<%--            .addTo(map)--%>
<%--            .bindPopup('<strong>' + titre + '</strong><br>' + adresse + '<br><em>' + prix + ' € / nuit</em>')--%>
<%--            .openPopup();--%>
<%--    } else {--%>
<%--        // Géocoder avec Nominatim--%>
<%--        fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(adresse + ', ' + pays))--%>
<%--            .then(response => response.json())--%>
<%--            .then(data => {--%>
<%--                if (data && data.length > 0) {--%>
<%--                    const latitude = parseFloat(data[0].lat);--%>
<%--                    const longitude = parseFloat(data[0].lon);--%>
<%--                    map.setView([latitude, longitude], 15);--%>
<%--                    L.marker([latitude, longitude])--%>
<%--                        .addTo(map)--%>
<%--                        .bindPopup('<strong>' + titre + '</strong><br>' + adresse + '<br><em>' + prix + ' € / nuit</em>')--%>
<%--                        .openPopup();--%>
<%--                }--%>
<%--            });--%>
<%--    }--%>
<%--    </c:if>--%>

<%--</script>--%>

<%--</body>--%>

<%--</html>--%>


