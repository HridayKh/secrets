import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getProjectSummary, updateProject } from '../api/projects';
import { withPrefix } from '@/main';

export default function Project() {
	const { projectSlug } = useParams();
	const navigate = useNavigate();
	const [summary, setSummary] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	const [selectedEnvIdx, setSelectedEnvIdx] = useState(0);

	useEffect(() => {
		let mounted = true;
		async function load() {
			// setSummary(JSON.parse("{\"name\":\"Mobile API\",\"description\":\"Backend for mobile clients\",\"envs\":[{\"name\":\"development\",\"secrets\":[\"API_KEY\",\"DATABASE_URL\"]},{\"name\":\"production\",\"secrets\":[\"API_KEY\",\"DATABASE_URL\"]},{\"name\":\"staging\",\"secrets\":[\"API_KEY\",\"DATABASE_URL\"]}],\"id\":2,\"message\":\"Project summary fetched successfully.\",\"type\":\"success\",\"slug\":\"mobile-api\"}"));
			try {
				const res = await getProjectSummary(projectSlug);
				if (res && res.status === 401) {
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
		setLoading(true);
		setError(null);
		load();
		return () => {
			mounted = false;
		};
	}, [projectSlug]);


	async function handleEditField(field, currentValue) {
		const newValue = window.prompt(`Enter new ${field}:`, currentValue || "");
		if (newValue === null || newValue === currentValue) return;
		try {
			const updateObj = { [field]: newValue };
			const res = await updateProject(projectSlug, updateObj);
			if (res && res.ok) {
				setSummary((prev) => ({ ...prev, [field]: newValue }));
			} else {
				window.alert(res && res.message ? res.message : 'Update failed');
			}
		} catch (err) {
			window.alert(err.message || String(err));
		}
	}

	return (
		<div className="bg-dark min-vh-100 text-light">
			<main className="container py-4">
				<Link to={withPrefix('/')} className="btn btn-secondary mb-3">Back</Link>
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
							<div className="tab-content bg-dark p-3 rounded border border-secondary">
								<div className="tab-pane active show">
									{summary.envs && summary.envs.length > 0 ? (
										<>
											<div><b>Name:</b> {summary.envs[selectedEnvIdx]?.name}</div>
											<div><b>Secrets:</b> {Array.isArray(summary.envs[selectedEnvIdx]?.secrets) ? summary.envs[selectedEnvIdx].secrets.join(', ') : ''}</div>
										</>
									) : (
										<span className="text-secondary">No environments found.</span>
									)}
								</div>
							</div>
						</div>
					</div>
				)}
			</main>
		</div>
	);
}
