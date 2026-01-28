<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="sidebar-hote">

    <!-- En-tête sidebar -->
    <div class="px-4 py-3 border-bottom">
        <h5 class="mb-0">
            <a href="${pageContext.request.contextPath}/" class="text-decoration-none text-dark">
                <i class="fas fa-home me-2 text-primary-hebergo"></i>SquatRBnB
            </a>
        </h5>
        <p class="text-muted small mb-0">Espace hôte</p>
    </div>

    <!-- Navigation -->
    <nav class="nav flex-column py-3">

        <!-- Dashboard -->
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/dashboard">
            <i class="fas fa-tachometer-alt me-2"></i>
            Tableau de bord
        </a>

        <!-- Mes biens -->
        <a class="nav-link ${param.active == 'logements' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/mes-biens">
            <i class="fas fa-building me-2"></i>
            Mes logements
        </a>

        <!-- Réservations -->
        <a class="nav-link ${param.active == 'reservations' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/reservations">
            <i class="fas fa-calendar-check me-2"></i>
            Réservations
            <c:if test="${not empty nbReservationsEnAttente && nbReservationsEnAttente > 0}">
                <span class="badge bg-warning text-dark ms-2">${nbReservationsEnAttente}</span>
            </c:if>
        </a>

        <!-- Calendrier -->
        <a class="nav-link ${param.active == 'calendrier' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/calendrier">
            <i class="fas fa-calendar-alt me-2"></i>
            Calendrier
        </a>

        <!-- Messages -->
        <a class="nav-link ${param.active == 'messages' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/messages">
            <i class="fas fa-envelope me-2"></i>
            Messages
            <c:if test="${not empty nbMessagesNonLus && nbMessagesNonLus > 0}">
                <span class="badge bg-danger ms-2">${nbMessagesNonLus}</span>
            </c:if>
        </a>

        <!-- Avis -->
        <a class="nav-link ${param.active == 'avis' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/avis">
            <i class="fas fa-star me-2"></i>
            Avis
        </a>

        <!-- Séparateur -->
        <hr class="my-2">

        <!-- Finances -->
        <a class="nav-link ${param.active == 'finances' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/finances">
            <i class="fas fa-euro-sign me-2"></i>
            Finances
        </a>

        <!-- Statistiques -->
        <a class="nav-link ${param.active == 'stats' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/statistiques">
            <i class="fas fa-chart-line me-2"></i>
            Statistiques
        </a>

        <!-- Séparateur -->
        <hr class="my-2">

        <!-- Paramètres -->
        <a class="nav-link ${param.active == 'parametres' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/hote/parametres">
            <i class="fas fa-cog me-2"></i>
            Paramètres
        </a>

        <!-- Aide -->
        <a class="nav-link"
           href="${pageContext.request.contextPath}/aide-hote"
           target="_blank">
            <i class="fas fa-question-circle me-2"></i>
            Centre d'aide
        </a>

        <!-- Séparateur -->
        <hr class="my-2">

        <!-- Retour au site -->
        <a class="nav-link"
           href="${pageContext.request.contextPath}/">
            <i class="fas fa-arrow-left me-2"></i>
            Retour au site
        </a>

    </nav>

    <!-- Footer sidebar -->
    <div class="px-4 py-3 mt-auto border-top">
        <div class="d-flex align-items-center">
            <div class="user-avatar me-2" style="width: 36px; height: 36px; font-size: 0.9rem;">
                ${sessionScope.utilisateur.prenom.substring(0, 1)}${sessionScope.utilisateur.nom.substring(0, 1)}
            </div>
            <div class="flex-grow-1">
                <p class="mb-0 small fw-bold">${sessionScope.utilisateur.prenom} ${sessionScope.utilisateur.nom}</p>
                <p class="mb-0 text-muted" style="font-size: 0.75rem;">Hôte</p>
            </div>
            <a href="${pageContext.request.contextPath}/auth/logout"
               class="btn btn-sm btn-outline-danger"
               title="Déconnexion">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
    </div>

</div>










<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@ taglib prefix="c" uri="jakarta.tags.core" %>--%>

<%--<aside class="sidebar-dashboard">--%>
<%--    <!-- Marque -->--%>
<%--    <a href="${pageContext.request.contextPath}/" class="sidebar-brand text-decoration-none d-block">--%>
<%--        <i class="fas fa-home me-2"></i>Squat'R--%>
<%--    </a>--%>
<%--    --%>
<%--    <!-- Utilisateur infos -->--%>
<%--    <div class="sidebar-user">--%>
<%--        <div class="d-flex align-items-center">--%>
<%--            <c:choose>--%>
<%--                <c:when test="${not empty sessionScope.utilisateur.photoUrl}">--%>
<%--                    <img src="${sessionScope.utilisateur.photoUrl}" alt="Photo" class="avatar-md me-3">--%>

<%--                </c:when>--%>
<%--                <c:otherwise>--%>
<%--                    <div class="avatar-md bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3">--%>
<%--&lt;%&ndash;                        ${sessionScope.utilisateur.initiales}&ndash;%&gt;--%>
<%--                        <img src="${pageContext.request.contextPath}/assets/img/avatar/photo-avatar-profil.png" alt="Photo" class="avatar-md me-3">--%>

<%--                    </div>--%>
<%--                </c:otherwise>--%>
<%--            </c:choose>--%>
<%--            <div>--%>
<%--                <div class="user-name">${sessionScope.utilisateur.prenom} ${sessionScope.utilisateur.nom}</div>--%>
<%--                <div class="user-role">--%>
<%--                    <i class="fas fa-home me-1"></i>Hôte--%>
<%--                    <c:if test="${sessionScope.utilisateur.hasRole('HOTE') && sessionScope.utilisateur.profil.verifie}">--%>
<%--                        <i class="fas fa-check-circle text-success ms-1" title="Vérifié"></i>--%>
<%--                    </c:if>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--    --%>
<%--    <!-- Navigation -->--%>
<%--    <ul class="sidebar-nav">--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/dashboard" --%>
<%--               class="nav-link ${param.active == 'dashboard' ? 'active' : ''}">--%>
<%--                <i class="fas fa-tachometer-alt"></i>--%>
<%--                <span>Dashboard</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/mes-biens" --%>
<%--               class="nav-link ${param.active == 'logements' ? 'active' : ''}">--%>
<%--                <i class="fas fa-home"></i>--%>
<%--                <span>Mes biens</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/reservations" --%>
<%--               class="nav-link ${param.active == 'reservations' ? 'active' : ''}">--%>
<%--                <i class="fas fa-calendar-alt"></i>--%>
<%--                <span>Réservations</span>--%>
<%--                <c:if test="${sessionScope.nbReservationsEnAttente > 0}">--%>
<%--                    <span class="badge bg-danger ms-auto">${sessionScope.nbReservationsEnAttente}</span>--%>
<%--                </c:if>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--&lt;%&ndash;        <li class="nav-item">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${pageContext.request.contextPath}/hote/calendrier" &ndash;%&gt;--%>
<%--&lt;%&ndash;               class="nav-link ${param.active == 'calendrier' ? 'active' : ''}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <i class="fas fa-calendar"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                <span>Calendrier</span>&ndash;%&gt;--%>
<%--&lt;%&ndash;            </a>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </li>&ndash;%&gt;--%>
<%--&lt;%&ndash;        <li class="nav-item">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${pageContext.request.contextPath}/hote/messages" &ndash;%&gt;--%>
<%--&lt;%&ndash;               class="nav-link ${param.active == 'messages' ? 'active' : ''}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <i class="fas fa-envelope"></i>&ndash;%&gt;--%>
<%--&lt;%&ndash;                <span>Messages</span>&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:if test="${sessionScope.nbMessagesNonLus > 0}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <span class="badge bg-danger ms-auto">${sessionScope.nbMessagesNonLus}</span>&ndash;%&gt;--%>
<%--&lt;%&ndash;                </c:if>&ndash;%&gt;--%>
<%--&lt;%&ndash;            </a>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </li>&ndash;%&gt;--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/avis" --%>
<%--               class="nav-link ${param.active == 'avis' ? 'active' : ''}">--%>
<%--                <i class="fas fa-star"></i>--%>
<%--                <span>Avis</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/revenus" --%>
<%--               class="nav-link ${param.active == 'revenus' ? 'active' : ''}">--%>
<%--                <i class="fas fa-euro-sign"></i>--%>
<%--                <span>Revenus</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        --%>
<%--        <li class="nav-item mt-4 pt-3 border-top">--%>
<%--            <a href="${pageContext.request.contextPath}/compte/profil" --%>
<%--               class="nav-link ${param.active == 'profil' ? 'active' : ''}">--%>
<%--                <i class="fas fa-user"></i>--%>
<%--                <span>Mon profil</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/compte/parametres" --%>
<%--               class="nav-link ${param.active == 'parametres' ? 'active' : ''}">--%>
<%--                <i class="fas fa-cog"></i>--%>
<%--                <span>Paramètres</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        --%>
<%--        <!-- Si aussi locataire -->--%>
<%--        <c:if test="${sessionScope.utilisateur.hasRole('LOCATAIRE')}">--%>
<%--            <li class="nav-item mt-3 pt-3 border-top">--%>
<%--                <a href="${pageContext.request.contextPath}/locataire/dashboard" class="nav-link">--%>
<%--                    <i class="fas fa-exchange-alt"></i>--%>
<%--                    <span>Mode Locataire</span>--%>
<%--                </a>--%>
<%--            </li>--%>
<%--        </c:if>--%>
<%--        --%>
<%--        <li class="nav-item mt-3 pt-3 border-top">--%>
<%--            <a href="${pageContext.request.contextPath}/auth/logout" class="nav-link text-danger">--%>
<%--                <i class="fas fa-sign-out-alt"></i>--%>
<%--                <span>Déconnexion</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--    </ul>--%>
<%--</aside>--%>
