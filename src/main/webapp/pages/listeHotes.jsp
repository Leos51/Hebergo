<%--
  Created by IntelliJ IDEA.
  User: DEV01
  Date: 08/01/2026
  Time: 14:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>Liste des Hôtes - Squat'R BNB</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
</head>
<body>
<main>
  <div class="page-header">
    <div class="container">
      <h1><i class="fas fa-users me-2"></i>Nos Hôtes</h1>
      <p>Découvrez notre communauté d'hôtes de confiance</p>
      <span class="total-count">
                <i class="fas fa-user-check me-1"></i>
                ${not empty hotes ? hotes.size() : 0} hôte(s) inscrit(s)
            </span>
    </div>
  </div>
  <div class="container">
    <div class="search-bar">
      <form action="${pageContext.request.contextPath}/hote/liste" method="get" class="row g-3">
        <div class="col-md-4">
          <input type="text" class="form-control" name="recherche" placeholder="Rechercher un hôte..."
                 value="${param.recherche}">
        </div>
        <div class="col-md-3">
          <input type="text" class="form-control" name="ville" placeholder="Ville"
                 value="${param.ville}">
        </div>
        <div class="col-md-3">
          <select class="form-control" name="tri">
            <option value="recent">Plus récents</option>
            <option value="ancien">Plus anciens</option>
            <option value="nom">Nom (A-Z)</option>
          </select>
        </div>
        <div class="col-md-2">
          <button type="submit" class="btn btn-search w-100">
            <i class="fas fa-search me-1"></i>Rechercher
          </button>
        </div>
      </form>
    </div>

    <!-- Messages -->
    <c:if test="${not empty erreur}">
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i>${erreur}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    </c:if>

    <c:if test="${not empty message}">
      <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-2"></i>${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    </c:if>

    <!-- Liste des hôtes -->
    <c:choose>
      <c:when test="${empty hotes}">
        <div class="no-results">
          <i class="fas fa-user-slash"></i>
          <h3>Aucun hôte trouvé</h3>
          <p>Il n'y a pas encore d'hôte inscrit sur la plateforme.</p>
          <a href="${pageContext.request.contextPath}/hote/inscription" class="btn btn-primary mt-3">
            <i class="fas fa-user-plus me-2"></i>Devenir hôte
          </a>
        </div>
      </c:when>
      <c:otherwise>
        <div class="row g-4">
          <c:forEach var="hote" items="${hotes}">
            <div class="col-lg-4 col-md-6">
              <div class="hote-card">
                <!-- Avatar -->
                <div class="hote-avatar">
                  <c:choose>
                    <c:when test="${not empty hote.photoUrl}">
                      <img src="${hote.photoUrl}" alt="${hote.nomComplet}">
                    </c:when>
                    <c:otherwise>
                      ${hote.prenom.substring(0,1)}${hote.nom.substring(0,1)}
                    </c:otherwise>
                  </c:choose>
                </div>

                <!-- Corps de la carte -->
                <div class="hote-card-body">
                  <h5 class="hote-name">${hote.nomComplet}</h5>

                  <c:if test="${hote.verifie}">
                                        <span class="badge-verifie">
                                            <i class="fas fa-check-circle"></i> Vérifié
                                        </span>
                  </c:if>

                  <c:if test="${not empty hote.ville}">
                    <p class="hote-location">
                      <i class="fas fa-map-marker-alt"></i>
                        ${hote.ville}<c:if test="${not empty hote.pays}">, ${hote.pays}</c:if>
                    </p>
                  </c:if>

                  <c:if test="${not empty hote.description}">
                    <p class="hote-description">${hote.description}</p>
                  </c:if>

                  <div class="member-since">
                    <i class="fas fa-calendar-alt"></i>
                    Membre depuis
                    <fmt:parseDate value="${hote.dateInscription}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/>
                    <fmt:formatDate value="${parsedDate}" pattern="MMMM yyyy"/>
                  </div>

                  <!-- Statut -->
                  <div class="mt-2">
                    <c:choose>
                      <c:when test="${hote.actif}">
                                                <span class="badge-actif">
                                                    <i class="fas fa-circle me-1"></i>Actif
                                                </span>
                      </c:when>
                      <c:otherwise>
                                                <span class="badge-inactif">
                                                    <i class="fas fa-circle me-1"></i>Inactif
                                                </span>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>

                <!-- Footer -->
                <div class="hote-card-footer">
                  <a href="${pageContext.request.contextPath}/hote/detail?id=${hote.id}"
                     class="btn btn-voir-profil">
                    <i class="fas fa-user me-2"></i>Voir le profil
                  </a>
                </div>
              </div>
            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>

  </div>
  <!-- Footer -->
  <footer class="mt-5 py-4 bg-white">
    <div class="container text-center">
      <p class="text-muted mb-0">© 2024 Hébergo - Tous droits réservés</p>
    </div>
  </footer>
</main>

</body>
</html>
