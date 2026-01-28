<%--
  Created by IntelliJ IDEA.
  User: DEV01
  Date: 26/01/2026
  Time: 13:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>Ajouter un logement</h1>

<form method="post" action="ajouter-logement">

  <label>Titre :</label><br>
  <input type="text" name="titre" value="${logement.titre}" required><br><br>

  <label>Description :</label><br>
  <textarea name="description" required>${logement.description}</textarea><br><br>

  <label>Adresse :</label><br>
  <input type="text" name="adresse" required value="${logement.adresse.adresse}"><br><br>

  <label>Ville :</label><br>
  <input type="text" name="ville" required value="${logement.ville}"><br><br>

  <label>Prix par nuit :</label><br>
  <input type="number" name="prix" step="0.01" required value="${logement.prixNuit}"><br><br>

  <label>Capacité :</label><br>
  <input type="number" name="capacite" required value="${logement.capaciteMax}" ><br><br>

  <button type="submit">Ajouter le logement</button>

</form>

<div>

  <c:if test="${ not empty logement }">

    <h2>✅ Logement ajouté avec succès</h2>

    <p><strong>Titre :</strong><c:out value="${logement.titre}" /></p>
    <p><strong>Description :</strong><c:out value="${ logement.description }" /></p>
    <p><strong>Adresse :</strong> <c:out value="${ logement.adresse }" /></p>
    <p><strong>Ville :</strong> <c:out value="${ logement.ville }"/> </p>
    <p><strong>Prix par nuit : <c:out value="${ logement.prixNuit }" /> </strong>  €</p>
    <p><strong>Capacité :</strong> <c:out value="${ logement.capaciteMax }" /> personnes</p>

  </c:if>

</div>
</body>
</html>
