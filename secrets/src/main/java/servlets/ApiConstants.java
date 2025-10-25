package servlets;

/**
 * Constants for all API endpoints used in the auth application.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Utility class - prevent instantiation
    }
    
    // TEST
    public static final String TEST_URL = "/v1/test"; // GET
    
    // BACKEND
    public static final String BACKEND_GET_ALL_SECRETS     = "/v1/secrets/{projectSlug}/{env}"; // GET
    public static final String BACKEND_GET_SPECIFIC_SECRET = "/v1/secrets/{projectSlug}/{env}/{secretKey}"; // GET
    
    // PROJECTS
    public static final String PROJECTS_LIST_ALL = "/v1/projects"; // GET
    public static final String PROJECTS_CREATE   = "/v1/projects"; // POST
    public static final String PROJECTS_UPDATE   = "/v1/projects/{projectSlug}"; // PATCH
    public static final String PROJECTS_SUMMARY  = "/v1/projects/{projectSlug}/summary"; // GET
    
    // ENVIRONMENTS
    public static final String ENVS_LIST   = "/v1/projects/{projectSlug}/envs"; // GET
    public static final String ENVS_CREATE = "/v1/projects/{projectSlug}/envs"; // POST
    public static final String ENVS_UPDATE = "/v1/projects/{projectSlug}/envs/{env}"; // PATCH
    
    // SECRETS
    public static final String SECRETS_ADD     = "/v1/projects/{projectSlug}/envs/{env}/secrets"; // POST
    public static final String SECRETS_UPDATE  = "/v1/projects/{projectSlug}/envs/{env}/secrets/{key}"; // PUT
    public static final String SECRETS_LIST    = "/v1/projects/{projectSlug}/envs/{env}/secrets"; // GET
    public static final String SECRETS_GET_VAL = "/v1/projects/{projectSlug}/envs/{env}/secrets/{key}"; // GET
    public static final String SECRETS_DELETE  = "/v1/projects/{projectSlug}/envs/{env}/secrets/{key}"; // DELETE
    
    // API KEYS
    public static final String API_KEYS_LIST   = "/v1/projects/{projectSlug}/apikeys"; // GET
    public static final String API_KEYS_CREATE = "/v1/projects/{projectSlug}/apikeys"; // POST
	public static final String API_KEYS_REVOKE = "/v1/projects/{projectSlug}/apikeys/{keyId}"; // DELETE
	
	// HEALTH
	public static final String HEALTH_CHECK = "/v1/health"; // GET

}
