<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ attribute name="js" required="false"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/common" %>
<jsp:useBean id="date" class="java.util.Date" />
<c:set var="issuer" value="${config.issuer}" />
<c:set var="baseUrl" value="${fn:substringBefore(issuer, 'oidc')}" />
<div id="footer">
    <div style="margin: 0px auto; max-width: 1000px;">
        <div style="float: left;">
            <img src="<c:out value='${baseUrl}'/>proxy/module.php/ceitec/res/img/logo_64.png">
        </div>
        <div style="float: left;">
            <p>CEITEC, Masaryk University, Žerotínovo nám. 9, 601 77 Brno, Czech Republic
                &nbsp; &nbsp; +420 549 498 732 &nbsp;
                <a href="mailto:is.ceitec@ceitec.cz">is.ceitec@ceitec.cz</a>
            </p>
            <p>Copyright &copy; CEITEC <fmt:formatDate value="${date}" pattern="yyyy" /></p>
        </div>
    </div>
</div>
<t:scripts />