/* Standalone Principle 1 result screen. Not connected to legacy routes, data or UI. */
(function(root,factory){const api=factory();if(typeof module==='object'&&module.exports)module.exports=api;if(root)root.CatholicPilgrimageResultV2=api;})(typeof window!=='undefined'?window:null,function(){
'use strict';
function make(tag,cls,text){const el=document.createElement(tag);if(cls)el.className=cls;if(text!==undefined)el.textContent=text;return el;}
function create(options){const opt=options||{},MapAPI=opt.engineApi||(typeof window!=='undefined'&&window.CatholicPilgrimageMapV2),RouteAPI=opt.routeApi||(typeof window!=='undefined'&&window.CatholicPilgrimageRouteV2);
 if(!MapAPI||!RouteAPI||!opt.container)throw Error('독립 지도 엔진/경로 계산/컨테이너가 필요합니다.');
 const root=make('section','pmv2-screen');const mapEl=make('div','pmv2-map');const card=make('section','pmv2-card');const grip=make('button','pmv2-grip','━━');grip.type='button';grip.setAttribute('aria-label','하단 카드 접기 또는 펼치기');const info=make('div','pmv2-info','경로 계산 준비 중');const actions=make('div','pmv2-actions');const nav=make('button','pmv2-btn','카카오내비');nav.type='button';const back=make('button','pmv2-btn','돌아가기');back.type='button';actions.append(nav,back);
 let select=null;if(opt.editable){select=make('button','pmv2-btn','다시 선택');select.type='button';actions.prepend(select);}card.append(grip,info,actions);root.append(mapEl,card);opt.container.append(root);
 let destroyed=false,abortController=null,route=null;
 const engine=new MapAPI.Engine({container:mapEl,kakao:opt.kakao,course:opt.course,mode:'result',cardCollapsed:false,cardHeight:card.getBoundingClientRect().height,callbacks:opt.markerCallbacks||{}});
 function collapsed(flag){card.classList.toggle('pmv2-collapsed',flag);engine.setCardState(flag,card.getBoundingClientRect().height);}
 grip.addEventListener('click',()=>collapsed(!card.classList.contains('pmv2-collapsed')));
 let yStart=null;grip.addEventListener('touchstart',e=>{yStart=e.touches[0].clientY;},{passive:true});grip.addEventListener('touchend',e=>{if(yStart===null)return;const delta=e.changedTouches[0].clientY-yStart;yStart=null;if(Math.abs(delta)>24)collapsed(delta>0);},{passive:true});
 back.addEventListener('click',()=>{if(typeof opt.onBack==='function')opt.onBack();});if(select)select.addEventListener('click',()=>{if(typeof opt.onReselect==='function')opt.onReselect();});
 nav.disabled=true;nav.addEventListener('click',()=>{if(route&&typeof opt.onNavigate==='function')opt.onNavigate({course:engine.course,route});});
 async function search(){if(abortController)abortController.abort();abortController=new AbortController();const token=engine.beginRequest();info.textContent='도로 경로 계산 중…';nav.disabled=true;
 try{const fetchLeg=opt.fetchLeg||(opt.proxyUrl?RouteAPI.proxyFetcher(opt.proxyUrl,opt.fetchImpl):null);const result=await RouteAPI.calculate(engine.course,fetchLeg,abortController.signal);if(destroyed||!engine.isCurrentRequest(token))return;route=result;engine.drawRouteSegments(result.segments);info.textContent=`총거리 ${RouteAPI.formatDistance(result.distance)} · 예상시간 ${RouteAPI.formatDuration(result.duration)}`;nav.disabled=typeof opt.onNavigate!=='function';}
 catch(e){if(destroyed||!engine.isCurrentRequest(token))return;route=null;info.textContent='도로 경로 계산 실패: '+(e&&e.message||'알 수 없는 오류');if(typeof opt.onRouteError==='function')opt.onRouteError(e);}
 }
 function destroy(){if(destroyed)return;destroyed=true;if(abortController)abortController.abort();engine.destroy();root.remove();}
 // Route geometry is computed asynchronously and MUST NOT refit the map.
 const ready=search();return {root,engine,ready,search,destroy,collapse:collapsed,getResult:()=>route};
}
return Object.freeze({create});
});
