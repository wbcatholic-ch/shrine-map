/* Standalone road routing for pilgrimage map V2. Never imports legacy app code. */
(function(root,factory){const api=factory();if(typeof module==='object'&&module.exports)module.exports=api;if(root)root.CatholicPilgrimageRouteV2=api;})(typeof window!=='undefined'?window:null,function(){
'use strict';
function valid(p){return p&&Number.isFinite(Number(p.lat))&&Number.isFinite(Number(p.lng))&&Math.abs(Number(p.lat))<=90&&Math.abs(Number(p.lng))<=180&&(Number(p.lat)!==0||Number(p.lng)!==0);}
function ordered(course){const c=course||{};return [c.start,...(Array.isArray(c.waypoints)?c.waypoints:[]),c.end].filter(valid).filter((p,i,a)=>i===0||Number(p.lat)!==Number(a[i-1].lat)||Number(p.lng)!==Number(a[i-1].lng));}
function parseLeg(data){const r=data&&data.routes&&data.routes[0];if(!r||r.result_code!==0||!r.summary)throw Error('도로 경로를 찾을 수 없습니다.');
 const path=[];for(const sec of r.sections||[])for(const road of sec.roads||[]){const vs=road.vertexes||[];for(let i=0;i+1<vs.length;i+=2){const lng=Number(vs[i]),lat=Number(vs[i+1]);if(Number.isFinite(lat)&&Number.isFinite(lng))path.push({lat,lng});}}
 if(path.length<2)throw Error('도로 경로 좌표가 없습니다.');
 return {path,distance:Number(r.summary.distance),duration:Number(r.summary.duration)};
}
async function calculate(course,fetchLeg,signal){if(typeof fetchLeg!=='function')throw Error('도로 경로 제공 함수가 필요합니다.');const points=ordered(course);if(points.length<2)throw Error('출발지와 도착지 또는 두 곳 이상의 유효한 장소가 필요합니다.');const segments=[];let distance=0,duration=0;
 for(let i=1;i<points.length;i++){if(signal&&signal.aborted)throw Error('경로 검색 취소');const leg=parseLeg(await fetchLeg(points[i-1],points[i],signal));if(!Number.isFinite(leg.distance)||!Number.isFinite(leg.duration)||leg.distance<0||leg.duration<0)throw Error('잘못된 경로 거리·시간');segments.push(leg.path);distance+=leg.distance;duration+=leg.duration;}
 return {segments,distance,duration};}
// Caller provides its separately configured, authorized REST proxy; no secrets stored in this module.
function proxyFetcher(proxyUrl,fetchImpl){if(!proxyUrl)throw Error('경로 프록시 주소가 없습니다.');const fn=fetchImpl||(typeof fetch==='function'?fetch:null);if(!fn)throw Error('fetch unavailable');return async function(a,b,signal){const url=new URL(proxyUrl,typeof location==='undefined'?'https://localhost/':location.href);url.searchParams.set('endpoint','directions');url.searchParams.set('origin',`${a.lng},${a.lat}`);url.searchParams.set('destination',`${b.lng},${b.lat}`);url.searchParams.set('priority','RECOMMEND');const res=await fn(url.toString(),{method:'GET',credentials:'omit',cache:'no-store',signal});if(!res.ok)throw Error(`경로 API 오류 ${res.status}`);return res.json();};}
function formatDistance(m){return (m/1000).toFixed(1)+' km';}
function formatDuration(s){const min=Math.ceil(s/60);return min<60?min+'분':Math.floor(min/60)+'시간 '+String(min%60)+'분';}
return Object.freeze({ordered,parseLeg,calculate,proxyFetcher,formatDistance,formatDuration});
});
