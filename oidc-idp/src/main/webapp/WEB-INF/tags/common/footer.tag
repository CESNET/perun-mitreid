<%@tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="elixir" tagdir="/WEB-INF/tags/elixir" %>
<%@ taglib prefix="cesnet" tagdir="/WEB-INF/tags/cesnet" %>
<%@ taglib prefix="bbmri" tagdir="/WEB-INF/tags/bbmri" %>
<%@ taglib prefix="ceitec" tagdir="/WEB-INF/tags/ceitec" %>
<%@ taglib prefix="europdx" tagdir="/WEB-INF/tags/europdx" %>
<%@ taglib prefix="muni" tagdir="/WEB-INF/tags/muni" %>
<%@ attribute name="baseURL" required="true" %>
<%@ attribute name="theme" required="true" %>

<c:choose>
    <c:when test="${theme eq 'elixir'}">
        <elixir:footer baseURL="${baseURL}"/>
    </c:when>
    <c:when test="${theme eq 'cesnet'}">
        <cesnet:footer baseURL="${baseURL}"/>
    </c:when>
    <c:when test="${theme eq 'bbmri'}">
        <bbmri:footer baseURL="${baseURL}"/>
    </c:when>
    <c:when test="${theme eq 'ceitec'}">
        <ceitec:footer baseURL="${baseURL}"/>
    </c:when>
    <c:when test="${theme eq 'europdx'}">
        <europdx:footer baseURL="${baseURL}"/>
    </c:when>
    <c:when test="${theme eq 'muni'}">
        <muni:footer baseURL="${baseURL}"/>
    </c:when>
    <c:otherwise>
        <o:footer />
    </c:otherwise>
</c:choose>

<t:scripts />
