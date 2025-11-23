import React, { useEffect, useState } from 'react';
import { listProjects, createProject } from '../api/projects';
import { useNavigate } from 'react-router-dom';
import { withPrefix } from '@/main';

const loginUrlBase = (import.meta.env.VITE_AUTH_BACKEND || 'https://auth.HridayKh.in') + '/login?redirect=';

export default function Projects() {
	const [projects, setProjects] = useState([]);
	const [creating, setCreating] = useState(false);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	
	useEffect(() => {
		let mounted = true;

		async function load() {
			setLoading(true);
			setError(null);
			try {
				const res = await listProjects();

				if (res && res.status === 401) {
					const redirectTo = window.location.href;
					window.location.href = loginUrlBase + encodeURIComponent(redirectTo);
					return;
				}

				if (res && res.status === 403) {
					throw new Error("Request failed (403 Forbidden) <br/> " + (res.message || "Not authorized to access this app."));
				}

				if (!res.ok) {
					// show backend message or generic
					throw new Error(res.message || `Request failed (${res.status})`);
				}

				if (mounted) setProjects((res.data && res.data.projects) || res.projects || []);
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
	}, []);

	const navigate = useNavigate();

	return (
		<div className="bg-dark min-vh-100 text-light">
			<main className="container py-4">
				<h1 className="mt-3 mb-4 text-center text-white py-2 rounded">HridayKh.in Secrets</h1>

				{loading && (
					<div className="d-flex justify-content-center my-5">
						<div className="spinner-border text-light" role="status">
							<span className="visually-hidden">Loading...</span>
						</div>
					</div>
				)}

				{error && (
					<div className="alert bg-black text-white border-0" role="alert" dangerouslySetInnerHTML={{ __html: error }}></div>
				)}

				{!loading && !error && (
					<div className="row g-3">
						{projects.length === 0 && (
							<div className="col-12">
								<div className="alert bg-black text-white border-0">No projects found.</div>
							</div>
						)}

						{projects.map((p) => (
							<div key={p.id} className="col-12 col-sm-6 col-md-4">
								<div
									className="text-center card h-100 bg-black text-light border-secondary"
									role="button"
									tabIndex={0}
									style={{ cursor: 'pointer' }}
									onClick={() => navigate(withPrefix(`/${p.slug}`))}
									onKeyDown={(e) => {
										if (e.key === 'Enter' || e.key === ' ') {
											navigate(withPrefix(`/${p.slug}`));
										}
									}}
								>
									<div className="card-body">
										<h5 className="card-title text-white">{p.name}</h5>
										<h6 className="card-subtitle mb-2 text-white-50">{p.slug}</h6>
										<p className="card-text">{p.description}</p>
									</div>
								</div>
							</div>
						))}

						{/* Hardcoded card to create a new project */}
						<div className="col-12 col-sm-6 col-md-4">
							<div className="card h-100 bg-black text-light border-success">
								<div className="card-body d-flex flex-column justify-content-center align-items-center">
									<h5 className="card-title text-white">Create Project</h5>
									<button
										className="btn btn-outline-success"
										onClick={async () => {
											const name = prompt('Project name:');
											if (!name) return;
											let slug = prompt('Project slug (URL-friendly):', name.toLowerCase().replace(/\s+/g, '-'));
											if (!slug) return;
											const description = prompt('Description:', '');
											if (!description) return;
											setCreating(true);
											try {
												const res = await createProject({ slug, name, description });
												if (!res.ok) {
													setError(res.message || 'Failed to create project');
													return;
												}
												// refresh list
												const refreshed = await listProjects();
												if (refreshed && refreshed.ok) {
													setProjects((refreshed.data && refreshed.data.projects) || refreshed.projects || []);
												}
											} finally {
												setCreating(false);
											}
										}}
									>
										{creating ? 'Creating a New Project...' : 'Create a New Project'}
									</button>
								</div>
							</div>
						</div>
					</div>
				)}
			</main>
		</div>
	);
}



