<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="title" value="Inscription"/>
</jsp:include>


<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card border-0 shadow">
                <div class="card-body p-5">
                    <h2 class="text-center mb-2">Créer un compte</h2>
                    <p class="text-center text-muted mb-4">Rejoignez Squat'R gratuitement</p>
                    
                    <!-- Message d'erreur -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/auth/register" method="post">
                        
                        <!-- Choix du rôle -->
                        <div class="mb-4">
                            <label class="form-label-hebergo d-block mb-3">Je souhaite :</label>
                            <div class="row g-3">
                                <div class="col-6">
                                    <input type="radio" class="btn-check" name="role" id="roleLocataire" 
                                           value="LOCATAIRE" ${empty param.role || param.role == 'LOCATAIRE' ? 'checked' : ''}>
                                    <label class="btn btn-outline-secondary w-100 py-3" for="roleLocataire">
                                        <i class="fas fa-search d-block mb-2 fa-2x"></i>
                                        <span class="fw-medium">Louer un logement</span>
                                        <small class="d-block text-muted">Je cherche une location</small>
                                    </label>
                                </div>
                                <div class="col-6">
                                    <input type="radio" class="btn-check" name="role" id="roleHote" 
                                           value="HOTE" ${param.role == 'HOTE' ? 'checked' : ''}>
                                    <label class="btn btn-outline-secondary w-100 py-3" for="roleHote">
                                        <i class="fas fa-home d-block mb-2 fa-2x"></i>
                                        <span class="fw-medium">Proposer un logement</span>
                                        <small class="d-block text-muted">Je mets mon bien en location</small>
                                    </label>
                                </div>
                            </div>
                        </div>
                        
                        <hr class="my-4">
                        
                        <!-- Nom et Prénom -->
                        <div class="row mb-3">
                            <div class="col-6">
                                <label for="prenom" class="form-label-hebergo">Prénom</label>
                                <input type="text" class="form-control form-control-hebergo" id="prenom" 
                                       name="prenom" value="${prenom}" required placeholder="Jean">
                            </div>
                            <div class="col-6">
                                <label for="nom" class="form-label-hebergo">Nom</label>
                                <input type="text" class="form-control form-control-hebergo" id="nom" 
                                       name="nom" value="${nom}" required placeholder="Dupont">
                            </div>
                        </div>
                        
                        <!-- Email -->
                        <div class="mb-3">
                            <label for="email" class="form-label-hebergo">Email</label>
                            <input type="email" class="form-control form-control-hebergo" id="email" 
                                   name="email" value="${email}" required placeholder="jean.dupont@email.com">
                        </div>
                        
                        <!-- Téléphone -->
                        <div class="mb-3">
                            <label for="telephone" class="form-label-hebergo">Téléphone <span class="text-muted">(optionnel)</span></label>
                            <input type="tel" class="form-control form-control-hebergo" id="telephone" 
                                   name="telephone" value="${telephone}" placeholder="06 12 34 56 78">
                        </div>
                        
                        <!-- Mot de passe -->
                        <div class="mb-3">
                            <label for="motDePasse" class="form-label-hebergo">Mot de passe</label>
                            <div class="input-group">
                                <input type="password" class="form-control form-control-hebergo" id="motDePasse" 
                                       name="motDePasse" required placeholder="Minimum 8 caractères"
                                       minlength="8">
                                <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <small class="text-muted">Au moins 8 caractère
                            </small>
                        </div>
                        
                        <!-- Confirmation mot de passe -->
                        <div class="mb-4">
                            <label for="confirmMotDePasse" class="form-label-hebergo">Confirmer le mot de passe</label>
                            <input type="password" class="form-control form-control-hebergo" id="confirmMotDePasse" 
                                   name="confirmMotDePasse" required placeholder="Retapez votre mot de passe">
                        </div>
                        
                        <!-- CGU -->
                        <div class="mb-4 form-check">
                            <input type="checkbox" class="form-check-input" id="cgu" name="cgu" required>
                            <label class="form-check-label small" for="cgu">
                                J'accepte les <a href="#" class="text-primary-hebergo">Conditions d'utilisation</a> 
                                et la <a href="#" class="text-primary-hebergo">Politique de confidentialité</a>
                            </label>
                        </div>
                        
                        <!-- Bouton inscription -->
                        <button type="submit" class="btn btn-primary-hebergo w-100 mb-3">
                            Créer mon compte
                        </button>
                        
                        <hr>
                        
                        <!-- Lien connexion -->
                        <p class="text-center mb-0">
                            Déjà un compte ? 
                            <a href="${pageContext.request.contextPath}/auth/login" class="text-primary-hebergo fw-medium">
                                Se connecter
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
    
    // Vérification mot de passe
    document.querySelector('form').addEventListener('submit', function(e) {
        const password = document.getElementById('motDePasse').value;
        const confirm = document.getElementById('confirmMotDePasse').value;
        
        if (password !== confirm) {
            e.preventDefault();
            alert('Les mots de passe ne correspondent pas');
        }
    });
</script>
