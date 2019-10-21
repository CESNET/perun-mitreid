<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags/common" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="reqURL" required="true" %>
<%@ attribute name="baseURL" required="true" %>
<%@ attribute name="cssLinks" required="true" type="java.util.ArrayList<String>" %>

<c:set var="logoURL" value="${baseURL}proxy/module.php/bbmri/res/img/BBMRI-ERIC-gateway-for-health_430.png"/>

<o:headerInit title="${title}" reqURL="${reqURL}" baseURL="${baseURL}" />

<link rel="stylesheet" type="text/css" href="${baseURL}proxy/module.php/bbmri/res/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="${baseURL}proxy/module.php/bbmri/res/css/bbmri.css" />

<o:headerCssLinks cssLinks="${cssLinks}"/>
<o:headerBody logoURL="${logoURL}" baseURL="${baseURL}"/>

</head>
