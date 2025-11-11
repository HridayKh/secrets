package servlets;

/**
 * Constants for all API endpoints used in the auth application.
 */
public final class ApiConstants {

	// TEST
	public static final String TEST_URL = "/v1/test"; // GET
	// PROJECTS
	public static final String PROJECTS_LIST_ALL = "/v1/projects"; // GET

	// BACKEND
	public static final String BACKEND_GET_ALL_SECRETS = "/v1/secrets/{env}"; // GET

	// PROJECTS
	public static final String PROJECTS_CREATE = "/v1/projects"; // POST
	public static final String PROJECTS_UPDATE = "/v1/projects/{projectSlug}"; // PATCH
	public static final String PROJECTS_SUMMARY = "/v1/projects/{projectSlug}/summary"; // GET

	// ENVIRONMENTS
	public static final String ENVS_LIST = "/v1/projects/{projectSlug}/envs"; // GET
	public static final String ENVS_CREATE = "/v1/projects/{projectSlug}/envs"; // POST
	public static final String ENVS_UPDATE = "/v1/projects/{projectSlug}/envs/{env}"; // PATCH
	public static final String ENVS_DELETE = "/v1/projects/{projectSlug}/envs/{env}"; // DELETE

	// SECRETS
	public static final String SECRETS_KEYS = "/v1/projects/{projectSlug}/envs/{env}/secrets"; // GET
	public static final String SECRETS_ADD = "/v1/projects/{projectSlug}/envs/{env}/secrets"; // POST
	public static final String SECRETS_GET_VAL = "/v1/projects/{projectSlug}/envs/{env}/secrets/{key}"; // GET
	public static final String SECRETS_UPDATE = "/v1/projects/{projectSlug}/envs/{env}/secrets/{key}"; // PUT
	public static final String SECRETS_DELETE = "/v1/projects/{projectSlug}/envs/{env}/secrets/{key}"; // DELETE

	// API KEYS
	public static final String API_KEYS_LIST = "/v1/projects/{projectSlug}/apiKeys"; // GET
	public static final String API_KEYS_CREATE = "/v1/projects/{projectSlug}/apiKeys"; // POST
	public static final String API_KEYS_REVOKE = "/v1/projects/{projectSlug}/apiKeys/{keyId}"; // DELETE

	// HEALTH
	public static final String HEALTH_CHECK = "/v1/health"; // GET

	private ApiConstants() {
		// Utility class - prevent instantiation
	}

}
