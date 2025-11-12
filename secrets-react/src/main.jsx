import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './index.css';

function withPrefix(path) {
	const routePrefix = import.meta.env.DEV ? '' : '/secrets';
	if (!routePrefix) return path;
	if (path === '/') return routePrefix + '/';
	return `${routePrefix}${path.startsWith('/') ? '' : '/'}${path}`;
}

import Home from "./Home.jsx";

createRoot(document.getElementById('root')).render(
	<StrictMode><Router><Routes>
		<Route path={withPrefix('/')} element={<Home />} />
		<Route path={withPrefix('/test')} element={<h1 className="mt-5 text-center"> Unimplemented Page </h1>} />
		<Route path={withPrefix('*')} element={<h1 className="mt-5 text-center"> NotFound Page </h1>} />
	</Routes></Router></StrictMode>
);
