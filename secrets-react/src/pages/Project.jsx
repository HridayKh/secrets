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
			setLoading(true);
			setError(null);
			setSummary(JSON.parse("{\"name\":\"Mobile API\",\"description\":\"Backend for mobile clients\",\"envs\":[{\"name\":\"development\",\"secrets\":[\"API_KEY\",\"DATABASE_URL\"]},{\"name\":\"production\",\"secrets\":[\"API_KEY\",\"DATABASE_URL\"]},{\"name\":\"staging\",\"secrets\":[\"API_KEY\",\"DATABASE_URL\"]}],\"id\":2,\"message\":\"Project summary fetched successfully.\",\"type\":\"success\",\"slug\":\"mobile-api\"}"));
			setLoading(false);
			// try {
			// 	const res = await getProjectSummary(projectSlug);
			// 	if (res && res.status === 401) {
			// 		setError('Unauthorized');
			// 		return;
			// 	}
			// 	if (!res.ok) {
			// 		throw new Error(res.message || `Request failed (${res.status})`);
			// 	}
			// 	if (mounted) setSummary((res.data && res.data) || res);
			// } catch (err) {
			// 	if (mounted) setError(err.message || String(err));
			// } finally {
			// 	if (mounted) setLoading(false);
			// }
		}
		load();
		return () => {
			mounted = false;
		};
	}, [
		// projectSlug
	]);


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
							<div className="d-flex align-items-center mb-2">
								<h3 className="card-title mb-0 me-2">{summary.name || projectSlug}</h3>
								<button className="btn btn-sm btn-outline-info me-2" title="Edit Name" onClick={() => handleEditField('name', summary.name)}>
									Edit
								</button>
							</div>
							<div className="d-flex align-items-center mb-2">
								<span className="me-2 text-secondary">{summary.description}</span>
								<button className="btn btn-sm btn-outline-info me-2" title="Edit Description" onClick={() => handleEditField('description', summary.description)}>
									Edit
								</button>
							</div>
							<div className="d-flex align-items-center mb-2">
								<span className="me-2 text-secondary">Slug: {summary.slug || projectSlug}</span>
								<button className="btn btn-sm btn-outline-info me-2" title="Edit Slug" onClick={() => handleEditField('slug', summary.slug)}>
									Edit
								</button>
							</div>

							{/* Environments as tabs */}
							<ul className="nav mb-3 mt-5">
								<li className=" border-bottom p-0 m-0 opacity-50" style={{ minWidth: 0, flexGrow: 5 }}></li>
								{(summary.envs || []).map((env, idx) => (<>
									<li className={"d-flex align-items-center p-1 m-0 rounded-top border " + (idx === selectedEnvIdx ? "border-bottom-0" : "opacity-50")} key={env.name}>
										<button
											className="btn-dark btn rounded-0 m-0 px-1 py-0 rounded-top rounded-end-0"
											type="button"
											onClick={() => setSelectedEnvIdx(idx)}
										>
											{env.name}
										</button>
										<button className="btn-info btn rounded-0 m-0 px-1 py-0" title="Edit Env">(e)</button>
										<button className="btn-danger btn rounded-0 m-0 px-1 py-0 rounded-top rounded-start-0" title="Delete Env">(d)</button>
									</li>
									<li className="border-bottom p-0 m-0 opacity-50" style={{ minWidth: 0, flexGrow: 1 }}></li>
								</>))}
								<li className="d-flex align-items-center p-1 m-0" style={{ borderBottom: '1px solid rgba(255,255,255,0.5)' }}>
									<button className="btn btn-sm btn-outline-success m-0" title="Add Environment">+</button>
								</li>
								<li className="border-bottom p-0 m-0 opacity-50" style={{ minWidth: 0, flexGrow: 5 }}></li>
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
