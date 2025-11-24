import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import './index.css';

export function withPrefix(path) {
	console.log('import.meta.env:', import.meta.env);
	const routePrefix = import.meta.env.DEV ? '' : '/secrets';
	if (!routePrefix) return path;
	if (path === '/') return routePrefix + '/';
	return `${routePrefix}${path.startsWith('/') ? '' : '/'}${path}`;
	// return "" + path; // Temporary fix for routing issue in Vite dev mode
}
import Projects from "./pages/Projects.jsx";
import Project from "./pages/Project.jsx";

createRoot(document.getElementById('root')).render(
	<StrictMode><Router><Routes>
		<Route path={withPrefix('/')} element={<Projects />} />
		<Route path={withPrefix('/:projectSlug')} element={<Project />} />
		<Route path={withPrefix('/_u')} element={<h1 className="mt-5 text-center"> Unimplemented Page </h1>} />
		<Route path={withPrefix('*')} element={<h1 className="mt-5 text-center"> NotFound Page </h1>} />
	</Routes></Router></StrictMode>
);
