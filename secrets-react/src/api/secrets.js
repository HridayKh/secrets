import { apiRequest } from './client';

const u = (projectSlug, env) => { `/v1/projects/${encodeURIComponent(projectSlug)}/envs/${encodeURIComponent(env)}/secrets` };

export async function listSecretKeys(projectSlug, env) {
	return apiRequest({
		path: u(projectSlug, env),
		method: 'GET'
	});
}

export async function addSecret(projectSlug, env, key, value) {
	return apiRequest({
		path: u(projectSlug, env),
		method: 'POST',
		body: { key, value }
	});
}

export async function getSecretValue(projectSlug, env, key) {
	return apiRequest({
		path: `${u(projectSlug, env)}/${encodeURIComponent(key)}`,
		method: 'GET'
	});
}

export async function updateSecret(projectSlug, env, key, value) {
	return apiRequest({
		path: `${u(projectSlug, env)}/${encodeURIComponent(key)}`,
		method: 'PUT',
		body: { value }
	});
}

export async function deleteSecret(projectSlug, env, key) {
	return apiRequest({
		path: `${u(projectSlug, env)}/${encodeURIComponent(key)}`,
		method: 'DELETE'
	});
}
