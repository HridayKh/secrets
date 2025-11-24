import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import './index.css';
import Projects from "./pages/Projects.jsx";
import Project from "./pages/Project.jsx";

export function withPrefix(path) {
	const routePrefix = import.meta.env.DEV ? '' : '/secrets';
	if (!routePrefix) return path;
	if (path === '/') return routePrefix + '/';
	return `${routePrefix}${path.startsWith('/') ? '' : '/'}${path}`;
}


ReactDOM.createRoot(document.getElementById('root')).render(
	<React.StrictMode><Router><Routes>
		<Route path={withPrefix('/')} element={<Projects />} />
		<Route path={withPrefix('/:projectSlug')} element={<Project />} />
		<Route path={withPrefix('/_u')} element={<h1 className="mt-5 text-center"> Unimplemented Page </h1>} />
		<Route path={withPrefix('*')} element={<h1 className="mt-5 text-center"> NotFound Page </h1>} />
	</Routes></Router></React.StrictMode>
);
