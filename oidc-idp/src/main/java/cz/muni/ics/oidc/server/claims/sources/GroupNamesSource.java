package cz.muni.ics.oidc.server.claims.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import cz.muni.ics.oidc.models.Facility;
import cz.muni.ics.oidc.models.Group;
import cz.muni.ics.oidc.server.adapters.PerunAdapter;
import cz.muni.ics.oidc.server.claims.ClaimSource;
import cz.muni.ics.oidc.server.claims.ClaimSourceInitContext;
import cz.muni.ics.oidc.server.claims.ClaimSourceProduceContext;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Source fetches all unique group names in context of user and facility. If no facility exists for the client, empty
 * list is returned as result.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class GroupNamesSource extends ClaimSource {

	public static final Logger log = LoggerFactory.getLogger(GroupNamesSource.class);

	protected static final String MEMBERS = "members";
	private final String claimName;

	public GroupNamesSource(ClaimSourceInitContext ctx) {
		super(ctx);
		this.claimName = ctx.getClaimName();
		log.debug("{} - initialized", claimName);
	}

	@Override
	public JsonNode produceValue(ClaimSourceProduceContext pctx) {
		Map<Long, String> idToNameMap = this.produceGroupNames(pctx);
		JsonNode result = convertResultStringsToJsonArray(new HashSet<>(idToNameMap.values()));
		log.debug("{} - produced value for user({}): '{}'", claimName, pctx.getPerunUserId(), result);
		return result;
	}

	protected Map<Long, String> produceGroupNames(ClaimSourceProduceContext pctx) {
		log.trace("{} - produce group names with trimming 'members' part of the group names", claimName);
		Facility facility = pctx.getContextCommonParameters().getClient();
		Set<Group> userGroups = getUserGroupsOnFacility(facility, pctx.getPerunUserId(), pctx.getPerunAdapter());
		return getGroupIdToNameMap(userGroups, true);
	}

	protected Map<Long, String> getGroupIdToNameMap(Set<Group> userGroups, boolean trimMembers) {
		Map<Long, String> idToNameMap = new HashMap<>();
		userGroups.forEach(g -> {
			String uniqueName = g.getUniqueGroupName();
			if (trimMembers && StringUtils.hasText(uniqueName) && MEMBERS.equals(g.getName())) {
				uniqueName = uniqueName.replace(':' + MEMBERS, "");
				g.setUniqueGroupName(uniqueName);
			}

			idToNameMap.put(g.getId(), g.getUniqueGroupName());
		});

		log.trace("{} - group ID to group name map: '{}'", claimName, idToNameMap);
		return idToNameMap;
	}

	protected Set<Group> getUserGroupsOnFacility(Facility facility, Long userId, PerunAdapter perunAdapter) {
		Set<Group> userGroups = new HashSet<>();
		if (facility == null) {
			log.warn("{} - no facility provided when searching for user groups, will return empty set", claimName);
		} else {
			userGroups = perunAdapter.getGroupsWhereUserIsActiveWithUniqueNames(facility.getId(), userId);
		}
		log.trace("{} - found user groups: '{}'", claimName, userGroups);
		return userGroups;
	}

	protected JsonNode convertResultStringsToJsonArray(Collection<String> collection) {
		ArrayNode arr = JsonNodeFactory.instance.arrayNode();
		collection.forEach(arr::add);
		return arr;
	}

}
