<%--
  Created by IntelliJ IDEA.
  User: DEV01
  Date: 08/01/2026
  Time: 15:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>Gestion des Hotes</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
  <link href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/listeHotes.css">
</head>
<body>
<nav class="sidebar">
  <div class="sidebar-brand">
    <i class="fas fa-home"></i> Hébergo
  </div>
  <ul class="sidebar-menu">
    <li>
      <a href="${pageContext.request.contextPath}/admin">
        <i class="fas fa-tachometer-alt"></i> Tableau de bord
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/hotes" class="active">
        <i class="fas fa-user-tie"></i> Gestion des Hôtes
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/locataires">
        <i class="fas fa-users"></i> Gestion des Locataires
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/biens">
        <i class="fas fa-building"></i> Gestion des Biens
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/reservations">
        <i class="fas fa-calendar-check"></i> Réservations
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/admin/statistiques">
        <i class="fas fa-chart-bar"></i> Statistiques
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/deconnexion">
        <i class="fas fa-sign-out-alt"></i> Déconnexion
      </a>
    </li>
  </ul>
</nav>

<!-- Main Content -->
<div class="main-content">
  <!-- Header -->
  <div class="page-header">
    <div>
      <h1><i class="fas fa-user-tie me-2"></i>Gestion des Hôtes</h1>
      <small class="text-muted">Gérez les comptes des propriétaires</small>
    </div>
    <div>
                <span class="badge bg-secondary fs-6">
                    ${not empty hotes ? hotes.size() : 0} hôte(s) au total
                </span>
    </div>
  </div>

  <!-- Messages -->
  <c:if test="${param.message == 'success'}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <i class="fas fa-check-circle me-2"></i>Opération effectuée avec succès !
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  <c:if test="${param.message == 'supprime'}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <i class="fas fa-trash me-2"></i>Hôte supprimé avec succès.
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  <c:if test="${param.error == 'echec'}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="fas fa-exclamation-circle me-2"></i>Une erreur est survenue.
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  <c:if test="${not empty erreur}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="fas fa-exclamation-circle me-2"></i>${erreur}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <!-- Statistiques rapides -->
  <div class="row mb-4">
    <div class="col-md-3">
      <div class="stat-card d-flex align-items-center">
        <div class="stat-icon bg-primary me-3">
          <i class="fas fa-user-tie"></i>
        </div>
        <div>
          <div class="stat-value">${not empty hotes ? hotes.size() : 0}</div>
          <div class="stat-label">Total Hôtes</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card d-flex align-items-center">
        <div class="stat-icon bg-success me-3">
          <i class="fas fa-check-circle"></i>
        </div>
        <div>
          <c:set var="nbActifs" value="0"/>
          <c:forEach var="h" items="${hotes}">
            <c:if test="${h.actif}"><c:set var="nbActifs" value="${nbActifs + 1}"/></c:if>
          </c:forEach>
          <div class="stat-value">${nbActifs}</div>
          <div class="stat-label">Actifs</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card d-flex align-items-center">
        <div class="stat-icon bg-warning me-3">
          <i class="fas fa-user-check"></i>
        </div>
        <div>
          <c:set var="nbVerifies" value="0"/>
          <c:forEach var="h" items="${hotes}">
            <c:if test="${h.verifie}"><c:set var="nbVerifies" value="${nbVerifies + 1}"/></c:if>
          </c:forEach>
          <div class="stat-value">${nbVerifies}</div>
          <div class="stat-label">Vérifiés</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card d-flex align-items-center">
        <div class="stat-icon bg-danger me-3">
          <i class="fas fa-user-clock"></i>
        </div>
        <div>
          <c:set var="nbNonVerifies" value="0"/>
          <c:forEach var="h" items="${hotes}">
            <c:if test="${!h.verifie}"><c:set var="nbNonVerifies" value="${nbNonVerifies + 1}"/></c:if>
          </c:forEach>
          <div class="stat-value">${nbNonVerifies}</div>
          <div class="stat-label">En attente</div>
        </div>
      </div>
    </div>
  </div>

  <!-- Filtres -->
  <div class="filters-bar">
    <form action="${pageContext.request.contextPath}/admin/hotes" method="get" class="row g-3 align-items-center">
      <div class="col-md-3">
        <select name="filtre" class="form-select">
          <option value="">Tous les hôtes</option>
          <option value="actif" ${filtre == 'actif' ? 'selected' : ''}>Actifs uniquement</option>
          <option value="inactif" ${filtre == 'inactif' ? 'selected' : ''}>Inactifs uniquement</option>
          <option value="verifie" ${filtre == 'verifie' ? 'selected' : ''}>Vérifiés</option>
          <option value="non_verifie" ${filtre == 'non_verifie' ? 'selected' : ''}>Non vérifiés</option>
        </select>
      </div>
      <div class="col-md-4">
        <input type="text" name="recherche" class="form-control" placeholder="Rechercher par nom ou email..." value="${recherche}">
      </div>
      <div class="col-md-2">
        <button type="submit" class="btn btn-primary w-100">
          <i class="fas fa-search me-1"></i>Filtrer
        </button>
      </div>
      <div class="col-md-3 text-end">
        <a href="${pageContext.request.contextPath}/admin/hotes/export" class="btn btn-outline-secondary">
          <i class="fas fa-download me-1"></i>Exporter CSV
        </a>
      </div>
    </form>
  </div>

  <!-- Table des hôtes -->
  <div class="table-container">
    <c:choose>
      <c:when test="${empty hotes}">
        <div class="text-center py-5">
          <i class="fas fa-user-slash fa-4x text-muted mb-3"></i>
          <h4 class="text-muted">Aucun hôte trouvé</h4>
          <p class="text-muted">Il n'y a pas encore d'hôte inscrit sur la plateforme.</p>
        </div>
      </c:when>
      <c:otherwise>
        <table id="hotesTable" class="table table-hover">
          <thead>
          <tr>
            <th>ID</th>
            <th>Hôte</th>
            <th>Email</th>
            <th>Téléphone</th>
            <th>Ville</th>
            <th>Statut</th>
            <th>Vérification</th>
            <th>Inscription</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="hote" items="${hotes}">
            <tr>
              <td><strong>#${hote.id}</strong></td>
              <td>
                <div class="d-flex align-items-center">
                  <div class="user-avatar me-2">
                      ${hote.prenom.substring(0,1)}${hote.nom.substring(0,1)}
                  </div>
                  <div>
                    <strong>${hote.prenom} ${hote.nom}</strong>
                  </div>
                </div>
              </td>
              <td>${hote.email}</td>
              <td>${not empty hote.telephone ? hote.telephone : '-'}</td>
              <td>${not empty hote.ville ? hote.ville : '-'}</td>
              <td>
                <c:choose>
                  <c:when test="${hote.actif}">
                    <span class="badge badge-actif">Actif</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge badge-inactif">Inactif</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${hote.verifie}">
                                                <span class="badge badge-verifie">
                                                    <i class="fas fa-check me-1"></i>Vérifié
                                                </span>
                  </c:when>
                  <c:otherwise>
                                                <span class="badge badge-non-verifie">
                                                    <i class="fas fa-clock me-1"></i>En attente
                                                </span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <fmt:parseDate value="${hote.dateInscription}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/>
                <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy"/>
              </td>
              <td>
                <div class="btn-group">
                  <!-- Voir détails -->
                  <a href="${pageContext.request.contextPath}/admin/hotes/detail?id=${hote.id}"
                     class="btn btn-sm btn-outline-primary btn-action" title="Voir détails">
                    <i class="fas fa-eye"></i>
                  </a>

                  <!-- Activer/Désactiver -->
                  <c:choose>
                    <c:when test="${hote.actif}">
                      <form action="${pageContext.request.contextPath}/admin/hotes/activer" method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${hote.id}">
                        <input type="hidden" name="action" value="desactiver">
                        <button type="submit" class="btn btn-sm btn-outline-warning btn-action" title="Désactiver">
                          <i class="fas fa-ban"></i>
                        </button>
                      </form>
                    </c:when>
                    <c:otherwise>
                      <form action="${pageContext.request.contextPath}/admin/hotes/activer" method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${hote.id}">
                        <input type="hidden" name="action" value="activer">
                        <button type="submit" class="btn btn-sm btn-outline-success btn-action" title="Activer">
                          <i class="fas fa-check"></i>
                        </button>
                      </form>
                    </c:otherwise>
                  </c:choose>

                  <!-- Vérifier -->
                  <c:if test="${!hote.verifie}">
                    <form action="${pageContext.request.contextPath}/admin/hotes/verifier" method="post" style="display:inline;">
                      <input type="hidden" name="id" value="${hote.id}">
                      <input type="hidden" name="action" value="verifier">
                      <button type="submit" class="btn btn-sm btn-outline-info btn-action" title="Vérifier">
                        <i class="fas fa-user-check"></i>
                      </button>
                    </form>
                  </c:if>

                  <!-- Supprimer -->
                  <button type="button" class="btn btn-sm btn-outline-danger btn-action"
                          title="Supprimer" data-bs-toggle="modal"
                          data-bs-target="#deleteModal${hote.id}">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>

                <!-- Modal de confirmation de suppression -->
                <div class="modal fade" id="deleteModal${hote.id}" tabindex="-1">
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h5 class="modal-title">Confirmer la suppression</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                      </div>
                      <div class="modal-body">
                        <p>Êtes-vous sûr de vouloir supprimer l'hôte <strong>${hote.prenom} ${hote.nom}</strong> ?</p>
                        <p class="text-danger"><i class="fas fa-exclamation-triangle me-1"></i>Cette action est irréversible et supprimera également tous ses biens et réservations.</p>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                        <form action="${pageContext.request.contextPath}/admin/hotes/supprimer" method="post" style="display:inline;">
                          <input type="hidden" name="id" value="${hote.id}">
                          <button type="submit" class="btn btn-danger">
                            <i class="fas fa-trash me-1"></i>Supprimer
                          </button>
                        </form>
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
<script>
  $(document).ready(function() {
    $('#hotesTable').DataTable({
      language: {
        url: 'https://cdn.datatables.net/plug-ins/1.13.6/i18n/fr-FR.json'
      },
      pageLength: 25,
      order: [[0, 'desc']]
    });
  });
</script>

</body>
</html>
