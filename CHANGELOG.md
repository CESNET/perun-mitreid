# Change Log
All notable changes to this project will be documented in this file.

## [v2.4.1]
### Fixed
- fixed NullPointerException in searching for facility without clientID

## [v2.4.0]
### Added
- Added custom view for logout confirmation page
- empty claims are filtered out instead of producing null values
- added custom views for device code flow related screens
- added support for MFA in device code flow
- added claim source for extended entitlements
### Changed
- refactored RPC mapper
- modified filter utility method checking giveni URL to check only for null or empty and not try to put protocol as well
- make use of contact.email property in UNAPPROVED view when no contact is available for client
- extended GA4GH claim source to support multliple configurable headers
- removed "large" types of perun attributes as done in Perun
- refactored logging (for claim scopes, modifiers and filters)
### Fixed
- fixed calling wrong methods in attributes fetching of RPC adapter
- fixed mapping of user: the firstName field is optional
- fixed fetching and comparing the "members" group in RPC adapter
### Important changes 
- configuration for each claimSource needs to explicitly have the class in the properties file
- if no class is set in configuration for claimValueModifier, default NoOperation is used
 
## [v2.3.0]
### Added
- added StaticValueClaimSource
### Changed
- Allow using Perun RPC serializer from the configuration. Default value is 'json'. 
- In userInfo, filter out empty claims. If scope does not have any claims in result, filter it out as well. 

## [v2.2.0]
### Added
- added possibility to specify, under which scopes being null the userInfo object gets generated again instead of being read from cache
### Changed
- set pool size for DB to 50 connections max (previously 10)
- refactored scheduled tasks - simpler SQL statements, run less often
### Fixed
- fixed some little bugs and missing things for logging purposes
- minimized time window under which the nodes could execute scheduled tasks concurrently

## [v2.1.1]
### Fixed
- fixed bug when redirecting to the registration (no return after redirect has been called, user ended on unapproved no matter what)

## [v2.1.0]
### Added
- logging at TRACE level via the Aspects
- implemented new filter which decides if user can access the service or not
  - no matter if service has requested check, this filter is run always if configured
- logging at TRACE level via the Aspects 
### Changed
- refactored AttributeMappingsService
- appender for each type of logging (syslog/file/roll_file) which logs only the trace level via the Aspects is now defined
- Refactored logback configuration, see README for options 
- appender for each type of logging (syslog/file/roll_file) which logs only the trace level via the Aspects is now defined
- optimized a bit procedure in LDAP for getting resource capabilities
- optimized filters - pass some things in parameters
- refactored PerunAttribute and value
### Fixed
- Fixed missing mapping for the attribute "capabilities" in facility
- Fixed InconvertibleValueException in AUP filter 
- fixed generating of facility capabilities, now if the user is not in any group assigned to facility, he/she will not receive facility capability 

## [2.0.0]
### Added
- added claim source for generating EPSA on MU
- added missing dependencies for newer JDKs
- added new LDAP connector options (SSL, TLS, ...)
- added support for MDC (MessageDiagnosticContext) logging
- added full support for LDAP connector
### Changed
- Changed the format of log messages to include session ID instead of thread ID
- refactored structure of connectors, adapters, etc...
- Updated configuration templates
- Fixed generating redirectURI NullPointerException
- Fixed group membership constructing
### Fixed
- Fixed a bug for two different URLs to be equal in HashMap (ga4gh scope configuration)
- Fixed a bug in generating expired visas (ga4gh scope)
- Fixed README

## [v1.25.2]
### Added
- added support for MDC (MessageDiagnosticContext) logging
### Changed
- Changed format of log messages to include session ID instead of thread ID

## [v1.25.1]
### Fixed
- Fixed bug for two different URLs to be equal in HashMap (ga4gh scope configuration)

## [v1.25.0]
### Added
- Added support for ShedLock in scheduled tasks
- Added DB table for ShedLock, see SQL scripts for the table definition that needs to be created
- Added claim source producing groupNames
- Added redirection of logged user to unapproved in case the user representation cannot be found in Perun
- Added logging to the specified file, rolling_file or syslog
- Added facility capabilities to Entitlement source
### Changed
- Overridden calling of scheduled task from MitreID with our custom class
- Changed property names for specifying custom claims (see configuration template for required format)
- UserInfo modifiers are now loaded only at the startup, previously were loaded for each modification separately
- Changed EntitlementSource, if forwardedEntitlements attribute name is not specified, the forwarded entitlements will not be added to the list
- Modified EntitlementSource to extend GroupNamesSource, removed groupNames attribute from its' configuration options
- Changed logging destinations
- Optimized Entitlement source
### Fixed
- When used in clustered environment, running scheduled task caused DeadLocks to appear in DB
- Fixed possible null pointer exceptions and wrong behavior for FilterEduPersonEntitlement UserInfo modifier
- Fixed typo in database table for Shedlock (column 'ocked_at' changed to 'locked_at')

## [v1.24.2]
### Changed
- GA4GH configuration for signers and repositories from specific YAML file. See configuration templates for such file

## [v1.24.1]
### Added
- Added service name to the consent header
### Changed
- Removed reference to CERIT-SC from CESNET footer

## [v1.24.0]
### Added
- Added option to read classes for HTML pages from file
- Implemented request filter - isCesnetEligible (see class documentation for more info)
- Created an unapproved page that displays passed texts
### Changed
- changed the implementation of GA4GHClaimSource because the EGA Permissions service
  changed its API to be GA4GH-compatible
- Changed logging configuration - log all classes at the level specified while building
- Consent uses the loading of HTML classes from the file for some elements
- Updated templates for consent page
- Moved logs of user logins into the statistics filter, removed userID from it
- Changed handling of user logins
- Refactored whole logic of connecting to the sources of data (Perun, LDAP)
- Removed ProxyIDP header from unapproved page
- Modified ProxyStatisticsFilter to respond to new version of SimpleSAMLphp perun statistics
- Changed loading of JS files due to content-security policy
- Changed database table [acrs](https://github.com/CESNET/perun-mitreid/blob/release_1_24/oidc-idp/src/main/webapp/WEB-INF/classes/db/mysql/acrs.sql): `ALTER TABLE  acrs MODIFY COLUMN expiration BIGINT;`
### Fixed
- Fixed handling of group "members" in resource capabilities
- Fixed wrong behavior of MFA forced logout
- Fixed wrong loading of SAML logout URL when specified in properties
- Fixed wrong handling of date and time because of ignoring time zone (ACR expiration)
- Fixed bad rendering of registration pages (registration into groups when authorization has failed) - double service name in heading
- Fixed validation of custom registration URL in PerunAuthorizationFilter

## [v1.23.0]
### Changed
- Updated MUNI template
- Updated sample configuration template file
- Updated proxy.\* properties to full paths
- Refactored valuseAs...() methods in PerunAttribute to return empty collections rather than null objects
### Fixed
- Fixed another bug in resource capabilities - wrong prefixing - configuration changes needed!
### Added
- Added property to specify SAML URL - used as path to CSS files from Proxy

## [v1.22.2]
### Fixed
- Fixed bug in remember consent functionality - view did not send the parameter

## [v1.22.1]
### Fixed
- Fixed bug in recourceCapabilities - thrown NullPointerException when obtained attribute has been null

## [v1.22.0]
### Changed
- Localization files are now read as UTF-8 (no escaping of accented chars needed in files)
### Fixed
- Bugfixes in PerunAuthenticationFilter and PerunAUPFilter
- Fixes in frontend
### Added
- Added detailed statistics
- Added resource capabilities support
  - !! CLASS JoinGroupNamesAndEduPersonEntitlementSource.java has been renamed to EntitlementSource.java !!
  - scope, which uses above-mentioned source, requires new configuration - custom.claim.[claimName].capabilities

## [v1.21.0]
### Changed
- Updated implementation of the GA4GH claim
- Refactored calling of custom security filters
- Refactored templating in custom JSPs
### Fixed
- Fixed error in db_updated script for MySQL
- Fixed bug in the language bar on custom pages - bug caused generating addresses with weirdly formatted parameter
### Added
- Added PerunUserInfo modifiers - provides capabilities to modify UserInfo before it is released to the end service
- Added AUP security filter - will enforce user to agree with AUP
- Added template for Masaryk University
- Added configuration files templates

## [v1.20.0]
### Changed
- Refactored GA4GH claim source
- Refactored localization for custom pages
### Fixed
- Fixed exception in SQL for storing too long request parameter - need an update of the DB schemas!
- Fixed logout confirmation - clicking NO will redirect user back to the service
### Added
- Added custom template for EuroPDX
- Added option to specify claims included in id_token
- Added CODEOWNERS
- Implemented passing ACR from Shibboleth to OIDC
- Added support for processing ACR_VALUES param in authorization requests
- Added logging of user logins

## [v1.19.0]
### Changed
- GA4GH affiliations expiration is now based on lastAccess
### Fixed
- Fixed path to the CSS files in header.tag
### Added
- Added ClaimSources ExtractValuesByDomainSource and JoinGroupsAndEduPersonEntitlement

## [v1.18.0]
### Changed
- Replaced PerunUserInfoRepository with PerunUserInfoService, which can produce different UserInfo for the same user but different clients
- Removed calling of old ELIXIR Permissions API
- Renamed DatasetPermissionsAccessTokenModifier to ElixirAccessTokenModifier
### Added
- Added HTTP connection pooling to calling Perun RPC
- Added ClaimSource interface for classes that can produce arbitrary values for claims
- Added PerunAttributeClaimSource that gets claim value from selected user's attribute loaded from Perun
- Added GA4GHClaimSource producing the "ga4gh" claim, it calls the new ELIXIR Permissions API
- Added all claims to ID tokens (claims are now available in ID token, /userinfo and /introspection endpoints)
- Added SAML logout - after the end of OIDC session and before redirecting to a client-specified URL, SAML logout is done

## [v1.17.0]
### Changed
- Changed dependency on a fixed version of MITREid declaring release of refresh_token
- Refactored project structure
- Rebased on MITREid 1.3.3
- Updated consent page
### Fixed
- Minor bugfixes
### Added
- Added new access control filters
- Implemented forwarding Identity Provider filters

## [v1.16.1]
### Changed
- Changed dependency on MITREid version with declared release of refresh_token

## [v1.16.0]
### Changed
- Updated mapping of resources in consent template
- Updated proxyStatistics filter
- Updated project README
### Added
- Added support for build parameters

## [v1.15.1]
### Changed
- Scopes and claims at the consent page are rendered in sorted manner
### Fixed
- Swapped middle_name and family_name claims
### Added
- Added texts for consent page for scopes permissions_rems and eduPersonEntitlement

## [v1.15.0]
### Changed
- PerunConnector methods return enities in format of models
- enabled allowCompleteDeviceCodeUri property
### Fixed
- Fixed getAttributeValue to not return "null" as a String value
### Added
- Added configurable names for attributes with IdP name and id to statistics filter
- Added model items for entities
- Implemented LDAP connector
- Added REMS dataset permissions

## [v1.14.0]
### Changed
- Proxystatistics filter stores identifier of SP/IdP as addition to the name of SP/IdP
- Storing name of the SP/IdP into mapping table
### Fixed
- Fixed problem with getting attributeValue with UTF-8 characters from the request

## [v1.13.0]
### Changed
- Logging using syslog
### Fixed
- Fixed problem, when more attributes of user have been read from one source, value has been modified by CustomClaimModifier, change has propagated ino both attributes
### Added
- Added option to store statistics about each log in

## [v1.12.0]
### Added
- Added framework for claim value modifiers
- Added claim value modifier GroupNamesAARCFormatModifier for converting claim "groupNames" into AARC format
- Added claim value modifier AppendModifier for appending domain to claim "sub"
- Added claim value modifier RegexReplaceModifier just in case it might be useful for simple conversions

## [v1.11.0]
First release of modified MITREid server which uses Shibboleth for authentication and reads user data from Perun system. It also has support for new scopes and claims, for extension of released access tokens, and for releasing user claims from introspection endpoint.

[Unreleased]: https://github.com/CESNET/perun-mitreid/tree/master
[v2.4.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v2.4.0
[v2.3.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v2.3.0
[v2.2.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v2.2.0
[v2.1.1]: https://github.com/CESNET/perun-mitreid/releases/tag/v2.1.1
[v2.1.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v2.1.0
[v2.0.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v2.0.0
[v1.25.2]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.25.2
[v1.25.1]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.25.1
[v1.25.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.25.0
[v1.24.2]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.24.2
[v1.24.1]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.24.1
[v1.24.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.24.0
[v1.23.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.23.0
[v1.22.2]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.22.2
[v1.22.1]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.22.1
[v1.22.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.22.0
[v1.21.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.21.0
[v1.20.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.20.0
[v1.19.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.19.0
[v1.18.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.18.0
[v1.17.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.17.0
[v1.16.1]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.16.1
[v1.16.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.16.0
[v1.15.1]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.15.1
[v1.15.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.15.0
[v1.14.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.14.0
[v1.13.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.13.0
[v1.12.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.12.0
[v1.11.0]: https://github.com/CESNET/perun-mitreid/releases/tag/v1.11.0
