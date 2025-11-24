const API_BASE_URL = import.meta.env.VITE_SECRETS_BACKEND || 'https://api.HridayKh.in/secrets';

export async function apiRequest({ path, method = 'GET', body, headers = {} }) {
	const url = API_BASE_URL + path;
	const fetchOptions = {
		method,
		headers: {
			'Content-Type': 'application/json',
			...headers,
		},
		credentials: 'include',
	};
	if (body) fetchOptions.body = JSON.stringify(body);

	let response;
	try {
		response = await fetch(url, fetchOptions);
	} catch (err) {
		return { ok: false, status: 0, type: 'error', message: 'Network error' };
	}

	let data = null;
	try {
		data = await response.json();
	} catch (e) { }

	// Just return backend's type/message/data, always
	return {
		ok: response.ok,
		status: response.status,
		type: data && data.type,
		message: data && data.message,
		data,
	};
}

export const env = {
	API_BASE_URL
}
