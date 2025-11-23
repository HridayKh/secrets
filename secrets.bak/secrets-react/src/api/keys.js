import { apiRequest } from './client';

export async function createApiKey(projectSlug, label) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/apiKeys`,
		method: 'POST',
		body: { label }
	});
}

export async function listApiKeys(projectSlug) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/apiKeys`,
		method: 'GET'
	});
}

export async function revokeApiKey(projectSlug, keyId) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/apiKeys/${encodeURIComponent(keyId)}`,
		method: 'DELETE'
	});
}
