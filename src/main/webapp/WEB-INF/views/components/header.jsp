
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="SquatRBnB - Trouvez votre logement idéal partout dans le monde">
  <meta name="author" content="SquatRBnB">

  <title>${param.title != null ? param.title : 'SquatRBnB - Location de logements'}</title>

  <!-- Favicon -->
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">

  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

  <!-- Leaflet (si carte) -->
  <c:if test="${param.useMap == 'true'}">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
  </c:if>

  <!-- CSS personnalisé -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">

  <!-- CSS additionnel si spécifié -->
  <c:if test="${not empty param.additionalCSS}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/${param.additionalCSS}">
  </c:if>
</head>
<body>



<%--<!DOCTYPE html>--%>
<%--<html>--%>
<%--<head>--%>
<%--  <title>--%>
<%--    <%= request.getParameter("pageTitle") != null ? request.getParameter("pageTitle") : "Accceuil - Squat'R"%>--%>
<%--  </title>--%>
<%--  <script src="https://kit.fontawesome.com/6e2cd958b0.js" crossorigin="anonymous"></script>--%>
<%--</head>--%>
<%--<body>--%>

<%--<nav class="navbar navbar-expand-lg navbar-dark bg-dark">--%>
<%--  <div class="container-fluid">--%>

<%--    <a class="navbar-brand" href="/home">MonSite</a>--%>

<%--    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">--%>
<%--      <span class="navbar-toggler-icon"></span>--%>
<%--    </button>--%>

<%--    <div class="collapse navbar-collapse" id="navbarNav">--%>
<%--      <ul class="navbar-nav">--%>

<%--        <!-- Accueil -->--%>
<%--        <li class="nav-item">--%>
<%--          <a class="nav-link ${activePage == 'home' ? 'active' : ''}"--%>
<%--             href="/home"--%>
<%--             aria-current="${activePage == 'home' ? 'page' : ''}">--%>
<%--            Accueil--%>
<%--          </a>--%>
<%--        </li>--%>

<%--        <!-- Contact -->--%>
<%--        <li class="nav-item">--%>
<%--          <a class="nav-link ${activePage == 'contact' ? 'active' : ''}"--%>
<%--             href="/contact"--%>
<%--             aria-current="${activePage == 'contact' ? 'page' : ''}">--%>
<%--            Contact--%>
<%--          </a>--%>
<%--        </li>--%>
<%--        <a href="${pageContext.request.contextPath}/auth/login">Connectez-vous</a>--%>
<%--        <li class="nav-item">--%>
<%--          <a class="nav-link ${activePage == 'Connexion' ? 'active' : ''}"--%>
<%--             href="${pageContext.request.contextPath}/auth/login"--%>
<%--             aria-current="${activePage == 'login' ? 'page' : ''}">--%>
<%--            Connexion--%>
<%--          </a>--%>
<%--        </li>--%>
<%--        <li class="nav-item">--%>
<%--          <a class="nav-link ${activePage == 'Inscription' ? 'active' : ''}"--%>
<%--             href="${pageContext.request.contextPath}/auth/register"--%>
<%--             aria-current="${activePage == 'register' ? 'page' : ''}">--%>
<%--            Inscription--%>
<%--          </a>--%>
<%--        </li>--%>
<%--      </ul>--%>
<%--    </div>--%>
<%--  </div>--%>
<%--</nav>--%>

<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@ taglib prefix="c" uri="jakarta.tags.core" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html lang="fr">--%>
<%--<head>--%>
<%--  <meta charset="UTF-8">--%>
<%--  <meta name="viewport" content="width=device-width, initial-scale=1.0">--%>
<%--  <title>${param.title != null ? param.title : 'Squat\'R'} | Squat'R</title>--%>

<%--  <!-- Favicon -->--%>
<%--  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">--%>

<%--  <!-- Google Fonts -->--%>
<%--  <link rel="preconnect" href="https://fonts.googleapis.com">--%>
<%--  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>--%>
<%--  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">--%>

<%--  <!-- Bootstrap 5 CSS -->--%>
<%--  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">--%>

<%--  <!-- Font Awesome 6 -->--%>
<%--  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">--%>

<%--  <!-- Squat'r CSS -->--%>
<%--  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">--%>
<%--<!-- Leaflet -->--%>
<%--  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />--%>
<%--  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>--%>
<%--</head>--%>
<%--<body>--%>

<%--<header>--%>
<%--  <%@ include file="/WEB-INF/views/components/navbar.jsp" %>--%>
<%--</header>--%>