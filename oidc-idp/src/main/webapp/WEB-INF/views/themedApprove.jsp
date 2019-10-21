<%@ page import="cz.muni.ics.oidc.server.elixir.GA4GHClaimSource" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="elixir" tagdir="/WEB-INF/tags/elixir" %>
<%@ taglib prefix="cesnet" tagdir="/WEB-INF/tags/cesnet" %>
<%@ taglib prefix="bbmri" tagdir="/WEB-INF/tags/bbmri" %>
<%@ taglib prefix="ceitec" tagdir="/WEB-INF/tags/ceitec" %>
<%@ taglib prefix="europdx" tagdir="/WEB-INF/tags/europdx" %>
<%@ taglib prefix="muni" tagdir="/WEB-INF/tags/muni" %>

<c:set var="baseURL" value="${fn:substringBefore(config.issuer, 'oidc')}" />

<%

String baseURL = (String) pageContext.getAttribute("baseURL");
List<String> cssLinks = new ArrayList<>();

cssLinks.add(baseURL + "proxy/module.php/consent/assets/css/consent.css");
cssLinks.add(baseURL + "proxy/module.php/perun/res/css/consent.css");

pageContext.setAttribute("cssLinks", cssLinks);

%>

<t:header title="${langProps['consent_header']}" reqURL="${reqURL}" baseURL="${baseURL}" cssLinks="${cssLinks}" theme="${theme}"/>

<h1>${langProps['consent_header']}</h1>

</div> <%-- header --%>

<div id="content">
	<c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION" />
		<form name="confirmationForm"
			  action="${pageContext.request.contextPath.endsWith('/') ? pageContext.request.contextPath : pageContext.request.contextPath.concat('/')}authorize" method="post">
			<p>${langProps['consent_privacypolicy']}
				&#32;<a target='_blank' href='${fn:escapeXml(client.policyUri)}'><em>${fn:escapeXml(client.clientName)}</em></a>
			</p>

				<ul id="perun-table_with_attributes" class="perun-attributes">
					<c:forEach var="scope" items="${scopes}">
						<c:set var="scopeValue" value="${langProps[scope.value]}"/>
						<c:if test="${empty fn:trim(scopeValue)}">
							<c:set var="scopeValue" value="${scope.value}"/>
						</c:if>

						<c:set var="singleClaim" value="${fn:length(claims[scope.value]) eq 1}" />

						<c:choose>
							<c:when test="${singleClaim}">
								<li class="perun-attr-singlevalue">
							</c:when>
							<c:otherwise>
								<li>
							</c:otherwise>
						</c:choose>

							<div class="row">
								<div class="col-sm-6">
                                    <div class="checkbox-wrapper">
                                        <input class="mt-0" type="checkbox" name="scope_${ fn:escapeXml(scope.value) }" checked="checked"
                                        id="scope_${fn:escapeXml(scope.value)}" value="${fn:escapeXml(scope.value)}">
                                    </div>
                                    <label class="perun-attrname attrname-formatter" for="scope_${fn:escapeXml(scope.value)}">
                                        ${scopeValue}
                                    </label>
								</div>
								<div class="perun-attrcontainer col-sm-6">
									<span class="perun-attrvalue">

										<ul class="perun-attrlist">
											<c:forEach var="claim" items="${claims[scope.value]}">
												<c:choose>
													<c:when test="${not singleClaim}">
														<li>
															<c:set var="claimKey" value="${langProps[claim.key]}"/>
															<c:if test="${empty fn:trim(claimKey)}">
																<c:set var="claimKey" value="${claim.key}"/>
															</c:if>
															<strong>${claimKey}:</strong>
															<c:choose>
																<c:when test="${claim.value.getClass().name eq 'java.util.ArrayList'}">
																	<br/>
																	<ul>
																		<c:forEach var="subValue" items="${claim.value}">
																			<li>${subValue}</li>
																		</c:forEach>
																	</ul>
																</c:when>
																<c:otherwise>
																	${claim.value}
																</c:otherwise>
															</c:choose>
														</li>
													</c:when>
													<c:when test="${claim.value.getClass().name eq 'java.util.ArrayList'}">
														<c:forEach var="subValue" items="${claim.value}">
															<c:choose>
																<c:when test="${claim.key=='ga4gh_passport_v1'}">
																	<li><%= GA4GHClaimSource.parseAndVerifyVisa((String)pageContext.findAttribute("subValue")).getPrettyString() %></li>
																</c:when>
																<c:otherwise>
																	<li>${subValue}</li>
																</c:otherwise>
															</c:choose>
														</c:forEach>
													</c:when>
													<c:otherwise>
														<li>${claim.value}</li>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</ul>

									</span>
								</div>
							</div>
						</li>
					</c:forEach>
				</ul>

			<div class="row" id="saveconsentcontainer">
				<div class="col-xs-12">
					<div class="checkbox">
						<input type="checkbox" form="yesform" name="saveconsent" id="saveconsent" value="1"/>
						<label for="saveconsent">${langProps['remember']}</label>
					</div>
				</div>
			</div>
			<input id="user_oauth_approval" name="user_oauth_approval" value="true" type="hidden" />
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="row">
				<div class="col-sm-6">
					<form action="$('#user_oauth_approval').attr('value',true)" id="yesform">
						<button id="yesbutton" name="yes" type="submit" class="btn btn-success btn-lg btn-block btn-primary">
							<span>${langProps['yes']}</span>
						</button>
					</form>
				</div>
				<div class="col-sm-6">
					<form action="$('#user_oauth_approval').attr('value',false)">
						<button id="nobutton" name="no" type="submit" class="btn btn-lg btn-default btn-block btn-no">
							<span>${langProps['no']}</span>
						</button>
					</form>
				</div>
			</div>
		</form>
</div>
</div><!-- wrap -->

<t:footer baseURL="${baseURL}" theme="${theme}"/>
