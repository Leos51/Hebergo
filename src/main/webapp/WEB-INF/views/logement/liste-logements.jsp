<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des logements</title>

    <style>
        html, body {
            height: 100%;
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
        }

        body {
            display: flex;
            flex-direction: column;
        }

        header {
            background-color: white;
            padding: 20px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            text-align: center;
        }

        header h1 {
            margin: 0;
            color: #ff385c;
        }

        main {
            flex: 1;
            padding: 30px;
        }

        .logements-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 25px;
        }

        .logement-card {
            background-color: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }

        .logement-card img {
            width: 100%;
            height: 180px;
            object-fit: cover;
        }

        .logement-content {
            padding: 15px;
        }

        .logement-content h3 {
            margin-top: 0;
            margin-bottom: 8px;
        }

        .prix {
            font-weight: bold;
            color: #ff385c;
        }

        .actions a {
            display: inline-block;
            margin-right: 10px;
            margin-top: 10px;
            text-decoration: none;
            color: #2e86de;
            font-weight: bold;
        }

        footer {
            background-color: #222;
            color: white;
            text-align: center;
            padding: 15px;
        }
    </style>
    <!-- Font Awesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

</head>

<body>

<!-- ===== HEADER ===== -->
<header>
    <h1>SquatAirBnb</h1>
</header>

<!-- ===== CONTENU ===== -->
<main>
    <div class="logements-container">
        <c:forEach var="logement" items="${listeLogements}">

            <div class="logement-card">

                <!-- 📸 PHOTO -->
                <c:choose>
                    <c:when test="${not empty logement.photoPrincipale}">
                        <img src="${logement.photoPrincipale}"
                             alt="${logement.titre}">
                    </c:when>
                    <c:otherwise>
<%--                        <img src="${pageContext.request.contextPath}/images/logements/default.jpg"--%>
<%--                             alt="Photo par défaut">--%>
                        <i class="fas fa-home fa-3x text-muted"></i>
                    </c:otherwise>
                </c:choose>


                <!-- 🏠 INFOS -->
                <div class="logement-content">
                    <h3>${logement.titre}</h3>
                    <p>${logement.ville}</p>

                    <p class="prix">${logement.prixNuit} € / nuit</p>
                    <p>${logement.capaciteMax} ${logement.capaciteMax > 1 ?"personnes":"personne"}</p>

                    <!-- 🔗 ACTIONS -->
                    <div class="actions">
                        <a href="${pageContext.request.contextPath}/logement?id=${logement.id}">Voir</a>
                        <c:if test="${sessionScope.utilisateur.hasRole('HOTE') and sessionScope.utilisateurId == logement.hoteId}">
                            <a href="modifierLogement?id=${logement.id}">Modifier</a>
                            <a href="/supprimer-logement?id=${logement.id}"
                               onclick="return confirm('Voulez-vous vraiment supprimer ce logement ?');">
                                Supprimer
                            </a>
                        </c:if>

                    </div>
                </div>

            </div>

        </c:forEach>

    </div>

</main>

<!-- ===== FOOTER ===== -->
<footer>
    © 2026 SquatAirBnb – Projet Java EE
</footer>

</body>
</html>

