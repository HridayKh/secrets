import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './index.css';

import Home from "./Home.jsx";


createRoot(document.getElementById('root')).render(
	<StrictMode>
		<Router>
			<Routes>
				<Route path="/" element={<Home />} />
				<Route path="/test" element={<Unimplemented />} />
				<Route path="*" element={<NotFound />} />
			</Routes>
		</Router>
	</StrictMode>
);

function NotFound() {
	return (
		<>
			<h1> NotFound Page </h1>
		</>
	);
}

function Unimplemented() {
	return (
		<>
			<h1> Unimplemented Page </h1>
		</>
	);
}

