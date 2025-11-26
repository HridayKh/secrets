import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { createApiKey, listApiKeys, revokeApiKey } from '../api/keys.js';
import { withPrefix } from '../main.jsx';

export default function Project() {
	const { projectSlug } = useParams();
	const [keys, setKeys] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	const [newKeyPlaintext, setNewKeyPlaintext] = useState('');
	const [newKeyMessage, setNewKeyMessage] = useState('');
	const loginUrlBase = (import.meta.env.VITE_AUTH_BACKEND || 'https://auth.HridayKh.in') + '/login?redirect=';

	useEffect(() => {
		async function load() {
			const res = await listApiKeys(projectSlug);
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
			setKeys((res.data && res.data) || res);
			// setKeys(JSON.parse(`{"apiKeys":[{"id":"2","label":"test_key"},{"id":"3","label":"test_key"}],"message":"API keys listed.","type":"success"}`));
			setLoading(false);
		}
		load();
	}, [projectSlug]);

	return (
		<div className="bg-dark text-light min-vh-100 min-vw-100">

			<Link to={withPrefix('/' + projectSlug)} className="btn btn-secondary mt-3 ms-3">Back</Link>

			{loading && <div className="spinner-border text-light" role="status"><span className="visually-hidden">Loading...</span></div>}
			{error && <div className="alert bg-black text-white">{error}</div>}
			{!loading && !error && (
				<div className="bg-black text-light p-4 rounded mt-3 mx-3">

					{/* Project Slug */}
					<div className="row align-items-center justify-content-center">
						<span className="col-auto text-center m-0 fs-1">Managing Api Keys for <b className='border-bottom border-3'>{projectSlug}</b></span>
					</div>

					{/* Main Content */}
					<div className="tab-content bg-dark p-3 border border-secondary rounded mt-4">
						{keys.apiKeys && keys.apiKeys.length > 0 ? (
							<>
								<div className="row align-items-center justify-content-center mb-4">
									<button
										className="col-auto btn btn-sm btn-success"
										onClick={() => {
											const label = prompt("Enter a label for the new API key:");
											if (!label || label.trim() === '') {
												alert("Key creation cancelled. Label cannot be empty.");
												return;
											}
											createApiKey(projectSlug, label.trim()).then(res => {
												if (!res.ok) {
													alert(`Failed to create key: ${res.message || `Request failed (${res.status})`}`);
													return;
												}
												const { key_plaintext, message } = res.data;
												setKeys(prevKeys => ({
													...prevKeys,
													apiKeys: [...prevKeys.apiKeys, { id: res.data.id, label }]
												}));
												setNewKeyPlaintext(key_plaintext);
												setNewKeyMessage(message);
											});
										}
										}
										title="Create Key"
									>
										+ Create Key
									</button>
									<p className='text-warning text-center'>{newKeyMessage}</p>
									<input type={newKeyPlaintext ? "text" : "hidden"} value={newKeyPlaintext}></input>
								</div>

								<div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3 justify-content-center">
									{keys.apiKeys.map(({ id, label }) => (
										<div key={id} className="col d-flex flex-column align-items-center mb-5">
											<span>{label}</span>
											<div className="d-flex justify-content-center w-100">
												<button
													onClick={() => {
														if (!confirm(`Are you sure you want to revoke/delete the key "${label}"? This action cannot be undone.`)) return;
														revokeApiKey(projectSlug, id).then(res => {
															if (!res.ok) {
																alert(`Failed to revoke key: ${res.message || `Request failed (${res.status})`}`);
																return;
															}
															alert(`Key "${label}" revoked successfully.`);
															setKeys(prevKeys => ({
																...prevKeys,
																apiKeys: prevKeys.apiKeys.filter(key => key.id !== id)
															}));
														});
													}}
													className="btn btn-sm btn-outline-danger mx-1"
													title="Revoke Key"
												>
													Delete
												</button>
											</div>
										</div>
									))}
								</div>
							</>
						) : (
							<span className="text-secondary text-center">No Keys found.</span>
						)}
					</div>
				</div>
			)
			}

		</div >
	);
}
