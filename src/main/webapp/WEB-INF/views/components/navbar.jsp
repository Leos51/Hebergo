<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg navbar-squatrbnb">
    <div class="container">
        <!-- Logo -->
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">
            <i class="fas fa-home me-2"></i>Squat'R BNB
        </a>
        
        <!-- Toggle mobile -->
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <!-- Navigation -->
        <div class="collapse navbar-collapse" id="navbarMain">
            
            <!-- Centre - Recherche rapide (optionnel) -->
<%--            <div class="mx-auto d-none d-lg-block">--%>
<%--                <c:if test="${param.showSearch == 'true'}">--%>
<%--                    <form action="${pageContext.request.contextPath}/recherche" method="get" class="d-flex align-items-center border rounded-pill px-3 py-1">--%>
<%--                        <input type="text" name="ville" placeholder="Où allez-vous ?" class="border-0 form-control-sm" style="width: 150px;">--%>
<%--                        <span class="text-muted mx-2">|</span>--%>
<%--                        <input type="date" name="dateDebut" class="border-0 form-control-sm" style="width: 130px;">--%>
<%--                        <span class="text-muted mx-2">|</span>--%>
<%--                        <input type="date" name="dateFin" class="border-0 form-control-sm" style="width: 130px;">--%>
<%--                        <button type="submit" class="btn btn-sm btn-danger rounded-circle ms-2">--%>
<%--                            <i class="fas fa-search"></i>--%>
<%--                        </button>--%>
<%--                    </form>--%>
<%--                </c:if>--%>
<%--            </div>--%>
            
            <!-- Droite -->
            <ul class="navbar-nav ms-auto align-items-center">
                
                <!-- Devenir hôte -->
                <c:if test="${empty sessionScope.utilisateur || !sessionScope.utilisateur.hasRole('HOTE')}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/devenir-hote">
                            Devenir hote
                        </a>
                    </li>
                </c:if>
                
                <!-- Non connecté -->
                <c:if test="${empty sessionScope.utilisateur}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/auth/login">
                            Connexion
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-outline-dark rounded-pill px-3" href="${pageContext.request.contextPath}/auth/register">
                            Inscription
                        </a>
                    </li>
                </c:if>
                
                <!-- Connecté -->
                <c:if test="${not empty sessionScope.utilisateur}">
                    <!-- Messages -->
<%--                    <li class="nav-item">--%>
<%--                        <a class="nav-link position-relative" href="${pageContext.request.contextPath}/messages">--%>
<%--                            <i class="far fa-comment-dots fa-lg"></i>--%>
<%--                            <c:if test="${sessionScope.nbMessagesNonLus > 0}">--%>
<%--                                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">--%>
<%--                                    ${sessionScope.nbMessagesNonLus}--%>
<%--                                </span>--%>
<%--                            </c:if>--%>
<%--                        </a>--%>
<%--                    </li>--%>
                    
                    <!-- Notifications -->
<%--                    <li class="nav-item">--%>
<%--                        <a class="nav-link position-relative" href="${pageContext.request.contextPath}/notifications">--%>
<%--                            <i class="far fa-bell fa-lg"></i>--%>
<%--                            <c:if test="${sessionScope.nbNotifications > 0}">--%>
<%--                                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">--%>
<%--                                    ${sessionScope.nbNotifications}--%>
<%--                                </span>--%>
<%--                            </c:if>--%>
<%--                        </a>--%>
<%--                    </li>--%>
                    
                    <!-- Dropdown utilisateur -->
                    <li class="nav-item dropdown ms-2">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" data-bs-toggle="dropdown">
                            <c:choose>
                                <c:when test="${not empty sessionScope.utilisateur.photoUrl}">
                                    <img src="${sessionScope.utilisateur.photoUrl}" alt="Photo" class="avatar-sm me-2">
                                </c:when>
                                <c:otherwise>
                                    <div class="avatar-sm bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center me-2">
                                        ${sessionScope.utilisateur.initiales}
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            ${sessionScope.utilisateur.prenom}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <!-- Si Hôte -->
                            <c:if test="${sessionScope.utilisateur.hasRole('HOTE')}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/hote/dashboard">
                                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard Hôte
                                </a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/hote/mes-biens">
                                    <i class="fas fa-home me-2"></i>Mes biens
                                </a></li>
                                <li><hr class="dropdown-divider"></li>
                            </c:if>
                            
                            <!-- Si Locataire -->
                            <c:if test="${sessionScope.utilisateur.hasRole('LOCATAIRE')}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/locataire/dashboard">
                                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard Locataire
                                </a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/locataire/mes-reservations">
                                    <i class="fas fa-calendar-check me-2"></i>Mes réservations
                                </a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/locataire/favoris">
                                    <i class="fas fa-heart me-2"></i>Favoris
                                </a></li>
                                <li><hr class="dropdown-divider"></li>
                            </c:if>
                            
                            <!-- Commun -->
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/compte/profil">
                                <i class="fas fa-user me-2"></i>Mon profil
                            </a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/compte/parametres">
                                <i class="fas fa-cog me-2"></i>Paramètres
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">
                                <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
                            </a></li>
                        </ul>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>

<%--<nav class="navbar navbar-expand-lg navbar-dark bg-dark">--%>
<%--    <div class="container-fluid">--%>

<%--        <a class="navbar-brand" href="/home">MonSite</a>--%>

<%--        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">--%>
<%--            <span class="navbar-toggler-icon"></span>--%>
<%--        </button>--%>

<%--        <div class="collapse navbar-collapse" id="navbarNav">--%>
<%--            <ul class="navbar-nav">--%>

<%--                <!-- Accueil -->--%>
<%--                <li class="nav-item">--%>
<%--                    <a class="nav-link ${activePage == 'home' ? 'active' : ''}"--%>
<%--                       href="/home"--%>
<%--                       aria-current="${activePage == 'home' ? 'page' : ''}">--%>
<%--                        Accueil--%>
<%--                    </a>--%>
<%--                </li>--%>

<%--                <!-- Contact -->--%>
<%--                <li class="nav-item">--%>
<%--                    <a class="nav-link ${activePage == 'contact' ? 'active' : ''}"--%>
<%--                       href="/contact"--%>
<%--                       aria-current="${activePage == 'contact' ? 'page' : ''}">--%>
<%--                        Contact--%>
<%--                    </a>--%>
<%--                </li>--%>
<%--                <a href="${pageContext.request.contextPath}/auth/login">Connectez-vous</a>--%>
<%--                <li class="nav-item">--%>
<%--                    <a class="nav-link ${activePage == 'Connexion' ? 'active' : ''}"--%>
<%--                       href="${pageContext.request.contextPath}/auth/login"--%>
<%--                       aria-current="${activePage == 'login' ? 'page' : ''}">--%>
<%--                        Connexion--%>
<%--                    </a>--%>
<%--                </li>--%>
<%--                <li class="nav-item">--%>
<%--                    <a class="nav-link ${activePage == 'Inscription' ? 'active' : ''}"--%>
<%--                       href="${pageContext.request.contextPath}/auth/register"--%>
<%--                       aria-current="${activePage == 'register' ? 'page' : ''}">--%>
<%--                        Inscription--%>
<%--                    </a>--%>
<%--                </li>--%>
<%--            </ul>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</nav>--%>