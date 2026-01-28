/**
 * SquatRBnB - Scripts principaux
 * Version: 1.0.0
 */

// ==========================================
// INITIALISATION
// ==========================================
document.addEventListener('DOMContentLoaded', function() {
    initSearchValidation();
    initDatePickers();
    initPhotoGallery();
    initTooltips();
});

// ==========================================
// VALIDATION RECHERCHE
// ==========================================
function initSearchValidation() {
    const dateArrivee = document.querySelector('input[name="dateArrivee"]');
    const dateDepart = document.querySelector('input[name="dateDepart"]');

    if (dateArrivee && dateDepart) {
        // Date minimum = aujourd'hui
        const today = new Date().toISOString().split('T')[0];
        dateArrivee.min = today;

        // Date de départ minimum = date d'arrivée
        dateArrivee.addEventListener('change', function() {
            dateDepart.min = this.value;
            if (dateDepart.value && dateDepart.value < this.value) {
                dateDepart.value = '';
            }
        });
    }
}

// ==========================================
// DATE PICKERS
// ==========================================
function initDatePickers() {
    // Configuration des date pickers si vous utilisez une librairie
    // Exemple avec flatpickr (si installé):
    /*
    flatpickr(".datepicker", {
        dateFormat: "Y-m-d",
        minDate: "today",
        locale: "fr"
    });
    */
}

// ==========================================
// GALERIE PHOTOS
// ==========================================
function initPhotoGallery() {
    const galleryImages = document.querySelectorAll('.photo-gallery img');

    galleryImages.forEach((img, index) => {
        img.addEventListener('click', function() {
            const modal = document.getElementById('galleryModal');
            if (modal) {
                const carousel = bootstrap.Carousel.getOrCreateInstance(
                    document.getElementById('carouselGallery')
                );
                carousel.to(index);
            }
        });
    });
}

// ==========================================
// TOOLTIPS BOOTSTRAP
// ==========================================
function initTooltips() {
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
}

// ==========================================
// GESTION DES LOGEMENTS (HÔTE)
// ==========================================

/**
 * Changer le statut d'un logement
 */
function changerStatut(logementId, statut) {
    const statutLabel = statut === 'DISPONIBLE' ? 'activer' : 'désactiver';

    if (confirm(`Êtes-vous sûr de vouloir ${statutLabel} ce logement ?`)) {
        const form = document.getElementById('formStatut');
        if (form) {
            document.getElementById('nouveauStatut').value = statut;
            form.action = getContextPath() + '/hote/logement/' + logementId + '/statut';
            form.submit();
        }
    }
}

/**
 * Supprimer un logement
 */
function supprimerLogement(logementId) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce logement ?\nCette action est irréversible.')) {
        const form = document.getElementById('formSupprimer');
        if (form) {
            form.action = getContextPath() + '/hote/logement/' + logementId + '/supprimer';
            form.submit();
        }
    }
}

/**
 * Archiver un logement
 */
function archiverLogement(logementId) {
    if (confirm('Voulez-vous archiver ce logement ?\nIl ne sera plus visible publiquement.')) {
        const form = document.getElementById('formArchiver');
        if (form) {
            form.action = getContextPath() + '/hote/logement/' + logementId + '/archiver';
            form.submit();
        }
    }
}

// ==========================================
// GESTION DES RÉSERVATIONS
// ==========================================

/**
 * Confirmer une réservation
 */
function confirmerReservation(reservationId) {
    if (confirm('Voulez-vous confirmer cette réservation ?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = getContextPath() + '/hote/reservation/' + reservationId + '/confirmer';
        document.body.appendChild(form);
        form.submit();
    }
}

/**
 * Annuler une réservation
 */
function annulerReservation(reservationId, motif) {
    if (!motif || motif.trim() === '') {
        alert('Veuillez indiquer un motif d\'annulation');
        return false;
    }
    return true;
}

/**
 * Afficher le modal d'annulation
 */
function showAnnulerModal(reservationId) {
    const modal = new bootstrap.Modal(document.getElementById('annulerModal' + reservationId));
    modal.show();
}

// ==========================================
// RÉSERVATION (LOCATAIRE)
// ==========================================

/**
 * Calculer le prix total de la réservation
 */
function calculerPrixTotal() {
    const dateArrivee = document.querySelector('input[name="dateDebut"]');
    const dateDepart = document.querySelector('input[name="dateFin"]');
    const prixNuit = parseFloat(document.getElementById('prixNuit').value);
    const fraisMenage = parseFloat(document.getElementById('fraisMenage').value) || 0;

    if (dateArrivee.value && dateDepart.value) {
        const debut = new Date(dateArrivee.value);
        const fin = new Date(dateDepart.value);
        const nbNuits = Math.ceil((fin - debut) / (1000 * 60 * 60 * 24));

        if (nbNuits > 0) {
            const sousTotal = prixNuit * nbNuits;
            const fraisService = sousTotal * 0.1; // 10% de frais de service
            const total = sousTotal + fraisMenage + fraisService;

            // Afficher les résultats
            document.getElementById('nbNuits').textContent = nbNuits;
            document.getElementById('sousTotal').textContent = sousTotal.toFixed(2);
            document.getElementById('fraisServiceAffiche').textContent = fraisService.toFixed(2);
            document.getElementById('totalAffiche').textContent = total.toFixed(2);

            // Mettre à jour les champs cachés
            document.querySelector('input[name="nbNuits"]').value = nbNuits;
            document.querySelector('input[name="prixSousTotal"]').value = sousTotal.toFixed(2);
            document.querySelector('input[name="fraisService"]').value = fraisService.toFixed(2);
            document.querySelector('input[name="prixTotal"]').value = total.toFixed(2);
        }
    }
}

/**
 * Initialiser le formulaire de réservation
 */
function initReservationForm() {
    const dateArrivee = document.querySelector('input[name="dateDebut"]');
    const dateDepart = document.querySelector('input[name="dateFin"]');

    if (dateArrivee && dateDepart) {
        dateArrivee.addEventListener('change', calculerPrixTotal);
        dateDepart.addEventListener('change', calculerPrixTotal);
    }
}

// ==========================================
// CARTE LEAFLET
// ==========================================

/**
 * Initialiser la carte Leaflet
 */
function initMap(lat, lng, titre, adresse, prix) {
    if (typeof L === 'undefined') {
        console.error('Leaflet non chargé');
        return;
    }

    const map = L.map('map').setView([lat, lng], 14);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
    }).addTo(map);

    // Zone approximative (cercle au lieu de marker exact)
    L.circle([lat, lng], {
        color: '#FF5A5F',
        fillColor: '#FF5A5F',
        fillOpacity: 0.2,
        radius: 500
    }).addTo(map);

    return map;
}

/**
 * Géocoder une adresse avec Nominatim
 */
async function geocodeAddress(adresse, ville, pays) {
    const query = encodeURIComponent(`${adresse}, ${ville}, ${pays}`);
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${query}`;

    try {
        const response = await fetch(url);
        const data = await response.json();

        if (data && data.length > 0) {
            return {
                lat: parseFloat(data[0].lat),
                lng: parseFloat(data[0].lon)
            };
        }
    } catch (error) {
        console.error('Erreur géocodage:', error);
    }

    return null;
}

// ==========================================
// UPLOAD DE PHOTOS
// ==========================================

/**
 * Prévisualiser les photos avant upload
 */
function previewPhotos(input) {
    const preview = document.getElementById('photosPreview');
    if (!preview) return;

    preview.innerHTML = '';

    if (input.files) {
        [...input.files].forEach((file, index) => {
            const reader = new FileReader();

            reader.onload = function(e) {
                const div = document.createElement('div');
                div.className = 'col-md-3 mb-3';
                div.innerHTML = `
                    <div class="position-relative">
                        <img src="${e.target.result}" class="img-fluid rounded" alt="Photo ${index + 1}">
                        <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0 m-2"
                                onclick="removePhoto(${index})">
                            <i class="fas fa-times"></i>
                        </button>
                        ${index === 0 ? '<span class="badge bg-primary position-absolute bottom-0 start-0 m-2">Photo principale</span>' : ''}
                    </div>
                `;
                preview.appendChild(div);
            };

            reader.readAsDataURL(file);
        });
    }
}

/**
 * Retirer une photo de la prévisualisation
 */
function removePhoto(index) {
    const input = document.getElementById('photosInput');
    const dt = new DataTransfer();

    [...input.files].forEach((file, i) => {
        if (i !== index) dt.items.add(file);
    });

    input.files = dt.files;
    previewPhotos(input);
}

// ==========================================
// FILTRES ET RECHERCHE
// ==========================================

/**
 * Appliquer les filtres de recherche
 */
function applyFilters() {
    const form = document.getElementById('filterForm');
    if (form) {
        form.submit();
    }
}

/**
 * Réinitialiser les filtres
 */
function resetFilters() {
    const form = document.getElementById('filterForm');
    if (form) {
        form.reset();
        form.submit();
    }
}

// ==========================================
// NOTIFICATIONS / FLASH MESSAGES
// ==========================================

/**
 * Afficher une notification toast
 */
function showToast(message, type = 'success') {
    const toastHTML = `
        <div class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    const toastContainer = document.getElementById('toastContainer');
    if (toastContainer) {
        toastContainer.innerHTML = toastHTML;
        const toastElement = toastContainer.querySelector('.toast');
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
    }
}

/**
 * Auto-hide des alertes après 5 secondes
 */
function autoHideAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

// ==========================================
// UTILITAIRES
// ==========================================

/**
 * Obtenir le context path de l'application
 */
function getContextPath() {
    return window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
}

/**
 * Formater un prix
 */
function formatPrice(price) {
    return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
    }).format(price);
}

/**
 * Formater une date
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(date);
}

/**
 * Copier dans le presse-papier
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showToast('Copié dans le presse-papier', 'success');
    } catch (err) {
        console.error('Erreur copie:', err);
        showToast('Erreur lors de la copie', 'danger');
    }
}

// ==========================================
// CHARGEMENT INITIAL
// ==========================================

// Auto-hide alerts
document.addEventListener('DOMContentLoaded', autoHideAlerts);

// Init reservation form if exists
if (document.getElementById('reservationForm')) {
    initReservationForm();
}