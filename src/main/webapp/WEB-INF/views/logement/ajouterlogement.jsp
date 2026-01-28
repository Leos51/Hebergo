<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>

<h1>Ajouter un logement</h1>

<form method="post" action="ajouter-logement">

    <label>Titre :</label><br>
    <input type="text" name="titre" required><br><br>

    <label>Description :</label><br>
    <textarea name="description" required></textarea><br><br>

    <label>Adresse :</label><br>
    <input type="text" name="adresse" required><br><br>

    <label>Ville :</label><br>
    <input type="text" name="ville" required><br><br>

    <label>Prix par nuit :</label><br>
    <input type="number" name="prix" step="0.01" required><br><br>

    <label>Capacité :</label><br>
    <input type="number" name="capacite" required><br><br>

    <button type="submit">Ajouter le logement</button>

</form>

<div>

    <c:if test="${ not empty logement }">

        <h2>✅ Logement ajouté avec succès</h2>

        <p><strong>Titre :</strong><c:out value="${logement.titre}" /></p>
        <p><strong>Description :</strong><c:out value="${ logement.description }" /></p>
        <p><strong>Adresse :</strong> <c:out value="${ logement.adresse }" /></p>
        <p><strong>Ville :</strong> <c:out value="${ logement.ville }"/> </p>
        <p><strong>Prix par nuit : <c:out value="${ logement.prixParNuit }" /> </strong>  €</p>
        <p><strong>Capacité :</strong> <c:out value="${ logement.capacite }" /> personnes</p>

    </c:if>

</div>

</body>
</html>