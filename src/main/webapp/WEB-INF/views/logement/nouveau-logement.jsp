<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/components/header.jsp">
  <jsp:param name="title" value="Ajouter un logement"/>
</jsp:include>

<jsp:include page="/WEB-INF/views/components/sidebar-hote.jsp">
  <jsp:param name="active" value="logements"/>
</jsp:include>

<main class="main-content-dashboard">
  <!-- Header -->
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb mb-1">
          <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/hote/logements">Mes logements</a>
          </li>
          <li class="breadcrumb-item active">Nouveau logement</li>
        </ol>
      </nav>
      <h1 class="h3 mb-0">Ajouter un logement</h1>
    </div>
  </div>

  <!-- Message d'erreur -->
  <c:if test="${not empty erreur}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="fas fa-exclamation-circle me-2"></i>${erreur}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <!-- Formulaire -->
  <form action="${pageContext.request.contextPath}/hote/logement/nouveau" method="post"
        enctype="multipart/form-data" id="formLogement" class="needs-validation" novalidate>

    <!-- Étape 1: Informations générales -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-info-circle text-primary me-2"></i>Informations générales
        </h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <!-- Titre -->
          <div class="col-12">
            <label for="titre" class="form-label">Titre de l'annonce <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="titre" name="titre"
                   value="${logement.titre}" required maxlength="200"
                   placeholder="Ex: Charmant appartement au cœur de Paris">
            <div class="form-text">Un titre accrocheur attire plus de visiteurs (max 200 caractères)</div>
            <div class="invalid-feedback">Le titre est obligatoire</div>
          </div>

          <!-- Type de logement -->
          <div class="col-md-6">
            <label for="typeLogementId" class="form-label">Type de logement <span class="text-danger">*</span></label>
            <select class="form-select" id="typeLogementId" name="typeLogementId" required>
              <option value="">Sélectionnez un type</option>
              <c:forEach var="type" items="${types}">
                <option value="${type.id}" ${logement.typeLogementId == type.id ? 'selected' : ''}>
                    ${type.nom}
                </option>
              </c:forEach>
            </select>
            <div class="invalid-feedback">Veuillez sélectionner un type</div>
          </div>

          <!-- Superficie -->
          <div class="col-md-6">
            <label for="superficie" class="form-label">Superficie (m²)</label>
            <input type="number" class="form-control" id="superficie" name="superficie"
                   value="${logement.superficie}" min="1" max="10000" step="0.5"
                   placeholder="Ex: 45">
          </div>

          <!-- Description -->
          <div class="col-12">
            <label for="description" class="form-label">Description <span class="text-danger">*</span></label>
            <textarea class="form-control" id="description" name="description"
                      rows="5" required maxlength="5000"
                      placeholder="Décrivez votre logement, son ambiance, ses points forts...">${logement.description}</textarea>
            <div class="form-text">
              <span id="descriptionCount">0</span>/5000 caractères
            </div>
            <div class="invalid-feedback">La description est obligatoire</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Étape 2: Capacité -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-users text-primary me-2"></i>Capacité
        </h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3 col-6">
            <label for="nbChambres" class="form-label">Chambres <span class="text-danger">*</span></label>
            <div class="input-group">
              <button type="button" class="btn btn-outline-secondary" onclick="decrement('nbChambres')">
                <i class="fas fa-minus"></i>
              </button>
              <input type="number" class="form-control text-center" id="nbChambres" name="nbChambres"
                     value="${logement.nbChambres != null ? logement.nbChambres : 1}" min="0" max="50" required>
              <button type="button" class="btn btn-outline-secondary" onclick="increment('nbChambres')">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>

          <div class="col-md-3 col-6">
            <label for="nbLits" class="form-label">Lits <span class="text-danger">*</span></label>
            <div class="input-group">
              <button type="button" class="btn btn-outline-secondary" onclick="decrement('nbLits')">
                <i class="fas fa-minus"></i>
              </button>
              <input type="number" class="form-control text-center" id="nbLits" name="nbLits"
                     value="${logement.nbLits != null ? logement.nbLits : 1}" min="1" max="50" required>
              <button type="button" class="btn btn-outline-secondary" onclick="increment('nbLits')">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>

          <div class="col-md-3 col-6">
            <label for="nbSallesBain" class="form-label">Salles de bain <span class="text-danger">*</span></label>
            <div class="input-group">
              <button type="button" class="btn btn-outline-secondary" onclick="decrement('nbSallesBain')">
                <i class="fas fa-minus"></i>
              </button>
              <input type="number" class="form-control text-center" id="nbSallesBain" name="nbSallesBain"
                     value="${logement.nbSallesBain != null ? logement.nbSallesBain : 1}" min="1" max="20" required>
              <button type="button" class="btn btn-outline-secondary" onclick="increment('nbSallesBain')">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>

          <div class="col-md-3 col-6">
            <label for="capaciteMax" class="form-label">Voyageurs max <span class="text-danger">*</span></label>
            <div class="input-group">
              <button type="button" class="btn btn-outline-secondary" onclick="decrement('capaciteMax')">
                <i class="fas fa-minus"></i>
              </button>
              <input type="number" class="form-control text-center" id="capaciteMax" name="capaciteMax"
                     value="${logement.capaciteMax != null ? logement.capaciteMax : 2}" min="1" max="50" required>
              <button type="button" class="btn btn-outline-secondary" onclick="increment('capaciteMax')">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Étape 3: Adresse -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-map-marker-alt text-primary me-2"></i>Adresse
        </h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-12">
            <label for="adresse" class="form-label">Adresse <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="adresse" name="adresse"
                   value="${logement.adresseLigne1}" required
                   placeholder="Ex: 15 rue de la Paix">
            <div class="invalid-feedback">L'adresse est obligatoire</div>
          </div>

          <div class="col-md-3">
            <label for="codePostal" class="form-label">Code postal <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="codePostal" name="codePostal"
                   value="${logement.codePostal}" required maxlength="10"
                   placeholder="Ex: 75002">
            <div class="invalid-feedback">Le code postal est obligatoire</div>
          </div>

          <div class="col-md-5">
            <label for="ville" class="form-label">Ville <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="ville" name="ville"
                   value="${logement.ville}" required maxlength="100"
                   placeholder="Ex: Paris">
            <div class="invalid-feedback">La ville est obligatoire</div>
          </div>

          <div class="col-md-4">
            <label for="region" class="form-label">Région</label>
            <input type="text" class="form-control" id="region" name="region"
                   value="${logement.region}" maxlength="100"
                   placeholder="Ex: Île-de-France">
          </div>

          <div class="col-md-4">
            <label for="pays" class="form-label">Pays</label>
            <input type="text" class="form-control" id="pays" name="pays"
                   value="${logement.pays != null ? logement.pays : 'France'}" maxlength="100">
          </div>

          <!-- Coordonnées GPS (cachées, remplies par JS si besoin) -->
          <input type="hidden" id="latitude" name="latitude" value="${logement.latitude}">
          <input type="hidden" id="longitude" name="longitude" value="${logement.longitude}">
        </div>
      </div>
    </div>

    <!-- Étape 4: Tarification -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-euro-sign text-primary me-2"></i>Tarification
        </h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <label for="prixNuit" class="form-label">Prix par nuit <span class="text-danger">*</span></label>
            <div class="input-group">
              <input type="number" class="form-control" id="prixNuit" name="prixNuit"
                     value="${logement.prixNuit}" required min="1" max="99999" step="0.01"
                     placeholder="Ex: 75">
              <span class="input-group-text">€</span>
            </div>
            <div class="invalid-feedback">Le prix par nuit est obligatoire</div>
          </div>

          <div class="col-md-4">
            <label for="fraisMenage" class="form-label">Frais de ménage</label>
            <div class="input-group">
              <input type="number" class="form-control" id="fraisMenage" name="fraisMenage"
                     value="${logement.fraisMenage != null ? logement.fraisMenage : 0}"
                     min="0" max="9999" step="0.01" placeholder="Ex: 30">
              <span class="input-group-text">€</span>
            </div>
            <div class="form-text">Frais uniques par réservation</div>
          </div>

          <div class="col-md-4">
            <label for="delaiAnnulation" class="form-label">Délai d'annulation</label>
            <div class="input-group">
              <input type="number" class="form-control" id="delaiAnnulation" name="delaiAnnulation"
                     value="${logement.delaiAnnulation != null ? logement.delaiAnnulation : 7}"
                     min="0" max="90">
              <span class="input-group-text">jours</span>
            </div>
            <div class="form-text">Remboursement total si annulé avant ce délai</div>
          </div>
        </div>

        <!-- Estimation -->
        <div class="mt-4 p-3 bg-light rounded">
          <div class="row align-items-center">
            <div class="col-md-8">
              <h6 class="mb-1">Estimation de vos revenus</h6>
              <p class="text-muted small mb-0">
                Basé sur un taux d'occupation moyen de 60% (18 nuits/mois)
              </p>
            </div>
            <div class="col-md-4 text-md-end">
              <span class="h4 text-success mb-0" id="estimationRevenus">0 €</span>
              <span class="text-muted">/ mois</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Étape 5: Règlement -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-clipboard-list text-primary me-2"></i>Règlement
        </h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-6">
            <label for="heureArrivee" class="form-label">Heure d'arrivée</label>
            <select class="form-select" id="heureArrivee" name="heureArrivee">
              <option value="">Flexible</option>
              <c:forEach var="h" begin="8" end="23">
                <option value="${h}:00" ${logement.heureArrivee == h.toString().concat(':00') ? 'selected' : ''}>
                  À partir de ${h}:00
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="col-md-6">
            <label for="heureDepart" class="form-label">Heure de départ</label>
            <select class="form-select" id="heureDepart" name="heureDepart">
              <option value="">Flexible</option>
              <c:forEach var="h" begin="6" end="18">
                <option value="${h}:00" ${logement.heureDepart == h.toString().concat(':00') ? 'selected' : ''}>
                  Avant ${h}:00
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="col-12">
            <label for="reglementInterieur" class="form-label">Règlement intérieur</label>
            <textarea class="form-control" id="reglementInterieur" name="reglementInterieur"
                      rows="4" maxlength="2000"
                      placeholder="Ex: Non-fumeur, pas de fêtes, animaux non acceptés...">${logement.reglementInterieur}</textarea>
            <div class="form-text">Indiquez les règles importantes à respecter</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Étape 6: Équipements -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-couch text-primary me-2"></i>Équipements
        </h5>
      </div>
      <div class="card-body">
        <p class="text-muted mb-3">Sélectionnez les équipements disponibles dans votre logement</p>

        <c:set var="currentCategorie" value=""/>
        <c:forEach var="equipement" items="${equipements}">
        <c:if test="${equipement.categorie != currentCategorie}">
        <c:if test="${not empty currentCategorie}">
      </div><!-- Fermer la row précédente -->
      </c:if>
      <h6 class="mt-3 mb-2 text-muted">${equipement.categorie}</h6>
      <div class="row g-2">
        <c:set var="currentCategorie" value="${equipement.categorie}"/>
        </c:if>

        <div class="col-md-4 col-6">
          <div class="form-check">
            <input class="form-check-input" type="checkbox"
                   name="equipements" value="${equipement.id}"
                   id="equip_${equipement.id}"
                   <c:if test="${logement.equipementIds.contains(equipement.id)}">checked</c:if>>
            <label class="form-check-label" for="equip_${equipement.id}">
              <i class="fas ${equipement.icone} me-1 text-muted"></i>
                ${equipement.nom}
            </label>
          </div>
        </div>
        </c:forEach>
        <c:if test="${not empty equipements}">
      </div><!-- Fermer la dernière row -->
      </c:if>
    </div>
    </div>

    <!-- Étape 7: Photos (optionnel à la création) -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-header bg-white border-bottom">
        <h5 class="mb-0">
          <i class="fas fa-images text-primary me-2"></i>Photos
          <span class="badge bg-secondary ms-2">Optionnel</span>
        </h5>
      </div>
      <div class="card-body">
        <p class="text-muted mb-3">
          Vous pourrez ajouter des photos après la création du logement.
        </p>

        <div class="upload-zone border-2 border-dashed rounded p-4 text-center" id="uploadZone">
          <i class="fas fa-cloud-upload-alt fa-3x text-muted mb-3"></i>
          <p class="mb-2">Glissez vos photos ici ou</p>
          <label for="photos" class="btn btn-outline-primary">
            <i class="fas fa-folder-open me-2"></i>Parcourir
          </label>
          <input type="file" id="photos" name="photos" multiple accept="image/*" class="d-none">
          <p class="small text-muted mt-2 mb-0">JPG, PNG ou WebP • Max 10 Mo par image</p>
        </div>

        <!-- Prévisualisation des photos -->
        <div class="row g-2 mt-3" id="photosPreviews"></div>
      </div>
    </div>

    <!-- Actions -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col-md-6 mb-3 mb-md-0">
            <div class="form-check">
              <input class="form-check-input" type="checkbox" id="publierDirectement" name="publierDirectement">
              <label class="form-check-label" for="publierDirectement">
                Publier directement (sinon le logement sera en brouillon)
              </label>
            </div>
          </div>
          <div class="col-md-6 text-md-end">
            <a href="${pageContext.request.contextPath}/hote/logements" class="btn btn-outline-secondary me-2">
              <i class="fas fa-times me-2"></i>Annuler
            </a>
            <button type="submit" class="btn btn-primary-hebergo btn-lg">
              <i class="fas fa-save me-2"></i>Enregistrer le logement
            </button>
          </div>
        </div>
      </div>
    </div>
  </form>
</main>

<jsp:include page="/WEB-INF/views/components/footer.jsp"/>

<style>
  .upload-zone {
    border-style: dashed !important;
    border-color: #dee2e6 !important;
    background-color: #f8f9fa;
    transition: all 0.3s ease;
    cursor: pointer;
  }

  .upload-zone:hover,
  .upload-zone.dragover {
    border-color: var(--primary-color) !important;
    background-color: #fff5f7;
  }

  .photo-preview {
    position: relative;
  }

  .photo-preview img {
    width: 100%;
    height: 120px;
    object-fit: cover;
    border-radius: 8px;
  }

  .photo-preview .btn-remove {
    position: absolute;
    top: 5px;
    right: 5px;
    padding: 2px 6px;
    font-size: 12px;
  }
</style>

<script>
  document.addEventListener('DOMContentLoaded', function() {

    // Compteur de caractères description
    const description = document.getElementById('description');
    const descriptionCount = document.getElementById('descriptionCount');

    function updateDescriptionCount() {
      descriptionCount.textContent = description.value.length;
    }
    description.addEventListener('input', updateDescriptionCount);
    updateDescriptionCount();

    // Estimation des revenus
    const prixNuit = document.getElementById('prixNuit');
    const estimationRevenus = document.getElementById('estimationRevenus');

    function updateEstimation() {
      const prix = parseFloat(prixNuit.value) || 0;
      const estimation = Math.round(prix * 18); // 18 nuits/mois (60%)
      estimationRevenus.textContent = estimation.toLocaleString('fr-FR') + ' €';
    }
    prixNuit.addEventListener('input', updateEstimation);
    updateEstimation();

    // Upload de photos
    const uploadZone = document.getElementById('uploadZone');
    const photosInput = document.getElementById('photos');
    const photosPreviews = document.getElementById('photosPreviews');

    uploadZone.addEventListener('click', () => photosInput.click());

    uploadZone.addEventListener('dragover', (e) => {
      e.preventDefault();
      uploadZone.classList.add('dragover');
    });

    uploadZone.addEventListener('dragleave', () => {
      uploadZone.classList.remove('dragover');
    });

    uploadZone.addEventListener('drop', (e) => {
      e.preventDefault();
      uploadZone.classList.remove('dragover');
      handleFiles(e.dataTransfer.files);
    });

    photosInput.addEventListener('change', () => {
      handleFiles(photosInput.files);
    });

    function handleFiles(files) {
      Array.from(files).forEach((file, index) => {
        if (!file.type.startsWith('image/')) return;
        if (file.size > 10 * 1024 * 1024) {
          alert('Le fichier ' + file.name + ' est trop volumineux (max 10 Mo)');
          return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
          const col = document.createElement('div');
          col.className = 'col-md-2 col-4 photo-preview';
          col.innerHTML = `
                    <img src="${e.target.result}" alt="Prévisualisation">
                    <button type="button" class="btn btn-danger btn-sm btn-remove" onclick="this.parentElement.remove()">
                        <i class="fas fa-times"></i>
                    </button>
                `;
          photosPreviews.appendChild(col);
        };
        reader.readAsDataURL(file);
      });
    }

    // Validation Bootstrap
    const form = document.getElementById('formLogement');
    form.addEventListener('submit', function(event) {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();

        // Scroll vers la première erreur
        const firstInvalid = form.querySelector(':invalid');
        if (firstInvalid) {
          firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
          firstInvalid.focus();
        }
      }
      form.classList.add('was-validated');
    });
  });

  // Fonctions increment/decrement pour les compteurs
  function increment(inputId) {
    const input = document.getElementById(inputId);
    const max = parseInt(input.max) || 999;
    if (parseInt(input.value) < max) {
      input.value = parseInt(input.value) + 1;
    }
  }

  function decrement(inputId) {
    const input = document.getElementById(inputId);
    const min = parseInt(input.min) || 0;
    if (parseInt(input.value) > min) {
      input.value = parseInt(input.value) - 1;
    }
  }
</script>
