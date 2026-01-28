<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="sidebar-dashboard">
    <!-- Marque -->
    <a href="${pageContext.request.contextPath}/" class="sidebar-brand text-decoration-none d-block">
        <i class="fas fa-home me-2"></i>Squat'R
    </a>
    
    <!-- Utilisateur infos -->
    <div class="sidebar-user">
        <div class="d-flex align-items-center">
            <c:choose>
                <c:when test="${not empty sessionScope.utilisateur.photoUrl}">
                    <img src="${sessionScope.utilisateur.photoUrl}" alt="Photo" class="avatar-md me-3">

                </c:when>
                <c:otherwise>
                    <div class="avatar-md bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3">
<%--                        ${sessionScope.utilisateur.initiales}--%>
                        <img src="${pageContext.request.contextPath}/assets/img/avatar/photo-avatar-profil.png" alt="Photo" class="avatar-md me-3">

                    </div>
                </c:otherwise>
            </c:choose>
            <div>
                <div class="user-name">${sessionScope.utilisateur.prenom} ${sessionScope.utilisateur.nom}</div>
                <div class="user-role">
                    <i class="fas fa-home me-1"></i>Hôte
                    <c:if test="${sessionScope.utilisateur.hasRole('HOTE') && sessionScope.utilisateur.profil.verifie}">
                        <i class="fas fa-check-circle text-success ms-1" title="Vérifié"></i>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Navigation -->
    <ul class="sidebar-nav">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/hote/dashboard" 
               class="nav-link ${param.active == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-tachometer-alt"></i>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/hote/mes-biens" 
               class="nav-link ${param.active == 'logements' ? 'active' : ''}">
                <i class="fas fa-home"></i>
                <span>Mes biens</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/hote/reservations" 
               class="nav-link ${param.active == 'reservations' ? 'active' : ''}">
                <i class="fas fa-calendar-alt"></i>
                <span>Réservations</span>
                <c:if test="${sessionScope.nbReservationsEnAttente > 0}">
                    <span class="badge bg-danger ms-auto">${sessionScope.nbReservationsEnAttente}</span>
                </c:if>
            </a>
        </li>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/calendrier" --%>
<%--               class="nav-link ${param.active == 'calendrier' ? 'active' : ''}">--%>
<%--                <i class="fas fa-calendar"></i>--%>
<%--                <span>Calendrier</span>--%>
<%--            </a>--%>
<%--        </li>--%>
<%--        <li class="nav-item">--%>
<%--            <a href="${pageContext.request.contextPath}/hote/messages" --%>
<%--               class="nav-link ${param.active == 'messages' ? 'active' : ''}">--%>
<%--                <i class="fas fa-envelope"></i>--%>
<%--                <span>Messages</span>--%>
<%--                <c:if test="${sessionScope.nbMessagesNonLus > 0}">--%>
<%--                    <span class="badge bg-danger ms-auto">${sessionScope.nbMessagesNonLus}</span>--%>
<%--                </c:if>--%>
<%--            </a>--%>
<%--        </li>--%>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/hote/avis" 
               class="nav-link ${param.active == 'avis' ? 'active' : ''}">
                <i class="fas fa-star"></i>
                <span>Avis</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/hote/revenus" 
               class="nav-link ${param.active == 'revenus' ? 'active' : ''}">
                <i class="fas fa-euro-sign"></i>
                <span>Revenus</span>
            </a>
        </li>
        
        <li class="nav-item mt-4 pt-3 border-top">
            <a href="${pageContext.request.contextPath}/compte/profil" 
               class="nav-link ${param.active == 'profil' ? 'active' : ''}">
                <i class="fas fa-user"></i>
                <span>Mon profil</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/compte/parametres" 
               class="nav-link ${param.active == 'parametres' ? 'active' : ''}">
                <i class="fas fa-cog"></i>
                <span>Paramètres</span>
            </a>
        </li>
        
        <!-- Si aussi locataire -->
        <c:if test="${sessionScope.utilisateur.hasRole('LOCATAIRE')}">
            <li class="nav-item mt-3 pt-3 border-top">
                <a href="${pageContext.request.contextPath}/locataire/dashboard" class="nav-link">
                    <i class="fas fa-exchange-alt"></i>
                    <span>Mode Locataire</span>
                </a>
            </li>
        </c:if>
        
        <li class="nav-item mt-3 pt-3 border-top">
            <a href="${pageContext.request.contextPath}/auth/logout" class="nav-link text-danger">
                <i class="fas fa-sign-out-alt"></i>
                <span>Déconnexion</span>
            </a>
        </li>
    </ul>
</aside>
