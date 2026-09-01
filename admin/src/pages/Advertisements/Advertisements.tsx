import { useEffect, useState } from 'react';

const API=import.meta.env.VITE_API_URL ?? 'http://localhost:4000/api/v1';
type Ad={id:string;title:string;description?:string;image_url?:string;target_url?:string;active:boolean;impressions:number;clicks:number};

export default function Advertisements(){
 const [ads,setAds]=useState<Ad[]>([]); const [open,setOpen]=useState(false);
 const [form,setForm]=useState({title:'',description:'',imageUrl:'',targetUrl:'',active:true});
 const token=localStorage.getItem('lenvora_token') ?? '';

 async function load(){const r=await fetch(`${API}/advertisements`,{headers:{Authorization:`Bearer ${token}`}}); const j=await r.json(); setAds(j.data??[])}
 useEffect(()=>{load()},[]);
 async function save(e:React.FormEvent){
  e.preventDefault();
  await fetch(`${API}/advertisements`,{method:'POST',headers:{'Content-Type':'application/json',Authorization:`Bearer ${token}`},body:JSON.stringify(form)});
  setForm({title:'',description:'',imageUrl:'',targetUrl:'',active:true});setOpen(false);load();
 }
 async function remove(id:string){if(!confirm('Delete this advertisement?'))return;await fetch(`${API}/advertisements/${id}`,{method:'DELETE',headers:{Authorization:`Bearer ${token}`}});load()}
 async function toggle(ad:Ad){await fetch(`${API}/advertisements/${ad.id}`,{method:'PATCH',headers:{'Content-Type':'application/json',Authorization:`Bearer ${token}`},body:JSON.stringify({active:!ad.active})});load()}
 return <div>
  <div className="page-head"><h1>Advertisements</h1><button onClick={()=>setOpen(true)}>+ New Advertisement</button></div>
  <div className="card table-wrap"><table><thead><tr><th>Title</th><th>Status</th><th>Impressions</th><th>Clicks</th><th>Actions</th></tr></thead>
  <tbody>{ads.map(a=><tr key={a.id}><td>{a.title}</td><td>{a.active?'Active':'Inactive'}</td><td>{a.impressions}</td><td>{a.clicks}</td><td><button onClick={()=>toggle(a)}>{a.active?'Disable':'Enable'}</button> <button onClick={()=>remove(a.id)}>Delete</button></td></tr>)}</tbody></table></div>
  {open&&<div className="modal"><form className="card form" onSubmit={save}><h2>New Advertisement</h2>
   <input required placeholder="Title" value={form.title} onChange={e=>setForm({...form,title:e.target.value})}/>
   <textarea placeholder="Description" value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/>
   <input placeholder="Image URL" value={form.imageUrl} onChange={e=>setForm({...form,imageUrl:e.target.value})}/>
   <input placeholder="Target URL" value={form.targetUrl} onChange={e=>setForm({...form,targetUrl:e.target.value})}/>
   <label><input type="checkbox" checked={form.active} onChange={e=>setForm({...form,active:e.target.checked})}/> Active</label>
   <button type="submit">Save</button><button type="button" onClick={()=>setOpen(false)}>Cancel</button>
  </form></div>}
 </div>
}
