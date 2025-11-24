import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getProjectSummary, updateProject } from '../api/projects';
import { withPrefix } from '../main.jsx';
import { getSecretValue } from '../api/secrets.js';

export default function Project() {
	const { projectSlug } = useParams();
	const [summary, setSummary] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	const [selectedEnvIdx, setSelectedEnvIdx] = useState(0);
	const loginUrlBase = (import.meta.env.VITE_AUTH_BACKEND || 'https://auth.HridayKh.in') + '/login?redirect=';

	useEffect(() => {
		async function load() {
			const res = await getProjectSummary(projectSlug);
			if (res && res.status === 401) {
				const redirectTo = window.location.href;
				window.location.href = loginUrlBase + encodeURIComponent(redirectTo) + '&type=error&msg=Please+login+to+access+the+project!';
				return;
			}

			if (res && res.status === 403) {
				throw new Error("Request failed (403 Forbidden) <br/> " + (res.message || "Not authorized to access this app."));
			}

			if (!res.ok) {
				setError(res.message || `Request failed (${res.status})`);
				setLoading(false);
				return;
			}
			setSummary((res.data && res.data) || res);
			// setSummary(JSON.parse(`{"name":"Mobile API","description":"Backend for mobile clients","envs":[{"name":"a","secrets":[]},{"name":"aa","secrets":[]},{"name":"development","secrets":["API_KEY","DATABASE_URL"]},{"name":"production","secrets":["API_KEY","DATABASE_URL"]},{"name":"staging","secrets":["API_KEY","DATABASE_URL"]}],"id":2,"message":"Project summary fetched successfully.","type":"success","slug":"mobile-api"}`));
			setLoading(false);
		}
		load();
	}, [projectSlug]);

	async function handleEditEnv() {
		const env = summary.envs[selectedEnvIdx];
		const newName = window.prompt('Edit environment name:', env.name);
		if (!newName || newName === env.name) return;
		try {
			const { updateEnv } = await import('../api/envs');
			const res = await updateEnv(projectSlug, env.name, newName);
			if (!res.ok) {
				window.alert(res.message || 'Failed to update environment');
				return;
			}
			setSummary((prev) => ({
				...prev,
				envs: prev.envs.map((e, i) => i === selectedEnvIdx ? { ...e, name: newName } : e)
			}));
		} catch (err) {
			window.alert(err.message || String(err));
		}
	}

	async function handleDeleteEnv() {
		const env = summary.envs[selectedEnvIdx];
		if (!window.confirm(`Delete environment "${env.name}"? This cannot be undone.`)) return;
		try {
			const { deleteEnv } = await import('../api/envs');
			const res = await deleteEnv(projectSlug, env.name);
			if (!res.ok) {
				window.alert(res.message || 'Failed to delete environment');
				return;
			}
			setSummary((prev) => {
				const newEnvs = prev.envs.filter((_, i) => i !== selectedEnvIdx);
				return { ...prev, envs: newEnvs };
			});
			setSelectedEnvIdx(0);
		} catch (err) {
			window.alert(err.message || String(err));
		}
	}

	async function handleAddEnv() {
		const name = window.prompt('Environment name:');
		if (!name) return;
		try {
			const { createEnv } = await import('../api/envs');
			const res = await createEnv(projectSlug, name);
			if (!res.ok) {
				window.alert(res.message || 'Failed to create environment');
				return;
			}
			setSummary((prev) => ({
				...prev,
				envs: [...(prev.envs || []), { name, secrets: [] }]
			}));
			setSelectedEnvIdx((prevIdx) => ((summary?.envs?.length ?? 0)));
		} catch (err) {
			window.alert(err.message || String(err));
		}
	}

	async function handleEditField(field, currentValue) {
		const newValue = window.prompt(`Enter new ${field}:`, currentValue || "");
		if (newValue === null || newValue === currentValue) return;
		try {
			const updateObj = { [field]: newValue };
			const res = await updateProject(projectSlug, updateObj);
			if (res && res.ok) {
				setSummary((prev) => ({ ...prev, [field]: newValue }));
				if (field === 'slug') {
					window.history.replaceState(null, '', withPrefix(`/${newValue}`));
				}
			} else {
				window.alert(res && res.message ? res.message : 'Update failed');
			}
		} catch (err) {
			window.alert(err.message || String(err));
		}
	}

	async function handleCreateSecret() {
		const env = summary.envs[selectedEnvIdx];
		const secretName = window.prompt('Enter new secret name:');
		if (!secretName) return;
		const secretValue = window.prompt('Enter new secret value:');
		if (!secretValue) return;
		try {
			const { addSecret } = await import('../api/secrets');
			const res = await addSecret(projectSlug, env.name, secretName, secretValue);
			if (!res.ok) {
				window.alert(res.message || 'Failed to create secret');
				return;
			}
			setSummary((prev) => {
				const envs = prev.envs.map((e, i) =>
					i === selectedEnvIdx
						? { ...e, secrets: [...(e.secrets || []), secretName] }
						: e
				);
				return { ...prev, envs };
			});
		} catch (err) {
			window.alert(err.message || String(err));
		}
	}

	return (
		<div className="bg-dark text-light min-vh-100 min-vw-100">

			<Link to={withPrefix('/')} className="btn btn-secondary mt-3 ms-3">Back</Link>

			{loading && <div className="spinner-border text-light" role="status"><span className="visually-hidden">Loading...</span></div>}
			{error && <div className="alert bg-black text-white">{error}</div>}
			{!loading && !error && summary && (
				<div className="bg-black text-light p-4 rounded mt-3 mx-3">

					{/* Project Name */}
					<div className="row align-items-center justify-content-center">
						<h1 className="col-auto text-center m-0">{summary.name || projectSlug}</h1>
						<button className="col-auto btn btn-sm btn-outline-info" title="Edit Name" onClick={() => handleEditField('name', summary.name)}>
							Edit
						</button>
					</div>

					{/* Project Description */}
					<div className="row align-items-center justify-content-center">
						<span className="col-auto text-secondary text-center m-0">{summary.description}</span>
						<button className="col-auto btn btn-sm btn-outline-info py-0 px-1 m-0" title="Edit Description" onClick={() => handleEditField('description', summary.description)}>
							Edit
						</button>
					</div>

					{/* Project Slug */}
					<div className="row align-items-center justify-content-center">
						<span className="col-auto text-secondary text-center m-0">Slug: {summary.slug || projectSlug}</span>
						<button className="col-auto btn btn-sm btn-outline-info py-0 px-1 m-0" title="Edit Slug" onClick={() => handleEditField('slug', summary.slug)}>
							Edit
						</button>
					</div>

					{/* Project Tabs */}
					<ul className="nav mt-3">

						<li className=" border-bottom p-0 m-0 opacity-50" style={{ minWidth: 0, flexGrow: 5 }} key="tabs-space-start"></li>

						{/* Tabs */}
						{(summary.envs || []).map((env, idx) => (<>
							<li className={" align-items-center p-0 m-0 rounded-top border p-0 m-0 " + (idx === selectedEnvIdx ? "bg-dark border-bottom-0" : "opacity-50")} key={env.name}>
								<button
									className="btn-dark btn rounded rounded-bottom-0 bold fw-bolder fs-6"
									type="button"
									onClick={() => setSelectedEnvIdx(idx)}
								>
									{env.name}
								</button>
							</li>
							<li className="border-bottom p-0 m-0 opacity-50" style={{ minWidth: 0, flexGrow: 1 }} key={`tabs-space-mid-${env.name}`}></li>
						</>))}

						{/* Add */}
						<li className="d-flex align-items-center p-1 m-0" style={{ borderBottom: '1px solid rgba(255,255,255,0.5)' }} key="add-env">
							<button className="btn btn-sm btn-outline-success m-0" onClick={handleAddEnv} title="Add Environment">+</button>
						</li>
						<li className="border-bottom p-0 m-0 opacity-50" style={{ minWidth: 0, flexGrow: 5 }} key="tabs-space-end"></li>
					</ul>

					{/* Env Content */}
					<div className="tab-content bg-dark p-3 border border-secondary border-top-0 rounded-bottom">
						{summary.envs && summary.envs.length > 0 ? (
							<>
								<div className="fs-6 row align-items-center justify-content-center">
									<h1 className="col-auto text-center m-0 p-1">{summary.envs[selectedEnvIdx]?.name}</h1>
									<button className="col-auto btn btn-info btn m-0 p-1 rounded-end-0" onClick={handleEditEnv} title="Edit Env">(e)</button>
									<button className="col-auto btn btn-danger btn m-0 p-1 rounded-start-0" onClick={handleDeleteEnv} title="Delete Env">(d)</button>
								</div>

								<div className="row align-items-center justify-content-center mb-4">
									<button className="col-auto btn btn-sm btn-outline-success" onClick={handleCreateSecret} title="Create Secret">+ Create Secret</button>
								</div>

								{Array.isArray(summary.envs[selectedEnvIdx]?.secrets) && summary.envs[selectedEnvIdx].secrets.length > 0 ? (
									<div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3 justify-content-center">
										{summary.envs[selectedEnvIdx].secrets.map((secret, i) => (
											<div key={secret} className="col d-flex flex-column align-items-center mb-5">
												<button
													onClick={
														async () => {
															const inputElem = document.getElementById(`secret-value-${secret}`);
															if (inputElem.type === 'hidden') {
																inputElem.type = 'text';
																inputElem.disabled = true;
																inputElem.value = 'Loading...';
																const secretValue = (await getSecretValue(projectSlug, summary.envs[selectedEnvIdx].name, secret)).data?.value;
																inputElem.disabled = false;
																inputElem.value = secretValue;
															} else {
																inputElem.value = '';
																inputElem.type = 'hidden';
															}
														}
													}
													className="btn btn-lg btn-outline-light mb-2 min-vw-25"
													title="View Secret Value"
												>
													{secret}
												</button>
												<input type="hidden" id={`secret-value-${secret}`} className='mb-2' />
												<div className="d-flex justify-content-center w-100">
													<button
														onClick={
															async () => {
																const newValue = window.prompt(`Enter new value for secret "${secret}":`);
																if (!newValue) return;
																const { updateSecret } = await import('../api/secrets');
																const res = await updateSecret(projectSlug, summary.envs[selectedEnvIdx].name, secret, newValue);
																if (!res.ok) {
																	window.alert(res.message || 'Failed to update secret');
																	return;
																}
																const inputElem = document.getElementById(`secret-value-${secret}`);
																if (inputElem.type === 'text') {
																	inputElem.value = newValue;
																}
																window.alert('Secret updated successfully');
															}
														}
														className="btn btn-sm btn-outline-info mx-1"
														title="Update Secret"
													>
														Update
													</button>
													<button
														onClick={
															async () => {
																if (!window.confirm(`Are you sure you want to delete secret "${secret}"? This action cannot be undone.`)) return;

																const { deleteSecret } = await import('../api/secrets');
																const res = await deleteSecret(projectSlug, summary.envs[selectedEnvIdx].name, secret);
																if (!res.ok) {
																	window.alert(res.message || 'Failed to delete secret');
																	return;
																}
																setSummary((prev) => {
																	const envs = prev.envs.map((e, idx) => {
																		if (idx === selectedEnvIdx) {
																			return {
																				...e,
																				secrets: e.secrets.filter((s) => s !== secret)
																			};
																		}
																		return e;
																	});
																	return { ...prev, envs };
																});
																window.alert('Secret deleted successfully');
															}
														}
														className="btn btn-sm btn-outline-danger mx-1"
														title="Delete Secret"
													>
														Delete
													</button>
												</div>
											</div>
										))}
									</div>
								) : (
									<span className="text-secondary">No secrets found.</span>
								)}
							</>
						) : (
							<span className="text-secondary text-center">No environments found.</span>
						)}
					</div>
				</div>
			)
			}

		</div >
	);
}
