<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<footer class="footer-hebergo">
  <div class="container">
    <div class="row">

      <!-- À propos -->
      <div class="col-md-3 mb-4">
        <h5>À propos</h5>
        <ul class="list-unstyled">
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/qui-sommes-nous">Qui sommes-nous</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/presse">Presse</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/carriere">Carrières</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/investisseurs">Investisseurs</a>
          </li>
        </ul>
      </div>

      <!-- Découvrir -->
      <div class="col-md-3 mb-4">
        <h5>Découvrir</h5>
        <ul class="list-unstyled">
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/logements">Tous les logements</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/destinations">Destinations</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/experiences">Expériences</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/blog">Blog voyage</a>
          </li>
        </ul>
      </div>

      <!-- Hébergement -->
      <div class="col-md-3 mb-4">
        <h5>Hébergement</h5>
        <ul class="list-unstyled">
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/devenir-hote">Devenir hôte</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/ressources-hote">Ressources</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/centre-communaute">Communauté</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/assurance-hote">Assurance hôte</a>
          </li>
        </ul>
      </div>

      <!-- Assistance -->
      <div class="col-md-3 mb-4">
        <h5>Assistance</h5>
        <ul class="list-unstyled">
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/aide">Centre d'aide</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/securite">Sécurité</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/conditions">Conditions</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/confidentialite">Confidentialité</a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/cookies">Cookies</a>
          </li>
        </ul>
      </div>

    </div>

    <!-- Ligne de séparation -->
    <div class="footer-bottom">
      <div class="row align-items-center">

        <!-- Copyright -->
        <div class="col-md-6 text-center text-md-start mb-3 mb-md-0">
          <p class="mb-0">
            &copy; <span id="currentYear"></span> SquatRBnB, Inc. Tous droits réservés.
          </p>
          <p class="mb-0 small">
            <a href="${pageContext.request.contextPath}/conditions" class="me-3">Conditions</a>
            <a href="${pageContext.request.contextPath}/confidentialite" class="me-3">Confidentialité</a>
            <a href="${pageContext.request.contextPath}/plan-site">Plan du site</a>
          </p>
        </div>

        <!-- Réseaux sociaux et langue -->
        <div class="col-md-6 text-center text-md-end">

          <!-- Langue et devise -->
          <div class="d-inline-block me-3">
            <button class="btn btn-sm btn-outline-light" type="button">
              <i class="fas fa-globe me-1"></i>Français (FR)
            </button>
            <button class="btn btn-sm btn-outline-light ms-2" type="button">
              <i class="fas fa-euro-sign me-1"></i>EUR
            </button>
          </div>

          <!-- Réseaux sociaux -->
          <div class="social-links d-inline-block">
            <a href="https://facebook.com" target="_blank" title="Facebook">
              <i class="fab fa-facebook-f"></i>
            </a>
            <a href="https://twitter.com" target="_blank" title="Twitter">
              <i class="fab fa-twitter"></i>
            </a>
            <a href="https://instagram.com" target="_blank" title="Instagram">
              <i class="fab fa-instagram"></i>
            </a>
            <a href="https://linkedin.com" target="_blank" title="LinkedIn">
              <i class="fab fa-linkedin-in"></i>
            </a>
          </div>
        </div>

      </div>
    </div>

  </div>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Leaflet (si carte) -->
<c:if test="${param.useMap == 'true'}">
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
</c:if>

<!-- JavaScript personnalisé -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

<!-- Script additionnel si spécifié -->
<c:if test="${not empty param.additionalJS}">
  <script src="${pageContext.request.contextPath}/assets/js/${param.additionalJS}"></script>
</c:if>

<script>
  // Année courante pour le copyright
  document.getElementById('currentYear').textContent = new Date().getFullYear();
</script>

</body>
</html>
