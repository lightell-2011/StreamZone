
const express=require('express'),http=require('http');
const {Server}=require('socket.io');
const app=express(),server=http.createServer(app),io=new Server(server);
const streams=new Map(), viewers=new Map();
app.use(express.static('public'));
app.get('/health',(_,res)=>res.json({ok:true,app:'StreamZone'}));
const clean=(v,n=100)=>String(v||'').replace(/[<>]/g,'').trim().slice(0,n);
function publish(){io.emit('streams',Array.from(streams.values()).map(s=>({channel:s.channel,title:s.title,category:s.category,name:s.name,viewers:s.viewers||0})))}
io.on('connection',s=>{
 s.emit('streams',Array.from(streams.values()).map(x=>({channel:x.channel,title:x.title,category:x.category,name:x.name,viewers:x.viewers||0})));
 s.on('go-live',d=>{
  const channel=clean(d.channel,32).toLowerCase().replace(/[^a-z0-9_-]/g,'');
  if(!channel)return s.emit('error-msg','Use letters/numbers for the channel name.');
  if(streams.has(channel)&&streams.get(channel).id!==s.id)return s.emit('error-msg','That channel is already live.');
  streams.set(channel,{channel,title:clean(d.title)||'Live now',category:clean(d.category)||'Gaming',name:clean(d.name)||channel,id:s.id,viewers:0});
  s.data.broadcast=channel;s.join(channel);s.emit('live-started',channel);publish();
 });
 s.on('end-live',()=>{
  const c=s.data.broadcast;if(c&&streams.get(c)?.id===s.id){io.to(c).emit('ended');streams.delete(c);delete s.data.broadcast;publish()}
 });
 s.on('join-channel',c=>{
  c=clean(c,32);const st=streams.get(c);if(!st)return s.emit('error-msg','This channel is no longer live.');
  const old=viewers.get(s.id);if(old){s.leave(old);if(streams.has(old)&&streams.get(old).id!==s.id)streams.get(old).viewers=Math.max(0,streams.get(old).viewers-1)}
  viewers.set(s.id,c);s.join(c);if(st.id!==s.id){st.viewers++;io.to(st.id).emit('viewer-joined',s.id)}
  s.emit('joined',{id:st.id,title:st.title,name:st.name,channel:c});publish();
 });
 s.on('leave-channel',()=>{const c=viewers.get(s.id);if(!c)return;s.leave(c);viewers.delete(s.id);const st=streams.get(c);if(st&&st.id!==s.id){st.viewers=Math.max(0,st.viewers-1);io.to(st.id).emit('viewer-left',s.id)}publish()});
 s.on('signal',d=>{if(d&&d.to&&d.data)io.to(d.to).emit('signal',{from:s.id,data:d.data})});
 s.on('chat',d=>{const c=viewers.get(s.id)||s.data.broadcast;if(c&&streams.has(c)){const msg=clean(d.message,300);if(msg)io.to(c).emit('chat',{name:clean(d.name,24)||'Viewer',message:msg,time:Date.now()})}});
 s.on('disconnect',()=>{const c=viewers.get(s.id);if(c){const st=streams.get(c);if(st&&st.id!==s.id)st.viewers=Math.max(0,st.viewers-1);viewers.delete(s.id)}const own=s.data.broadcast;if(own&&streams.get(own)?.id===s.id){io.to(own).emit('ended');streams.delete(own)}publish()});
});
server.listen(process.env.PORT||3000,'0.0.0.0',()=>console.log('StreamZone: http://localhost:'+(process.env.PORT||3000)));
