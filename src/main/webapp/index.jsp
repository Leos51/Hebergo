<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="pageTitle" value="Accueil" />
</jsp:include>
<main>
    <h1 class="text-center">
        INDEX PAGE - test hôte
    </h1>
    <!-- ===== SEARCH BAR ===== -->
    <form class="search-bar" action="afficherLogements" method="get">
        <input type="text" name="ville" placeholder="Rechercher par ville (ex : Nancy)">
        <button type="submit">Rechercher</button>
    </form>
    <!-- ===== LOGEMENTS POPULAIRES ===== -->
    <div class="section">
        <h2>Logements populaires</h2>

        <!-- Pour l’instant, lien simple vers la liste -->
        <div class="logements">

            <div class="logement-card">
                <h3>Appartement cosy</h3>
                <p>Nancy</p>
                <a href="afficherUnLogement">Voir le logement</a>
            </div>

            <div class="logement-card">
                <h3>Maison familiale</h3>
                <p>Metz</p>
                <a href="afficherUnLogement">Voir le logement</a>
            </div>

            <div class="logement-card">
                <h3>Studio étudiant</h3>
                <p>Nancy</p>
                <a href="afficherUnLogement">Voir le logement</a>
            </div>

        </div>
    </div>
</main>


<div id="map" style="height: 400px">

</div>
<script>
    const map = L.map('map').setView([48.6921, 6.1844], 13); // Exemple : Nancy

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
    }).addTo(map);

</script>

<%@ include file="/WEB-INF/views/components/footer.jsp" %>

