<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/components/header.jsp">
    <jsp:param name="title" value="Mon compte"/>
</jsp:include>

<jsp:include page="/WEB-INF/views/components/sidebar-hote.jsp">
    <jsp:param name="active" value="dashboard"/>
</jsp:include>

<h1>Profil page</h1>



<jsp:include page="/WEB-INF/views/components/footer.jsp"/>