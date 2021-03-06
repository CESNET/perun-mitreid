package cz.muni.ics.oidc.server.claims.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.UrlEscapers;
import cz.muni.ics.oidc.models.Facility;
import cz.muni.ics.oidc.models.Group;
import cz.muni.ics.oidc.models.PerunAttributeValue;
import cz.muni.ics.oidc.server.adapters.PerunAdapter;
import cz.muni.ics.oidc.server.claims.ClaimSourceInitContext;
import cz.muni.ics.oidc.server.claims.ClaimSourceProduceContext;
import cz.muni.ics.oidc.server.claims.ClaimUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This source converts groupNames and resource capabilities to AARC format and joins them with eduPersonEntitlement.
 *
 * Configuration (replace [claimName] with the name of the claim):
 * <ul>
 *     <li>
 *         <b>custom.claim.[claimName].source.forwardedEntitlements</b> - forwardedEntitlements attribute name,
 *         if not specified, the forwarded entitlements will not be added to the list
 *     </li>
 *     <li><b>custom.claim.[claimName].source.resourceCapabilities</b> - resource capabilities attribute name for resources</li>
 *     <li><b>custom.claim.[claimName].source.facilityCapabilities</b> - resource capabilities attribute name for facility</li>
 *     <li><b>custom.claim.[claimName].source.prefix</b> - string to be prepended to the value,</li>
 *     <li>
 *         <b>custom.claim.[claimName].source.authority</b> - string to be appended to the value, represents authority
 *         who has released the value
 *     </li>
 * </ul>
 *
 * @author Dominik Baránek <baranek@ics.muni.cz>
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class EntitlementSource extends GroupNamesSource {

	public static final Logger log = LoggerFactory.getLogger(EntitlementSource.class);

	private static final String GROUP = "group";

	protected static final String FORWARDED_ENTITLEMENTS = "forwardedEntitlements";
	protected static final String RESOURCE_CAPABILITIES = "resourceCapabilities";
	protected static final String FACILITY_CAPABILITIES = "facilityCapabilities";
	protected static final String PREFIX = "prefix";
	protected static final String AUTHORITY = "authority";

	private final String forwardedEntitlements;
	private final String resourceCapabilities;
	private final String facilityCapabilities;
	private final String prefix;
	private final String authority;
	private final String claimName;

	public EntitlementSource(ClaimSourceInitContext ctx) {
		super(ctx);
		this.claimName = ctx.getClaimName();
		this.forwardedEntitlements = ClaimUtils.fillStringPropertyOrNoVal(FORWARDED_ENTITLEMENTS, ctx);
		this.resourceCapabilities = ClaimUtils.fillStringPropertyOrNoVal(RESOURCE_CAPABILITIES, ctx);
		this.facilityCapabilities = ClaimUtils.fillStringPropertyOrNoVal(FACILITY_CAPABILITIES, ctx);
		this.prefix = ClaimUtils.fillStringPropertyOrNoVal(PREFIX, ctx);
		if (!ClaimUtils.isPropSet(this.prefix)) {
			throw new IllegalArgumentException(claimName + " - missing mandatory configuration option: " + PREFIX);
		}
		this.authority = ClaimUtils.fillStringPropertyOrNoVal(AUTHORITY, ctx);
		if (!ClaimUtils.isPropSet(this.authority)) {
			throw new IllegalArgumentException(claimName + " - missing mandatory configuration option: " + AUTHORITY);
		}
		log.debug("{} - forwardedEntitlements: '{}', resourceCapabilities: '{}', facilityCapabilities: '{}', " +
				"prefix: '{}', authority: '{}'", claimName, forwardedEntitlements, resourceCapabilities,
				facilityCapabilities, prefix, authority);
	}

	@Override
	public JsonNode produceValue(ClaimSourceProduceContext pctx) {
		PerunAdapter perunAdapter = pctx.getPerunAdapter();
		Long userId = pctx.getPerunUserId();
		Facility facility = pctx.getContextCommonParameters().getClient();
		Set<Group> userGroups = getUserGroupsOnFacility(facility, userId, perunAdapter);
		Set<String> entitlements = produceEntitlements(facility, userGroups, userId, perunAdapter);

		JsonNode result = convertResultStringsToJsonArray(entitlements);
		log.debug("{} - produced value for user({}): '{}'", claimName, userId, result);
		return result;
	}

	protected void fillCapabilities(Facility facility, PerunAdapter perunAdapter,
								  Map<Long, String> idToGnameMap, Set<String> entitlements) {
		Set<String> resultCapabilities = perunAdapter
				.getCapabilities(facility, idToGnameMap,
						ClaimUtils.isPropSet(this.facilityCapabilities) ? facilityCapabilities : null,
						ClaimUtils.isPropSet(this.resourceCapabilities)? resourceCapabilities: null);

		for (String capability : resultCapabilities) {
			entitlements.add(wrapCapabilityToAARC(capability));
			log.trace("{} - added capability: {}", claimName, capability);
		}
	}

	protected void fillForwardedEntitlements(PerunAdapter perunAdapter, Long userId, Set<String> entitlements) {
		PerunAttributeValue forwardedEntitlementsVal = perunAdapter
				.getUserAttributeValue(userId, this.forwardedEntitlements);
		if (forwardedEntitlementsVal != null && !forwardedEntitlementsVal.isNullValue()) {
			JsonNode eduPersonEntitlementJson = forwardedEntitlementsVal.valueAsJson();
			for (int i = 0; i < eduPersonEntitlementJson.size(); i++) {
				String entitlement = eduPersonEntitlementJson.get(i).asText();
				log.trace("{} - added forwarded entitlement: {}", claimName, entitlement);
				entitlements.add(entitlement);
			}
		}
	}

	private void fillEntitlementsFromGroupNames(Collection<String> groupNames, Set<String> entitlements) {
		for (String fullGname: groupNames) {
			if (fullGname == null || fullGname.trim().isEmpty()) {
				continue;
			}

			String[] parts = fullGname.split(":", 2);
			if (parts.length == 2 && StringUtils.hasText(parts[1]) && MEMBERS.equals(parts[1])) {
				parts[1] = parts[1].replace(MEMBERS, "");
			}

			String gname = parts[0];
			if (StringUtils.hasText(parts[1])) {
				gname += (':' + parts[1]);
			}
			String gNameEntitlement = wrapGroupNameToAARC(gname);
			log.trace("{} - added group name entitlement: {}", claimName, gNameEntitlement);
			entitlements.add(gNameEntitlement);
		}
	}

	protected Set<String> produceEntitlements(Facility facility, Set<Group> userGroups,
											  Long userId, PerunAdapter perunAdapter)
	{
		Set<String> entitlements = new TreeSet<>();
		Map<Long, String> groupIdToNameMap = super.getGroupIdToNameMap(userGroups, false);

		if (groupIdToNameMap != null && !groupIdToNameMap.values().isEmpty()) {
			this.fillEntitlementsFromGroupNames(new HashSet<>(groupIdToNameMap.values()), entitlements);
			log.trace("{} - entitlements for group names added", claimName);
		}

		if (facility != null) {
			this.fillCapabilities(facility, perunAdapter, groupIdToNameMap, entitlements);
			log.trace("{} - capabilities added", claimName);
		}

		if (ClaimUtils.isPropSet(this.forwardedEntitlements)) {
			this.fillForwardedEntitlements(perunAdapter, userId, entitlements);
			log.trace("{} - forwarded entitlements added", claimName);
		}

		return entitlements;
	}

	protected String wrapGroupNameToAARC(String groupName) {
		return addPrefixAndSuffix(GROUP + ':' + UrlEscapers.urlPathSegmentEscaper().escape(groupName));
	}

	private String wrapCapabilityToAARC(String capability) {
		return addPrefixAndSuffix(UrlEscapers.urlPathSegmentEscaper().escape(capability));
	}

	protected String addPrefixAndSuffix(String capability) {
		return prefix + capability + '#' + authority;
	}

}
