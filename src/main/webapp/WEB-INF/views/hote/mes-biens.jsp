<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="title" value="Mes logements"/>
</jsp:include>

<jsp:include page="/WEB-INF/views/components/sidebar-hote.jsp">
    <jsp:param name="active" value="logements"/>
</jsp:include>

<main class="main-content-dashboard">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 mb-1">Mes logements</h1>
            <p class="text-muted mb-0">${logements.size()} logement${logements.size() > 1 ? "s":""} au total</p>
        </div>
        <a href="${pageContext.request.contextPath}/hote/logement/nouveau" class="btn btn-primary-hebergo">
            <i class="fas fa-plus me-2"></i>Ajouter un logement
        </a>
    </div>

    <!-- Message flash -->
<%--    <c:if test="${not empty sessionScope.flash}">--%>
<%--        <div class="alert alert-${sessionScope.flash}.type} alert-dismissible fade show" role="alert">--%>
<%--                ${sessionScope.flash}--%>
<%--            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>--%>
<%--        </div>--%>
<%--    </c:if>--%>

    <!-- Filtres -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <form class="row g-3 align-items-end" method="get">
                <div class="col-md-3">
                    <label class="form-label small text-muted">Statut</label>
                    <select name="statut" class="form-select">
                        <option value="">Tous les statuts</option>
                        <option value="DISPONIBLE" ${param.statut == 'DISPONIBLE' ? 'selected' : ''}>Disponible</option>
                        <option value="INDISPONIBLE" ${param.statut == 'INDISPONIBLE' ? 'selected' : ''}>Indisponible</option>
                        <option value="BROUILLON" ${param.statut == 'BROUILLON' ? 'selected' : ''}>Brouillon</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small text-muted">Type</label>
                    <select name="type" class="form-select">
                        <option value="">Tous les types</option>
                        <c:forEach var="type" items="${types}">
                            <option value="${type.id}" ${param.type == type.id.toString() ? 'selected' : ''}>${type.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small text-muted">Rechercher</label>
                    <input type="text" name="q" class="form-control" placeholder="Titre, ville..." value="${param.q}">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-outline-secondary w-100">
                        <i class="fas fa-filter me-2"></i>Filtrer
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Liste des logements -->
    <c:choose>
        <c:when test="${not empty logements}">
            <div class="row g-4">
                <c:forEach var="logement" items="${logements}">
                    <div class="col-lg-4 col-md-6">
                        <div class="card border-0 shadow-sm h-100">
                            <!-- Image -->
                            <div class="position-relative">
                                <c:choose>
                                    <c:when test="${not empty logement.photoPrincipale}">
                                        <img src="${logement.photoPrincipale}"
                                             class="card-img-top" alt="${logement.titre}"
                                             style="height: 200px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="card-img-top bg-light d-flex align-items-center justify-content-center"
                                             style="height: 200px;">
                                            <i class="fas fa-home fa-3x text-muted"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <!-- Badge statut -->
                                <c:if test="${logement.statut != null}">
                                    <span class="position-absolute top-0 end-0 m-2 badge bg-${logement.statut.cssClass}">
                                            ${logement.statut.libelle}
                                    </span>
                                </c:if>
                            </div>

                            <div class="card-body">
                                <h5 class="card-title mb-1">${logement.titre}</h5>
                                <p class="text-muted small mb-2">
                                    <i class="fas fa-map-marker-alt me-1"></i>${logement.ville}
                                    <c:if test="${not empty logement.typeLogement}">
                                        <span class="ms-2">
                                            <i class="${logement.typeLogement.icone} me-1"></i>${logement.typeLogement.nom}
                                        </span>
                                    </c:if>
                                </p>

                                <!-- Infos -->
                                <div class="d-flex gap-3 text-muted small mb-3">
                                    <c:if test="${logement.nbChambres != null}">
                                        <span><i class="fas fa-bed me-1"></i>${logement.nbChambres} ch.</span>
                                    </c:if>
                                    <c:if test="${logement.capaciteMax != null}">
                                        <span><i class="fas fa-users me-1"></i>${logement.capaciteMax} pers.</span>
                                    </c:if>
                                    <c:if test="${logement.superficie != null}">
                                        <span><i class="fas fa-ruler-combined me-1"></i>${logement.superficie}m²</span>
                                    </c:if>
                                </div>

                                <!-- Prix et note -->
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <div class="fw-bold text-primary-hebergo">
                                        <fmt:formatNumber value="${logement.prixNuit}" type="number" maxFractionDigits="0"/>€
                                        <span class="fw-normal text-muted">/ nuit</span>
                                    </div>
                                    <c:if test="${logement.noteMoyenne != null && logement.noteMoyenne > 0}">
                                        <div class="text-muted">
                                            <i class="fas fa-star text-warning"></i>
                                            <fmt:formatNumber value="${logement.noteMoyenne}" maxFractionDigits="1"/>
                                            <span class="small">(${logement.nbAvis})</span>
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Stats -->
                                <div class="d-flex gap-3 text-muted small mb-3 pb-3 border-bottom">
                                    <span><i class="fas fa-calendar-check me-1"></i>${logement.nbReservations} réservation(s)</span>
                                    <span><i class="fas fa-comment me-1"></i>${logement.nbAvis} avis</span>
                                </div>

                                <!-- Actions -->
                                <div class="d-flex gap-2">
                                    <a href="${pageContext.request.contextPath}/hote/logement/${logement.id}/modifier"
                                       class="btn btn-outline-primary btn-sm flex-grow-1">
                                        <i class="fas fa-edit me-1"></i>Modifier
                                    </a>
                                    <a href="${pageContext.request.contextPath}/logement?id=${logement.id}"
                                       class="btn btn-outline-secondary btn-sm" target="_blank" title="Voir l'annonce">
                                        <i class="fas fa-external-link-alt"></i>
                                    </a>
                                    <div class="dropdown">
                                        <button class="btn btn-outline-secondary btn-sm dropdown-toggle"
                                                type="button" data-bs-toggle="dropdown">
                                            <i class="fas fa-ellipsis-v"></i>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end">
                                            <li>
                                                <a class="dropdown-item" href="${pageContext.request.contextPath}/hote/logement/${logement.id}/calendrier">
                                                    <i class="fas fa-calendar me-2"></i>Calendrier
                                                </a>
                                            </li>
                                            <li>
                                                <a class="dropdown-item" href="${pageContext.request.contextPath}/hote/logement/${logement.id}/tarifs">
                                                    <i class="fas fa-euro-sign me-2"></i>Tarifs
                                                </a>
                                            </li>
                                            <li>
                                                <a class="dropdown-item" href="${pageContext.request.contextPath}/hote/logement/${logement.id}/stats">
                                                    <i class="fas fa-chart-bar me-2"></i>Statistiques
                                                </a>
                                            </li>
                                            <li><hr class="dropdown-divider"></li>
                                            <c:choose>
                                                <c:when test="${logement.statut == 'DISPONIBLE'}">
                                                    <li>
                                                        <a class="dropdown-item text-warning" href="#"
                                                           onclick="changerStatut(${logement.id}, 'INDISPONIBLE'); return false;">
                                                            <i class="fas fa-pause me-2"></i>Désactiver
                                                        </a>
                                                    </li>
                                                </c:when>
                                                <c:otherwise>
                                                    <li>
                                                        <a class="dropdown-item text-success" href="#"
                                                           onclick="changerStatut(${logement.id}, 'DISPONIBLE'); return false;">
                                                            <i class="fas fa-play me-2"></i>Activer
                                                        </a>
                                                    </li>
                                                </c:otherwise>
                                            </c:choose>
                                            <li>
                                                <a class="dropdown-item text-danger" href="#"
                                                   onclick="supprimerLogement(${logement.id}); return false;">
                                                    <i class="fas fa-trash me-2"></i>Supprimer
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <!-- Aucun logement -->
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center py-5">
                    <i class="fas fa-home fa-4x text-muted mb-4"></i>
                    <h4>Vous n'avez pas encore de logement</h4>
                    <p class="text-muted mb-4">
                        Publiez votre premier squat et commencez à recevoir des réservations
                    </p>
                    <a href="${pageContext.request.contextPath}/hote/logement/nouveau" class="btn btn-primary-hebergo btn-lg">
                        <i class="fas fa-plus me-2"></i>Ajouter mon premier logement
                    </a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<!-- Formulaires cachés pour les actions POST -->
<form id="formStatut" method="post" style="display: none;">
    <input type="hidden" name="action" value="changerStatut">
    <input type="hidden" name="logementId" id="statutLogementId">
    <input type="hidden" name="statut" id="nouveauStatut">
</form>

<form id="formSupprimer" method="post" style="display: none;">
    <input type="hidden" name="action" value="supprimer">
    <input type="hidden" name="logementId" id="supprimerLogementId">
</form>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<script>
    function changerStatut(logementId, statut) {
        const statutLabel = statut === 'DISPONIBLE' ? 'activer' : 'désactiver';
        if (confirm('Êtes-vous sûr de vouloir ' + statutLabel + ' ce logement ?')) {
            document.getElementById('statutLogementId').value = logementId;
            document.getElementById('nouveauStatut').value = statut;
            document.getElementById('formStatut').action = '${pageContext.request.contextPath}/hote/logement/' + logementId + '/statut';
            document.getElementById('formStatut').submit();
        }
    }

    function supprimerLogement(logementId) {
        if (confirm('Êtes-vous sûr de vouloir supprimer ce logement ?\nCette action est irréversible.')) {
            document.getElementById('supprimerLogementId').value = logementId;
            document.getElementById('formSupprimer').action = '${pageContext.request.contextPath}/hote/logement/' + logementId + '/supprimer';
            document.getElementById('formSupprimer').submit();
        }
    }
</script>