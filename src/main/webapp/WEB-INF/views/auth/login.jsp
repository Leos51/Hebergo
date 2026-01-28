

<%--<!-- Formulaire d'inscription -->--%>
<%--<div class="registration-container">--%>
<%--  <div class="registration-card">--%>
<%--    <div class="registration-header">--%>
<%--      <h2><i class="fas fa-user-plus me-2"></i>Inscription</h2>--%>
<%--      <p>Rejoignez notre communauté</p>--%>
<%--    </div>--%>

<%--    <div class="registration-body">--%>
<%--      <!-- Messages d'erreur/succès -->--%>
<%--      <c:if test="${not empty erreur}">--%>
<%--        <div class="alert alert-danger" role="alert">--%>
<%--          <i class="fas fa-exclamation-circle me-2"></i>${erreur}--%>
<%--        </div>--%>
<%--      </c:if>--%>
<%--      <c:if test="${not empty message}">--%>
<%--        <div class="alert alert-success" role="alert">--%>
<%--          <i class="fas fa-check-circle me-2"></i>${message}--%>
<%--        </div>--%>
<%--      </c:if>--%>

<%--      <form action="${pageContext.request.contextPath}/auth/login" method="post" id="inscriptionForm">--%>

<%--        <!-- Informations personnelles -->--%>
<%--        <div class="form-section">--%>
<%--          <h3 class="form-section-title">--%>
<%--            <i class="fas fa-user"></i>Informations personnelle--%>
<%--          </h3>--%>



<%--          <div class="form-floating">--%>
<%--            <input type="email" class="form-control" id="email" name="email"--%>
<%--                   value="${email}" required placeholder="Email">--%>
<%--            <label for="email" class="required-field">Adresse email</label>--%>
<%--          </div>--%>


<%--        </div>--%>

<%--          <div class="form-floating">--%>
<%--            <input type="password" class="form-control" id="motDePasse" name="motDePasse"--%>
<%--                   required placeholder="Mot de passe">--%>
<%--            <label for="motDePasse" class="required-field">Mot de passe</label>--%>
<%--          </div>--%>

<%--        </div>--%>

<%--        <!-- Bouton de soumission -->--%>
<%--        <button type="submit" class="btn btn-inscription">--%>
<%--          <i class="fas fa-user-plus me-2"></i>Se connecter--%>
<%--        </button>--%>
<%--      </form>--%>

<%--      <!-- Lien connexion -->--%>
<%--      <div class="login-link">--%>
<%--        <p class="mb-0">Pas de compte?--%>
<%--          <a href="${pageContext.request.contextPath}/auth/register">Inscription</a>--%>
<%--        </p>--%>
<%--      </div>--%>
<%--    </div>--%>
<%--  </div>--%>
<%--</div>--%>
<%--<!-- Scripts -->--%>
<%--<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>--%>

<%--<%@ include file="../components/footer.jsp" %>--%>
<%--</body>--%>
<%--</html>--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/components/header.jsp">
  <jsp:param name="title" value="Connexion"/>
</jsp:include>


<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-md-5">
      <div class="card border-0 shadow">
        <div class="card-body p-5">
          <h2 class="text-center mb-4">Connexion</h2>

          <!-- Message de succès -->
          <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              <i class="fas fa-check-circle me-2"></i>${success}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Message d'erreur -->
          <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <i class="fas fa-exclamation-circle me-2"></i>${error}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Message déconnexion -->
          <c:if test="${param.logout == '1'}">
            <div class="alert alert-info alert-dismissible fade show" role="alert">
              <i class="fas fa-info-circle me-2"></i>Vous avez été déconnecté.
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <!-- Email -->
            <div class="mb-3">
              <label for="email" class="form-label-squatrbnb">Email</label>
              <input type="email" class="form-control form-control-squatrbnb" id="email"
                     name="email" value="${email}" required autofocus
                     placeholder="votre@email.com">
            </div>

            <!-- Mot de passe -->
            <div class="mb-3">
              <label for="motDePasse" class="form-label-squatrbnb">Mot de passe</label>
              <div class="input-group">
                <input type="password" class="form-control form-control-squatrbnb" id="motDePasse"
                       name="motDePasse" required placeholder="••••••••">
                <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                  <i class="fas fa-eye"></i>
                </button>
              </div>
            </div>

            <!-- Se souvenir de moi -->
            <div class="mb-3 form-check">
              <input type="checkbox" class="form-check-input" id="remember" name="remember">
              <label class="form-check-label" for="remember">Se souvenir de moi</label>
            </div>

            <!-- Bouton connexion -->
            <button type="submit" class="btn btn-primary-squatrbnb w-100 mb-3">
              Se connecter
            </button>

            <!-- Mot de passe oublié -->
            <div class="text-center mb-4">
              <a href="#" class="text-muted small">
                Mot de passe oublié ?
              </a>
            </div>

            <hr>

            <!-- Lien inscription -->
            <p class="text-center mb-0">
              Pas encore de compte ?
              <a href="${pageContext.request.contextPath}/auth/register" class="text-primary-squatrbnb fw-medium">
                S'inscrire
              </a>
            </p>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<script>
  // Toggle password visibility
  document.getElementById('togglePassword').addEventListener('click', function() {
    const passwordInput = document.getElementById('motDePasse');
    const icon = this.querySelector('i');

    if (passwordInput.type === 'password') {
      passwordInput.type = 'text';
      icon.classList.remove('fa-eye');
      icon.classList.add('fa-eye-slash');
    } else {
      passwordInput.type = 'password';
      icon.classList.remove('fa-eye-slash');
      icon.classList.add('fa-eye');
    }
  });
</script>