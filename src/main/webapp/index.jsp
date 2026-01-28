<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- Header -->
<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="title" value="SquatRBnB - Trouvez votre squat idéal"/>
</jsp:include>

<!-- Navbar -->
<jsp:include page="/WEB-INF/views/components/navbar.jsp">
    <jsp:param name="active" value=""/>
</jsp:include>

<!-- Hero Section -->
<section class="hero-section">
    <div class="hero-content">
        <h1 class="hero-title">Trouvez votre squat idéal</h1>
        <p class="hero-subtitle">Des logements uniques à travers le monde</p>

        <!-- Barre de recherche -->
        <div class="search-card">
            <form action="${pageContext.request.contextPath}/logements/recherche" method="get" class="row g-0 align-items-center">
                <div class="col-md-4">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-0">
                            <i class="fas fa-map-marker-alt text-muted"></i>
                        </span>
                        <input type="text"
                               class="form-control"
                               name="destination"
                               placeholder="Où allez-vous ?"
                               value="${param.destination}">
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-0">
                            <i class="fas fa-calendar text-muted"></i>
                        </span>
                        <input type="date"
                               class="form-control"
                               name="dateArrivee"
                               placeholder="Arrivée"
                               value="${param.dateArrivee}">
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-0">
                            <i class="fas fa-calendar text-muted"></i>
                        </span>
                        <input type="date"
                               class="form-control"
                               name="dateDepart"
                               placeholder="Départ"
                               value="${param.dateDepart}">
                    </div>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-search w-100">
                        <i class="fas fa-search me-2"></i>Rechercher
                    </button>
                </div>
            </form>
        </div>
    </div>
</section>

<!-- Section Features -->
<section class="section-padding" style="background: #f8f9fa;">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Pourquoi choisir SquatRBnB ?</h2>
            <p class="section-subtitle">Votre plateforme de confiance pour trouver des logements uniques</p>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <h3 class="h5 mb-3">Paiements sécurisés</h3>
                    <p class="text-muted">
                        Vos transactions sont protégées avec notre système de paiement sécurisé.
                        Réservez en toute confiance.
                    </p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-star"></i>
                    </div>
                    <h3 class="h5 mb-3">Avis vérifiés</h3>
                    <p class="text-muted">
                        Consultez les avis authentiques de voyageurs pour faire le meilleur choix
                        pour votre séjour.
                    </p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-headset"></i>
                    </div>
                    <h3 class="h5 mb-3">Support 24/7</h3>
                    <p class="text-muted">
                        Notre équipe est disponible à tout moment pour vous aider.
                        Assistance en français.
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Section Destinations populaires -->
<section class="section-padding">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Destinations populaires</h2>
            <p class="section-subtitle">Découvrez les villes les plus prisées</p>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/logements/recherche?destination=Paris"
                   class="text-decoration-none">
                    <div class="destination-card">
                        <img src="https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=1173&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Paris">
                        <div class="destination-overlay">
                            <div class="destination-title">Paris</div>
                            <div class="text-light">Plus de 1 logements</div>
                        </div>
                    </div>
                </a>
            </div>

            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/logements/recherche?destination=Lyon"
                   class="text-decoration-none">
                    <div class="destination-card">
                        <img src="https://plus.unsplash.com/premium_photo-1742418148669-85fde84c4431?q=80&w=1171&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Lyon">
                        <div class="destination-overlay">
                            <div class="destination-title">Lyon</div>
                            <div class="text-light">Plus de 1 logements</div>
                        </div>
                    </div>
                </a>
            </div>

            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/logements/recherche?destination=Marseille"
                   class="text-decoration-none">
                    <div class="destination-card">
                        <img src="https://images.unsplash.com/photo-1566838217578-1903568a76d9?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Marseille">
                        <div class="destination-overlay">
                            <div class="destination-title">Marseille</div>
                            <div class="text-light">Plus de 1 logements</div>
                        </div>
                    </div>
                </a>
            </div>
        </div>
    </div>
</section>

<!-- Section Logements populaires -->
<section class="section-padding" style="background: #f8f9fa;">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Logements populaires</h2>
            <p class="section-subtitle">Les mieux notés par nos voyageurs</p>
        </div>

        <div class="row g-4">
            <c:choose>
                <c:when test="${not empty logementsPopulaires}">
                    <c:forEach var="logement" items="${logementsPopulaires}" end="5">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/logement?id=${logement.id}"
                               class="text-decoration-none">
                                <div class="logement-card">
                                    <div class="logement-image">
                                        <c:choose>
                                            <c:when test="${not empty logement.photos && logement.photos.size() > 0}">
                                                <img src="${pageContext.request.contextPath}${logement.photos[0].url}"
                                                     alt="${logement.titre}">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="https://via.placeholder.com/400x240?text=Photo+bient%C3%B4t+disponible"
                                                     alt="${logement.titre}">
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${logement.noteMoyenne != null && logement.noteMoyenne > 4.5}">
                                            <div class="logement-badge">
                                                <i class="fas fa-star me-1"></i>Coup de cœur
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="logement-body">
                                        <div class="logement-location">
                                            <i class="fas fa-map-marker-alt me-1"></i>
                                            <c:if test="${not empty logement.adresse && not empty logement.adresse.ville}">
                                                ${logement.adresse.ville}
                                            </c:if>
                                        </div>
                                        <h3 class="logement-title">${logement.titre}</h3>
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div class="logement-price">
                                                <fmt:formatNumber value="${logement.prixNuit}" type="number" maxFractionDigits="0"/>€
                                                <span class="text-muted small">/ nuit</span>
                                            </div>
                                            <c:if test="${logement.noteMoyenne != null && logement.noteMoyenne > 0}">
                                                <div class="logement-rating">
                                                    <i class="fas fa-star text-warning"></i>
                                                    <strong><fmt:formatNumber value="${logement.noteMoyenne}" maxFractionDigits="1"/></strong>
                                                    <span>(${logement.nbAvis})</span>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <!-- Placeholder cards si pas de données -->
                    <c:forEach begin="1" end="6">
                        <div class="col-md-4">
                            <div class="logement-card">
                                <div class="logement-image">
                                    <img src="https://via.placeholder.com/400x240?text=Logement+disponible+bient%C3%B4t"
                                         alt="Logement">
                                </div>
                                <div class="logement-body">
                                    <div class="logement-location">
                                        <i class="fas fa-map-marker-alt me-1"></i>France
                                    </div>
                                    <h3 class="logement-title">Découvrez nos logements</h3>
                                    <div class="logement-price">
                                        À partir de 50€
                                        <span class="text-muted small">/ nuit</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="text-center mt-5">
            <a href="${pageContext.request.contextPath}/logements" class="btn btn-outline-primary btn-lg px-5">
                Voir tous les logements
                <i class="fas fa-arrow-right ms-2"></i>
            </a>
        </div>
    </div>
</section>

<!-- Section Comment ça marche -->
<section class="section-padding">
    <div class="container">
        <div class="section-header">
            <h2 class="section-title">Comment ça marche ?</h2>
            <p class="section-subtitle">Réservez en 3 étapes simples</p>
        </div>

        <div class="row">
            <div class="col-md-4">
                <div class="step-card">
                    <div class="step-number">1</div>
                    <h3 class="h5 mb-3">Recherchez</h3>
                    <p class="text-muted">
                        Parcourez notre sélection de logements uniques et trouvez celui qui vous correspond.
                    </p>
                    <div class="step-arrow d-none d-md-block">
                        <i class="fas fa-arrow-right"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="step-card">
                    <div class="step-number">2</div>
                    <h3 class="h5 mb-3">Réservez</h3>
                    <p class="text-muted">
                        Sélectionnez vos dates, validez votre réservation en toute sécurité.
                    </p>
                    <div class="step-arrow d-none d-md-block">
                        <i class="fas fa-arrow-right"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="step-card">
                    <div class="step-number">3</div>
                    <h3 class="h5 mb-3">Profitez</h3>
                    <p class="text-muted">
                        Recevez les coordonnées de votre hôte et profitez de votre séjour !
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Section CTA (Devenir hôte) -->
<section class="section-padding" style="background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)); color: white;">
    <div class="container text-center">
        <h2 class="display-5 fw-bold mb-3">Devenez hôte sur SquatRBnB</h2>
        <p class="fs-5 mb-4">
            Partagez votre espace et gagnez un revenu complémentaire
        </p>
        <a href="${pageContext.request.contextPath}/devenir-hote"
           class="btn btn-light btn-lg px-5"
           style="color: var(--primary-color); font-weight: 600;">
            Commencer maintenant
            <i class="fas fa-arrow-right ms-2"></i>
        </a>
    </div>
</section>

<!-- Footer -->
<jsp:include page="/WEB-INF/views/components/footer.jsp"/>
