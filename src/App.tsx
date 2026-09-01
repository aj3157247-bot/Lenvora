import { useState } from 'react';
import { Routes, Route, NavLink, Navigate, useNavigate } from 'react-router-dom';

const API = import.meta.env.VITE_API_URL ?? 'http://localhost:4000/api/v1';

function Login() {
  const [email,setEmail]=useState(''); const [password,setPassword]=useState('');
  const [error,setError]=useState(''); const nav=useNavigate();
  async function submit(e:React.FormEvent){
    e.preventDefault(); setError('');
    try{
      const r=await fetch(`${API}/auth/login`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email,password})});
      const j=await r.json();
      if(!r.ok) throw new Error(j.error ?? 'Login failed');
      localStorage.setItem('lenvora_token',j.data.token); nav('/dashboard');
    }catch(err){setError(err instanceof Error?err.message:'Login failed');}
  }
  return <main className="login"><form onSubmit={submit} className="card">
    <h1>Lenvora</h1><p>Admin Panel</p>
    {error&&<div className="error">{error}</div>}
    <input required type="email" placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)}/>
    <input required type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)}/>
    <button>Login</button>
  </form></main>;
}

function Layout({children}:{children:React.ReactNode}){
 const nav=useNavigate();
 const logout=()=>{localStorage.removeItem('lenvora_token');nav('/login')};
 const links=[['Dashboard','/dashboard'],['Users','/users'],['Languages','/languages'],['Words','/words'],['Translations','/translations'],['OCR','/ocr'],['Advertisements','/advertisements'],['Reports','/reports'],['Settings','/settings']];
 return <div className="layout"><aside><h2>Lenvora</h2>{links.map(([n,p])=><NavLink key={p} to={p}>{n}</NavLink>)}<button className="logout" onClick={logout}>Logout</button></aside><section className="content">{children}</section></div>;
}
function Dashboard(){return <><h1>Dashboard</h1><div className="grid">{['Users','Words','Translations','Active Ads'].map((x,i)=><div className="stat" key={x}><span>{x}</span><strong>{[1245,25430,8921,7][i]}</strong></div>)}</div></>}
function Simple({title}:{title:string}){return <><h1>{title}</h1><div className="card"><p>{title} management will be connected to the backend API.</p></div></>}
function Protected(){
 if(!localStorage.getItem('lenvora_token')) return <Navigate to="/login" replace/>;
 return <Layout><Routes>
   <Route path="/dashboard" element={<Dashboard/>}/>
   <Route path="/users" element={<Simple title="Users"/>}/>
   <Route path="/languages" element={<Simple title="Languages"/>}/>
   <Route path="/words" element={<Simple title="Dictionary / Words"/>}/>
   <Route path="/translations" element={<Simple title="Translations"/>}/>
   <Route path="/ocr" element={<Simple title="OCR"/>}/>
   <Route path="/advertisements" element={<Simple title="Advertisements"/>}/>
   <Route path="/reports" element={<Simple title="Reports"/>}/>
   <Route path="/settings" element={<Simple title="Settings"/>}/>
   <Route path="*" element={<Navigate to="/dashboard" replace/>}/>
 </Routes></Layout>
}
export default function App(){return <Routes><Route path="/login" element={<Login/>}/><Route path="/*" element={<Protected/>}/></Routes>}
