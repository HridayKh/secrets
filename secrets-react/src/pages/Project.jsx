import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProjectSummary } from '../api/projects';

export default function Project() {
	const { projectSlug } = useParams();
	const navigate = useNavigate();
	const [summary, setSummary] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);

	useEffect(() => {
		let mounted = true;
		async function load() {
			setLoading(true);
			setError(null);
			try {
				const res = await getProjectSummary(projectSlug);
				if (res && res.status === 401) {
					// Let caller handle redirects (could also navigate to login)
					setError('Unauthorized');
					return;
				}
				if (!res.ok) {
					throw new Error(res.message || `Request failed (${res.status})`);
				}
				if (mounted) setSummary((res.data && res.data) || res);
			} catch (err) {
				if (mounted) setError(err.message || String(err));
			} finally {
				if (mounted) setLoading(false);
			}
		}
		load();
		return () => {
			mounted = false;
		};
	}, [projectSlug]);

	return (
		<div className="bg-dark min-vh-100 text-light">
			<main className="container py-4">
				<button className="btn btn-secondary mb-3" onClick={() => navigate(-1)}>Back</button>
				{loading && <div className="spinner-border text-light" role="status"><span className="visually-hidden">Loading...</span></div>}
				{error && <div className="alert bg-black text-white">{error}</div>}
				{!loading && !error && summary && (
					<div className="card bg-black text-light">
						<div className="card-body">
							<h3 className="card-title">{summary.name || projectSlug}</h3>
							<p className="card-text text-secondary">{summary.description}</p>
							<h5>Environments</h5>
							<ul>
								{(summary.envs || []).map((e) => (
									<li key={e.name}>{e.name} — {Array.isArray(e.secrets) ? e.secrets.join(', ') : ''}</li>
								))}
							</ul>
						</div>
					</div>
				)}
			</main>
		</div>
	);
}
