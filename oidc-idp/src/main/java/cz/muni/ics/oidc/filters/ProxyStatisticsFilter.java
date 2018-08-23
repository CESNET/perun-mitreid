package cz.muni.ics.oidc.filters;

import com.google.common.base.Strings;
import cz.muni.ics.oidc.PerunConnector;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.openid.connect.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
 * Filter for collecting data about login.
 *
 * @author Dominik Baránek <0Baranek.dominik0@gmail.com>
 */
public class ProxyStatisticsFilter extends GenericFilterBean {

	private final static Logger log = LoggerFactory.getLogger(ProxyStatisticsFilter.class);

	private static final String IDP_NAME = "sourceIdPName";
	private static final String IDP_ENTITY_ID = "sourceIdPEntityID";

	@Autowired
	private OAuth2RequestFactory authRequestFactory;

	@Autowired
	private ClientDetailsEntityService clientService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private PerunConnector perunConnector;

	@Autowired
	private DataSource mitreIdStats;

	private static final String REQ_PATTERN = "/authorize";
	private RequestMatcher requestMatcher = new AntPathRequestMatcher(REQ_PATTERN);

	private String identityProvidersTableName;
	private String identityProvidersMapTableName;
	private String serviceProvidersTableName;
	private String serviceProvidersMapTableName;

	public void setIdentityProvidersTableName(String identityProvidersTableName) {
		this.identityProvidersTableName = identityProvidersTableName;
	}

	public void setIdentityProvidersMapTableName(String identityProvidersMapTableName) {
		this.identityProvidersMapTableName = identityProvidersMapTableName;
	}

	public void setServiceProvidersTableName(String serviceProvidersTableName) {
		this.serviceProvidersTableName = serviceProvidersTableName;
	}

	public void setServiceProvidersMapTableName(String serviceProvidersMapTableName) {
		this.serviceProvidersMapTableName = serviceProvidersMapTableName;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;

		if (!requestMatcher.matches(request) || request.getParameter("response_type") == null) {
			chain.doFilter(req, res);
			return;
		}

		AuthorizationRequest authRequest = authRequestFactory.createAuthorizationRequest(
				createRequestMap(request.getParameterMap()));

		ClientDetailsEntity client;
		if (Strings.isNullOrEmpty(authRequest.getClientId())) {
			log.warn("ClientID is null or empty, skip to next filter");
			chain.doFilter(req, res);
			return;
		}
		client = clientService.loadClientByClientId(authRequest.getClientId());
		log.debug("Found client: {}", client);

		if(Strings.isNullOrEmpty(client.getClientName())) {
			log.warn("ClientName is null or empty, skip to next filter");
			chain.doFilter(req, res);
			return;
		}

		String clientIdentifier = client.getClientId();
		String clientName = client.getClientName();


		if(Strings.isNullOrEmpty((String) request.getAttribute(IDP_ENTITY_ID))) {
			log.warn("Attribute '" + IDP_ENTITY_ID + "' is null or empty, skip to next filter");
			chain.doFilter(req, res);
			return;
		}

		String idpEntityId = new String(((String)request.getAttribute(IDP_ENTITY_ID)).getBytes("iso-8859-1"), "utf-8");
		String idpName = null;

		if(!Strings.isNullOrEmpty((String) request.getAttribute(IDP_NAME))) {
			idpName = new String(((String)request.getAttribute(IDP_NAME)).getBytes("iso-8859-1"), "utf-8");
		}
		insertLogin(idpEntityId, idpName, clientIdentifier, clientName);

		chain.doFilter(req, res);
	}

	private Map<String, String> createRequestMap(Map<String, String[]> parameterMap) {
		Map<String, String> requestMap = new HashMap<>();
		for (String key : parameterMap.keySet()) {
			String[] val = parameterMap.get(key);
			if (val != null && val.length > 0) {
				requestMap.put(key, val[0]); // add the first value only (which is what Spring seems to do)
			}
		}

		return requestMap;
	}

	private void insertLogin(String idpEntityId, String idpName, String spIdentifier, String spName) {
		LocalDate date = LocalDate.now();

		String queryIdp = "INSERT INTO " + identityProvidersTableName + "(year, month, day, sourceIdp, count) VALUES(?,?,?,?,'1') ON DUPLICATE KEY UPDATE count = count + 1";
		String queryIdPMap = "INSERT INTO " + identityProvidersMapTableName + "(entityId, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?";
		String queryService = "INSERT INTO " + serviceProvidersTableName + "(year, month, day, service, count) VALUES(?,?,?,?,'1') ON DUPLICATE KEY UPDATE count = count + 1";
		String queryServiceMap = "INSERT INTO " + serviceProvidersMapTableName + "(identifier, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?";

		try (Connection c = mitreIdStats.getConnection()) {
			try (PreparedStatement preparedStatement = c.prepareStatement(queryIdp)) {
				preparedStatement.setInt(1, date.getYear());
				preparedStatement.setInt(2, date.getMonthValue());
				preparedStatement.setInt(3, date.getDayOfMonth());
				preparedStatement.setString(4, idpEntityId);
				preparedStatement.execute();
			}
			try (PreparedStatement preparedStatement = c.prepareStatement(queryService)) {
				preparedStatement.setInt(1, date.getYear());
				preparedStatement.setInt(2, date.getMonthValue());
				preparedStatement.setInt(3, date.getDayOfMonth());
				preparedStatement.setString(4, spIdentifier);
				preparedStatement.execute();
			}
			if (!Strings.isNullOrEmpty(idpName)){
				try (PreparedStatement preparedStatement = c.prepareStatement(queryIdPMap)){
					preparedStatement.setString(1, idpEntityId);
					preparedStatement.setString(2, idpName);
					preparedStatement.setString(3, idpName);
					preparedStatement.execute();
				}
			}
			if (!Strings.isNullOrEmpty(spName)) {
				try (PreparedStatement preparedStatement = c.prepareStatement(queryServiceMap)){
					preparedStatement.setString(1, spIdentifier);
					preparedStatement.setString(2, spName);
					preparedStatement.setString(3, spName);
					preparedStatement.execute();
				}
			}
			log.debug("The login log was successfully stored into database.");
		} catch (SQLException ex) {
			log.warn("Statistics weren't updated due to SQLException.");
			log.debug("SQLException {}", ex);
		}
	}
}
