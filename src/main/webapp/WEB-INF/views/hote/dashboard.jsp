<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="title" value="Dashboard Hôte"/>
</jsp:include>

<jsp:include page="/WEB-INF/views/components/sidebar-hote.jsp">
    <jsp:param name="active" value="dashboard"/>
</jsp:include>

<main class="main-content-dashboard">
<%--
--%>
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 mb-1">Bonjour ${sessionScope.utilisateur.prenom} 👋</h1>
            <p class="text-muted mb-0">Voici un résumé de votre activité</p>
        </div>
        <a href="${pageContext.request.contextPath}/hote/bien/nouveau" class="btn btn-primary-hebergo">
            <i class="fas fa-plus me-2"></i>Ajouter un bien
        </a>
    </div>
    
    <!-- Message flash -->
<%--    <c:if test="${not empty sessionScope.flash}">--%>
<%--        <div class="alert alert-success alert-dismissible fade show" role="alert">--%>
<%--            <i class="fas fa-check-circle me-2"></i>${sessionScope.flash}--%>
<%--            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>--%>
<%--        </div>--%>
<%--        <c:remove var="flash" scope="session"/>--%>
<%--    </c:if>--%>


    <!-- Stats -->
    <div class="row g-4 mb-4">
        <div class="col-md-3 col-sm-6">
            <div class="stat-card">
                <div class="stat-icon bg-primary">
                    <i class="fas fa-home"></i>
                </div>

                <div class="stat-value">${stats.nbLogements != null ? stats.nbLogements : 0}</div>
                <div class="stat-label">Logements publiés</div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6">
            <div class="stat-card">
                <div class="stat-icon bg-secondary">
                    <i class="fas fa-calendar-check"></i>
                </div>
                <div class="stat-value">${stats.reservationsAVenir != null ? stats.reservationsAVenir : 0}</div>
                <div class="stat-label">Réservations</div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6">
            <div class="stat-card">
                <div class="stat-icon bg-primary">
                    <i class="fas fa-euro-sign"></i>
                </div>
                <div class="stat-value">
                    <fmt:formatNumber value="${stats.revenusTotaux != null ? stats.revenusTotaux : 0}" type="currency" currencySymbol="€" maxFractionDigits="0"/>
                </div>
                <div class="stat-label">Revenus totaux</div>
            </div>
        </div>
        <div class="col-md-3 col-sm-6">
            <div class="stat-card">
                <div class="stat-icon bg-info">
                    <i class="fas fa-star"></i>
                </div>
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${stats.noteMoyenne != null && stats.noteMoyenne > 0}">
                            <fmt:formatNumber value="${stats.noteMoyenne}" maxFractionDigits="1"/>
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </div>
                <div class="stat-label">Note moyenne</div>
            </div>
        </div>
    </div>
    
    <div class="row g-4">
        <!-- Réservations en attente -->
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white border-0 py-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Réservations récentes</h5>
                        <a href="${pageContext.request.contextPath}/hote/reservations" class="btn btn-sm btn-outline-secondary">
                            Voir tout
                        </a>
                    </div>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty reservationsRecentes}">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Locataire</th>
                                            <th>Bien</th>
                                            <th>Dates</th>
                                            <th>Montant</th>
                                            <th>Statut</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="resa" items="${reservationsRecentes}">
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center">
                                                        <div class="avatar-sm bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center me-2">
                                                            ${resa.locataire.initiales}
                                                        </div>
                                                        ${resa.locataire.prenom} ${resa.locataire.nom.charAt(0)}.
                                                    </div>
                                                </td>
                                                <td>${resa.bien.titre}</td>
                                                <td>
                                                    <fmt:formatDate value="${resa.dateDebut}" pattern="dd/MM"/> - 
                                                    <fmt:formatDate value="${resa.dateFin}" pattern="dd/MM"/>
                                                </td>
                                                <td><strong>${resa.prixTotal}€</strong></td>
                                                <td>
                                                    <span class="badge badge-statut badge-${resa.statut.cssClass}">
                                                        ${resa.statut.libelle}
                                                    </span>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/hote/reservation/${resa.id}" 
                                                       class="btn btn-sm btn-outline-primary">
                                                        Voir
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5">
                                <i class="fas fa-calendar-alt fa-3x text-muted mb-3"></i>
                                <p class="text-muted">Aucune réservation pour le moment</p>
                                <c:if test="${stats.nbBiens == 0}">
                                    <a href="${pageContext.request.contextPath}/hote/bien/nouveau" class="btn btn-primary-hebergo">
                                        Publier votre premier bien
                                    </a>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <!-- Actions rapides et Messages -->
        <div class="col-lg-4">
            <!-- Actions rapides -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white border-0 py-3">
                    <h5 class="mb-0">Actions rapides</h5>
                </div>
                <div class="card-body">
                    <a href="${pageContext.request.contextPath}/hote/bien/nouveau" class="btn btn-outline-secondary w-100 mb-2 text-start">
                        <i class="fas fa-plus me-2"></i>Ajouter un bien
                    </a>
                    <a href="${pageContext.request.contextPath}/hote/calendrier" class="btn btn-outline-secondary w-100 mb-2 text-start">
                        <i class="fas fa-calendar me-2"></i>Gérer le calendrier
                    </a>
                    <a href="${pageContext.request.contextPath}/hote/tarifs" class="btn btn-outline-secondary w-100 mb-2 text-start">
                        <i class="fas fa-euro-sign me-2"></i>Modifier les tarifs
                    </a>
                    <a href="${pageContext.request.contextPath}/compte/profil" class="btn btn-outline-secondary w-100 text-start">
                        <i class="fas fa-user me-2"></i>Modifier mon profil
                    </a>
                </div>
            </div>


        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>
