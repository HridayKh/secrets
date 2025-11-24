import { apiRequest } from './client';

export async function listEnvs(projectSlug) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/envs`,
		method: 'GET'
	});
}

export async function createEnv(projectSlug, name) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/envs`,
		method: 'POST',
		body: { name }
	});
}

export async function updateEnv(projectSlug, env, name) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/envs/${encodeURIComponent(env)}`,
		method: 'PATCH',
		body: { name }
	});
}

export async function deleteEnv(projectSlug, env) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/envs/${encodeURIComponent(env)}`,
		method: 'DELETE'
	});
}
