<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Réservations - SquatRBnB</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Header / Navbar -->
<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="title" value="Dashboard Hôte"/>
</jsp:include>

<jsp:include page="/WEB-INF/views/components/sidebar-hote.jsp">
    <jsp:param name="active" value="reservations"/>
</jsp:include>
<main class="main-content-dashboard">

        <!-- En-tête -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0">Mes Réservations</h1>

            <c:if test="${nbEnAttente > 0}">
            <span class="badge bg-warning text-dark">
                <i class="fas fa-clock me-1"></i>
                ${nbEnAttente} en attente
            </span>
            </c:if>
        </div>

        <!-- Flash message -->
        <c:if test="${not empty sessionScope.flash}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.flash}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="flash" scope="session"/>
        </c:if>

        <!-- Filtres -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/hote/reservations" class="row g-3">

                    <!-- Filtre par statut -->
                    <div class="col-md-4">
                        <label class="form-label">Statut</label>
                        <select name="statut" class="form-select" onchange="this.form.submit()">
                            <option value="">Tous les statuts</option>
                            <option value="EN_ATTENTE" ${param.statut == 'EN_ATTENTE' ? 'selected' : ''}>
                                En attente
                            </option>
                            <option value="CONFIRMEE" ${param.statut == 'CONFIRMEE' ? 'selected' : ''}>
                                Confirmée
                            </option>
                            <option value="EN_COURS" ${param.statut == 'EN_COURS' ? 'selected' : ''}>
                                En cours
                            </option>
                            <option value="TERMINEE" ${param.statut == 'TERMINEE' ? 'selected' : ''}>
                                Terminée
                            </option>
                            <option value="ANNULEE" ${param.statut == 'ANNULEE' ? 'selected' : ''}>
                                Annulée
                            </option>
                        </select>
                    </div>

                    <!-- Filtre par logement -->
                    <div class="col-md-6">
                        <label class="form-label">Logement</label>
                        <select name="logementId" class="form-select" onchange="this.form.submit()">
                            <option value="">Tous les logements</option>
                            <c:forEach var="logement" items="${mesLogements}">
                                <option value="${logement.id}"
                                    ${param.logementId == logement.id ? 'selected' : ''}>
                                        ${logement.titre}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">&nbsp;</label>
                        <a href="${pageContext.request.contextPath}/hote/reservations"
                           class="btn btn-outline-secondary w-100">
                            <i class="fas fa-redo me-1"></i> Réinitialiser
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Liste des réservations -->
        <c:choose>
            <c:when test="${empty reservations}">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle me-2"></i>
                    Aucune réservation trouvée.
                </div>
            </c:when>
            <c:otherwise>
                <!-- Tableau des réservations -->
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                        <tr>
                            <th>Référence</th>
                            <th>Logement</th>
                            <th>Locataire</th>
                            <th>Dates</th>
                            <th>Voyageurs</th>
                            <th>Montant</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="resa" items="${reservations}">
                            <tr>
                                <!-- Référence -->
                                <td>
                                    <strong>${resa.reference}</strong><br>
                                    <small class="text-muted">
                                        <fmt:formatDate value="${resa.dateReservation}"
                                                        pattern="dd/MM/yyyy HH:mm"/>
                                    </small>
                                </td>

                                <!-- Logement -->
                                <td>
                                    <a href="${pageContext.request.contextPath}/hote/logement/${resa.logementId}">
                                            ${resa.logementTitre}
                                    </a>
                                </td>

                                <!-- Locataire -->
                                <td>
                                    <div>
                                        <i class="fas fa-user me-1"></i>
                                            ${resa.locatairePrenom} ${resa.locataireNom}
                                    </div>
                                    <small class="text-muted">${resa.locataireEmail}</small>
                                </td>

                                <!-- Dates -->
                                <td>
                                    <div>
                                        <i class="fas fa-calendar me-1"></i>
                                        <fmt:formatDate value="${resa.dateDebut}" pattern="dd/MM/yyyy"/>
                                    </div>
                                    <div>
                                        <i class="fas fa-arrow-right me-1"></i>
                                        <fmt:formatDate value="${resa.dateFin}" pattern="dd/MM/yyyy"/>
                                    </div>
                                    <small class="text-muted">${resa.nbNuits} nuit(s)</small>
                                </td>

                                <!-- Voyageurs -->
                                <td>
                                    <div>
                                        <i class="fas fa-users me-1"></i>
                                            ${resa.nbVoyageurs} voyageur(s)
                                    </div>
                                    <small class="text-muted">
                                            ${resa.nbAdultes} adulte(s)
                                        <c:if test="${resa.nbEnfants > 0}">
                                            , ${resa.nbEnfants} enfant(s)
                                        </c:if>
                                    </small>
                                </td>

                                <!-- Montant -->
                                <td>
                                    <div class="fw-bold">
                                        <fmt:formatNumber value="${resa.prixTotal}"
                                                          type="currency"
                                                          currencyCode="${resa.devise}"/>
                                    </div>
                                    <small class="text-muted">
                                        <fmt:formatNumber value="${resa.prixNuit}"
                                                          type="currency"
                                                          currencyCode="${resa.devise}"/> / nuit
                                    </small>
                                </td>

                                <!-- Statut -->
                                <td>
                                    <c:choose>
                                        <c:when test="${resa.statut == 'EN_ATTENTE'}">
                                            <span class="badge bg-warning">
                                                <i class="fas fa-clock me-1"></i>
                                                En attente
                                            </span>
                                        </c:when>
                                        <c:when test="${resa.statut == 'CONFIRMEE'}">
                                            <span class="badge bg-success">
                                                <i class="fas fa-check me-1"></i>
                                                Confirmée
                                            </span>
                                        </c:when>
                                        <c:when test="${resa.statut == 'EN_COURS'}">
                                            <span class="badge bg-info">
                                                <i class="fas fa-play me-1"></i>
                                                En cours
                                            </span>
                                        </c:when>
                                        <c:when test="${resa.statut == 'TERMINEE'}">
                                            <span class="badge bg-secondary">
                                                <i class="fas fa-flag-checkered me-1"></i>
                                                Terminée
                                            </span>
                                        </c:when>
                                        <c:when test="${resa.statut == 'ANNULEE'}">
                                            <span class="badge bg-danger">
                                                <i class="fas fa-times me-1"></i>
                                                Annulée
                                            </span>
                                            <c:if test="${not empty resa.annuleePar}">
                                                <br><small class="text-muted">Par: ${resa.annuleePar}</small>
                                            </c:if>
                                        </c:when>
                                        <c:when test="${resa.statut == 'REFUSEE'}">
                                            <span class="badge bg-dark">
                                                <i class="fas fa-ban me-1"></i>
                                                Refusée
                                            </span>
                                        </c:when>
                                    </c:choose>
                                </td>

                                <!-- Actions -->
                                <td>
                                    <div class="btn-group" role="group">
                                        <!-- Bouton Confirmer (si en attente) -->
                                        <c:if test="${resa.statut == 'EN_ATTENTE'}">
                                            <form action="${pageContext.request.contextPath}/hote/reservation/${resa.id}/confirmer"
                                                  method="post" style="display: inline;">
                                                <button type="submit"
                                                        class="btn btn-sm btn-success"
                                                        title="Confirmer">
                                                    <i class="fas fa-check"></i>
                                                </button>
                                            </form>
                                        </c:if>

                                        <!-- Bouton Voir détails -->
                                        <button type="button"
                                                class="btn btn-sm btn-outline-primary"
                                                data-bs-toggle="modal"
                                                data-bs-target="#detailModal${resa.id}"
                                                title="Voir détails">
                                            <i class="fas fa-eye"></i>
                                        </button>

                                        <!-- Bouton Annuler (si annulable) -->
                                        <c:if test="${resa.annulable}">
                                            <button type="button"
                                                    class="btn btn-sm btn-danger"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#annulerModal${resa.id}"
                                                    title="Annuler">
                                                <i class="fas fa-times"></i>
                                            </button>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>

                            <!-- Modal Détails -->
                            <div class="modal fade" id="detailModal${resa.id}" tabindex="-1">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">
                                                Détails de la réservation ${resa.reference}
                                            </h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="row g-3">
                                                <!-- Informations générales -->
                                                <div class="col-md-6">
                                                    <h6>Informations générales</h6>
                                                    <p><strong>Référence:</strong> ${resa.reference}</p>
                                                    <p><strong>Logement:</strong> ${resa.logementTitre}</p>
                                                    <p><strong>Date de réservation:</strong>
                                                        <fmt:formatDate value="${resa.dateReservation}"
                                                                        pattern="dd/MM/yyyy HH:mm"/>
                                                    </p>
                                                </div>

                                                <!-- Voyageurs -->
                                                <div class="col-md-6">
                                                    <h6>Voyageurs</h6>
                                                    <p><strong>Total:</strong> ${resa.nbVoyageurs}</p>
                                                    <p><strong>Adultes:</strong> ${resa.nbAdultes}</p>
                                                    <p><strong>Enfants:</strong> ${resa.nbEnfants}</p>
                                                </div>

                                                <!-- Détails financiers -->
                                                <div class="col-12">
                                                    <h6>Détails financiers</h6>
                                                    <table class="table table-sm">
                                                        <tr>
                                                            <td>Prix par nuit:</td>
                                                            <td class="text-end">
                                                                <fmt:formatNumber value="${resa.prixNuit}"
                                                                                  type="currency"
                                                                                  currencyCode="${resa.devise}"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td>Nombre de nuits:</td>
                                                            <td class="text-end">${resa.nbNuits}</td>
                                                        </tr>
                                                        <tr>
                                                            <td>Sous-total logement:</td>
                                                            <td class="text-end">
                                                                <fmt:formatNumber value="${resa.prixSousTotal}"
                                                                                  type="currency"
                                                                                  currencyCode="${resa.devise}"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td>Frais de service:</td>
                                                            <td class="text-end">
                                                                <fmt:formatNumber value="${resa.fraisService}"
                                                                                  type="currency"
                                                                                  currencyCode="${resa.devise}"/>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td>Frais de ménage:</td>
                                                            <td class="text-end">
                                                                <fmt:formatNumber value="${resa.fraisMenage}"
                                                                                  type="currency"
                                                                                  currencyCode="${resa.devise}"/>
                                                            </td>
                                                        </tr>
                                                        <c:if test="${resa.reduction > 0}">
                                                            <tr class="text-success">
                                                                <td>Réduction:</td>
                                                                <td class="text-end">
                                                                    -<fmt:formatNumber value="${resa.reduction}"
                                                                                       type="currency"
                                                                                       currencyCode="${resa.devise}"/>
                                                                </td>
                                                            </tr>
                                                        </c:if>
                                                        <tr class="fw-bold">
                                                            <td>Total:</td>
                                                            <td class="text-end">
                                                                <fmt:formatNumber value="${resa.prixTotal}"
                                                                                  type="currency"
                                                                                  currencyCode="${resa.devise}"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </div>

                                                <!-- Message du locataire -->
                                                <c:if test="${not empty resa.messageLocataire}">
                                                    <div class="col-12">
                                                        <h6>Message du locataire</h6>
                                                        <div class="alert alert-info">
                                                                ${resa.messageLocataire}
                                                        </div>
                                                    </div>
                                                </c:if>

                                                <!-- Réponse de l'hôte -->
                                                <c:if test="${not empty resa.reponseHote}">
                                                    <div class="col-12">
                                                        <h6>Votre réponse</h6>
                                                        <div class="alert alert-secondary">
                                                                ${resa.reponseHote}
                                                        </div>
                                                    </div>
                                                </c:if>

                                                <!-- Informations d'annulation -->
                                                <c:if test="${resa.statut == 'ANNULEE'}">
                                                    <div class="col-12">
                                                        <h6>Informations d'annulation</h6>
                                                        <p><strong>Annulée par:</strong> ${resa.annuleePar}</p>
                                                        <p><strong>Date:</strong>
                                                            <fmt:formatDate value="${resa.dateAnnulation}"
                                                                            pattern="dd/MM/yyyy HH:mm"/>
                                                        </p>
                                                        <c:if test="${not empty resa.motifAnnulation}">
                                                            <p><strong>Motif:</strong></p>
                                                            <div class="alert alert-warning">
                                                                    ${resa.motifAnnulation}
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${resa.montantRembourse > 0}">
                                                            <p><strong>Montant remboursé:</strong>
                                                                <fmt:formatNumber value="${resa.montantRembourse}"
                                                                                  type="currency"
                                                                                  currencyCode="${resa.devise}"/>
                                                            </p>
                                                        </c:if>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                Fermer
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Modal Annulation -->
                            <div class="modal fade" id="annulerModal${resa.id}" tabindex="-1">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header bg-danger text-white">
                                            <h5 class="modal-title">Annuler la réservation</h5>
                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/hote/reservation/${resa.id}/annuler"
                                              method="post">
                                            <div class="modal-body">
                                                <p>Êtes-vous sûr de vouloir annuler cette réservation ?</p>
                                                <div class="mb-3">
                                                    <label for="motif${resa.id}" class="form-label">
                                                        Motif d'annulation <span class="text-danger">*</span>
                                                    </label>
                                                    <textarea id="motif${resa.id}"
                                                              name="motif"
                                                              class="form-control"
                                                              rows="3"
                                                              required></textarea>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                    Non, garder
                                                </button>
                                                <button type="submit" class="btn btn-danger">
                                                    Oui, annuler
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav>
                        <ul class="pagination justify-content-center">
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/hote/reservations?page=${i}&statut=${param.statut}&logementId=${param.logementId}">
                                            ${i}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>



</main>
<!-- Footer -->
<jsp:include page="../components/footer.jsp"/>



