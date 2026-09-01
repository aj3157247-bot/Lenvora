import { useState } from 'react';
import { Routes, Route, NavLink, Navigate, useNavigate } from 'react-router-dom';
import Advertisements from './pages/Advertisements/Advertisements';

const API=import.meta.env.VITE_API_URL ?? 'http://localhost:4000/api/v1';

function Login(){const [email,setEmail]=useState(''),[password,setPassword]=useState(''),[error,setError]=useState('');const nav=useNavigate();
 async function submit(e:React.FormEvent){e.preventDefault();setError('');try{const r=await fetch(`${API}/auth/login`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email,password})});const j=await r.json();if(!r.ok)throw Error(j.error??'Login failed');localStorage.setItem('lenvora_token',j.data.token);nav('/dashboard')}catch(x){setError(x instanceof Error?x.message:'Login failed')}}
 return <main className="login"><form onSubmit={submit} className="card"><h1>Lenvora</h1><p>Admin Panel</p>{error&&<div className="error">{error}</div>}<input required type="email" placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)}/><input required type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)}/><button>Login</button></form></main>}

function Layout(){const nav=useNavigate();const logout=()=>{localStorage.removeItem('lenvora_token');nav('/login')};return <div className="layout"><aside><h2>Lenvora</h2>{[['Dashboard','/dashboard'],['Users','/users'],['Languages','/languages'],['Words','/words'],['Translations','/translations'],['OCR','/ocr'],['Advertisements','/advertisements'],['Reports','/reports'],['Settings','/settings']].map(([n,p])=><NavLink key={p} to={p}>{n}</NavLink>)}<button className="logout" onClick={logout}>Logout</button></aside><section className="content"><Routes><Route path="/dashboard" element={<h1>Dashboard</h1>}/><Route path="/advertisements" element={<Advertisements/>}/><Route path="*" element={<h1>Coming soon</h1>}/></Routes></section></div>}

export default function App(){return <Routes><Route path="/login" element={<Login/>}/><Route path="/*" element={localStorage.getItem('lenvora_token')?<Layout/>:<Navigate to="/login" replace/>}/></Routes>}
