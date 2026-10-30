/* Pilgrimage V2 external service adapters. Independent of legacy map state.
 * Development-only: no storage writes, no navigation without a user click.
 */
(function(root,factory){
  const api=factory();
  if(typeof module==='object'&&module.exports)module.exports=api;
  if(root)root.CatholicPilgrimageServicesV2=api;
})(typeof window!=='undefined'?window:null,function(){
 'use strict';
 function point(p){
  if(!p)return null;
  const lat=Number(p.lat),lng=Number(p.lng);
  if(!Number.isFinite(lat)||!Number.isFinite(lng)||Math.abs(lat)>90||Math.abs(lng)>180||(lat===0&&lng===0))return null;
  return {lat,lng,name:String(p.name||p.placeName||'장소').trim()||'장소'};
 }
 function ordered(course){
  const c=course||{};
  return [c.start,...(Array.isArray(c.waypoints)?c.waypoints:[]),c.end].map(point).filter(Boolean);
 }
 function routeUrl(course){
  const points=ordered(course);
  if(points.length<2)return '';
  const segment=p=>encodeURIComponent(p.name)+','+p.lat+','+p.lng;
  if(points.length>2)return 'https://map.kakao.com/link/by/car/'+points.map(segment).join('/');
  return 'https://map.kakao.com/link/from/'+segment(points[0])+'/to/'+segment(points[1]);
 }
 function routeProxy(proxyUrl,fetcher){
  if(!proxyUrl||typeof proxyUrl!=='string')throw Error('도로 경로 프록시 주소가 없습니다.');
  const request=fetcher||(typeof fetch!=='undefined'?fetch:null);
  if(typeof request!=='function')throw Error('도로 경로 요청 함수가 없습니다.');
  return async function(a,b,signal){
   const origin=point(a),destination=point(b);
   if(!origin||!destination)throw Error('유효하지 않은 출발·도착 좌표');
   const url=new URL(proxyUrl,typeof location==='undefined'?'https://localhost/':location.href);
   url.searchParams.set('endpoint','directions');
   url.searchParams.set('origin',origin.lng+','+origin.lat);
   url.searchParams.set('destination',destination.lng+','+destination.lat);
   url.searchParams.set('priority','RECOMMEND');
   const response=await request(url.toString(),{method:'GET',credentials:'omit',cache:'no-store',signal});
   if(!response||!response.ok)throw Error('경로 API 오류 '+(response&&response.status||'unknown'));
   return response.json();
  };
 }
 async function searchKeyword(proxyUrl,query,signal,fetchImpl){
  const term=String(query||'').trim();
  if(!term)return [];
  if(!proxyUrl)throw Error('장소 검색 서버 주소가 없습니다.');
  const request=fetchImpl||(typeof fetch==='function'?fetch:null);
  if(typeof request!=='function')throw Error('장소 검색을 실행할 수 없습니다.');
  const url=new URL(proxyUrl,typeof location==='undefined'?'https://localhost/':location.href);
  url.searchParams.set('endpoint','keyword');url.searchParams.set('query',term);url.searchParams.set('size','15');
  const res=await request(url.toString(),{method:'GET',credentials:'omit',cache:'no-store',signal});
  if(!res||!res.ok)throw Error('장소 검색 오류 '+(res&&res.status||'unknown'));
  const data=await res.json();
  if(!data||!Array.isArray(data.documents))throw Error('장소 검색 결과 형식이 올바르지 않습니다.');
  return data.documents.map(item=>{
   const lat=Number(item.y),lng=Number(item.x);
   if(!item.place_name||!Number.isFinite(lat)||!Number.isFinite(lng)||lat>90||lat< -90||lng>180||lng< -180)return null;
   return {name:String(item.place_name),addr:String(item.road_address_name||item.address_name||''),lat,lng,placeId:String(item.id||''),kind:'place'};
  }).filter(Boolean);
 }
 function navigationAction(openExternal){
  if(typeof openExternal!=='function')throw Error('외부 이동 함수가 필요합니다.');
  return function(payload){
   const url=routeUrl(payload&&payload.course);
   if(!url)throw Error('길찾기에 필요한 장소가 부족합니다.');
   return openExternal(url);
  };
 }
 function resultActions(options){
  const o=options||{};
  if(typeof o.onBack!=='function'||typeof o.openExternal!=='function')throw Error('실제 돌아가기·외부 길찾기 연결이 필요합니다.');
  return {onBack:o.onBack,fetchLeg:routeProxy(o.proxyUrl,o.fetchImpl),onNavigate:navigationAction(o.openExternal),onReselect:o.onReselect};
 }
 return Object.freeze({point,ordered,routeUrl,routeProxy,searchKeyword,navigationAction,resultActions});
});
