import { apiRequest } from './client';

/** Projects API helpers */
export async function listProjects() {
	return apiRequest({
		path: '/v1/projects',
		method: 'GET'
	});
}

export async function createProject({slug, name, description}) {
	return apiRequest({
		path: '/v1/projects',
		method: 'POST',
		body: { slug, name, description }
	});
}

export async function updateProject(projectSlug, { slug, name, description } = {}) {
	const body = {};
	if (slug !== undefined) body.slug = slug;
	if (name !== undefined) body.name = name;
	if (description !== undefined) body.description = description;

	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}`,
		method: 'PATCH',
		body
	});
}

export async function getProjectSummary(projectSlug) {
	return apiRequest({
		path: `/v1/projects/${encodeURIComponent(projectSlug)}/summary`,
		method: 'GET'
	});
}
