<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 09/01/2026
  Time: 09:31
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <title>${logement.titre} - SquatRbnB</title>

    <!-- Leaflet -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
    <style>

        body {
            font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 30px;
        }

        .logement-card {
            max-width: 800px; margin: auto; background: white; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        h1 {
            margin-bottom: 15px;
            text-align: center;
        }

        p {
            margin: 10px 0;
        }

        .prix {
            color: #2e86de;
            font-weight: bold;
            font-size: 1.2em;
        }

        .retour {
            display: block;
            margin-top: 25px;
            text-align: center;
            text-decoration: none;
            color: #2e86de;
            font-weight: bold;
        }

        #map { height: 400px; border-radius: 12px; margin-top: 20px; }
    </style>

</head>

<body>

<!-- 🔍 FORMULAIRE DE RECHERCHE -->
<form action="afficherUnLogement" method="get" style="margin-bottom:30px; text-align:center;">
    <label for="id">Rechercher un logement par ID :</label><br><br>

    <input type="number" name="id" id="id" placeholder="Ex : 1" required>

    <br><br>
    <button type="submit">Rechercher</button>
</form>


<!-- 🏠 AFFICHAGE DU LOGEMENT -->
<c:if test="${logement == null}">
    <p style="text-align:center;">Logement introuvable.</p>
</c:if>

<c:if test="${logement != null}">
    <div class="logement-card">

        <h1>${logement.titre}</h1>

        <p><strong>Description :</strong> ${logement.description}</p>
        <p><strong>Adresse :</strong> ${logement.adresse.adresseFormatee}</p>
        <p><strong>Ville :</strong> ${logement.ville}</p>

        <p class="prix">
            Prix par nuit : ${logement.prixNuit} €
        </p>

        <p><strong>Capacité :</strong> ${logement.capaciteMax} personnes</p>

        <p>
            <strong>Disponible :</strong>
            <c:choose>
                <c:when test="${logement.disponible}">Oui</c:when>
                <c:otherwise>Non</c:otherwise>
            </c:choose>
        </p>
        <c:if test="${sessionScope.utilisateur.hasRole('HOTE') and sessionScope.utilisateurId == logement.hoteId}">
            <a href="modifierLogement?id=${logement.id}">
                Modifier ce logement
            </a>
            <br>
            <a href="/supprimer-logement?id=${logement.id}"
               onclick="return confirm('Voulez-vous vraiment supprimer ce logement ?');">
                Supprimer
            </a>
        </c:if>
        <div id="map"  style="height: 400px"></div>
    </div>
</c:if>
<script>
    <c:if test="${logement != null && logement.adresse != null}">
    // Coordonnées depuis l'objet Adresse
    const lat = ${logement.adresse.latitude != null ? logement.adresse.latitude : null};
    const lng = ${logement.adresse.longitude != null ? logement.adresse.longitude : null};

    const titre = "${logement.titre}";
    const adresse = "${logement.adresse.adresseFormatee}";
    const pays = "~${logement.adresse.pays}"
    const prix = "${logement.prixNuit}";

    // Initialiser la carte
    const map = L.map('map');

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
    }).addTo(map);

    // Si coordonnées en BDD
    if (lat !== null && lng !== null) {
        map.setView([lat, lng], 15);
        L.marker([lat, lng])
            .addTo(map)
            .bindPopup('<strong>' + titre + '</strong><br>' + adresse + '<br><em>' + prix + ' € / nuit</em>')
            .openPopup();
    } else {
        // Géocoder avec Nominatim
        fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(adresse + ', ' + pays))
            .then(response => response.json())
            .then(data => {
                if (data && data.length > 0) {
                    const latitude = parseFloat(data[0].lat);
                    const longitude = parseFloat(data[0].lon);
                    map.setView([latitude, longitude], 15);
                    L.marker([latitude, longitude])
                        .addTo(map)
                        .bindPopup('<strong>' + titre + '</strong><br>' + adresse + '<br><em>' + prix + ' € / nuit</em>')
                        .openPopup();
                }
            });
    }
    </c:if>

</script>

</body>

</html>


