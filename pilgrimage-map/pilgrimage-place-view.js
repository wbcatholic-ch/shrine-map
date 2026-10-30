/* Principle 3: standalone read-only pilgrimage place map.
 * No legacy globals, listeners or storage modifications.
 * Coordinates shown: start, every waypoint, end; fitting: waypoints only.
 */
(function(root,factory){
  const api=factory();
  if(typeof module==='object'&&module.exports)module.exports=api;
  if(root)root.CatholicPilgrimagePlaceViewV2=api;
})(typeof window!=='undefined'?window:null,function(){
 'use strict';
 function node(tag,cls,text){const e=document.createElement(tag);if(cls)e.className=cls;if(text!==undefined)e.textContent=text;return e;}
 function button(label,cls,fn){const b=node('button',cls,label);b.type='button';b.addEventListener('click',fn);return b;}
 function labelFor(point,index){return String(point.name||('경유지 '+(index+1)));}
 function create(options){
  const opt=options||{};
  const MapAPI=opt.engineApi||(typeof window!=='undefined'&&window.CatholicPilgrimageMapV2);
  const RouteAPI=opt.routeApi||(typeof window!=='undefined'&&window.CatholicPilgrimageRouteV2);
  if(!MapAPI||!RouteAPI||!opt.container)throw Error('독립 지도/경로 모듈 및 컨테이너가 필요합니다.');
  const course=MapAPI.normalize(opt.course);
  const root=node('section','pmv2-screen pmv2-place-view');
  const mapEl=node('div','pmv2-map');
  const card=node('section','pmv2-card pmv2-collapsed');
  const grip=button('━━','pmv2-grip',()=>collapse(!card.classList.contains('pmv2-collapsed')));
  grip.setAttribute('aria-label','하단 카드 접기 또는 펼치기');
  const info=node('div','pmv2-info','경로 계산 준비 중');
  const actions=node('div','pmv2-actions');
  const back=button('돌아가기','pmv2-btn',()=>{if(typeof opt.onBack==='function')opt.onBack();});
  actions.append(back);card.append(grip,info,actions);root.append(mapEl,card);opt.container.append(root);
  let destroyed=false,abortController=null,route=null;
  const labelOverlays=[];
  let engine;
  try{
    engine=new MapAPI.Engine({container:mapEl,kakao:opt.kakao,course,mode:'place-view',cardCollapsed:true,cardHeight:card.getBoundingClientRect().height,currentLocation:opt.currentLocation,callbacks:{}});
  }catch(e){root.remove();throw e;}
  function clearLabels(){for(const overlay of labelOverlays)overlay.setMap(null);labelOverlays.length=0;}
  function addLabels(){
    const maps=engine.maps;
    if(!maps.CustomOverlay)return;
    course.waypoints.forEach((p,i)=>{
      const name=node('span','pmv2-waypoint-name',labelFor(p,i));
      const overlay=new maps.CustomOverlay({map:engine.map,position:new maps.LatLng(p.lat,p.lng),content:name,xAnchor:0.5,yAnchor:1.8,zIndex:4});
      labelOverlays.push(overlay);
    });
  }
  addLabels();
  function collapse(flag){
    if(destroyed)return;
    card.classList.toggle('pmv2-collapsed',!!flag);
    engine.setCardState(!!flag,card.getBoundingClientRect().height);
  }
  let startTouch=null;
  grip.addEventListener('touchstart',e=>{if(e.touches&&e.touches.length)startTouch=e.touches[0].clientY;},{passive:true});
  grip.addEventListener('touchend',e=>{if(startTouch===null||!e.changedTouches||!e.changedTouches.length)return;const delta=e.changedTouches[0].clientY-startTouch;startTouch=null;if(Math.abs(delta)>24)collapse(delta>0);},{passive:true});
  function fit(){if(!destroyed)engine.fitInitial();}
  async function search(){
    if(destroyed)return;
    if(abortController)abortController.abort();
    abortController=new AbortController();
    const token=engine.beginRequest();
    route=null;info.textContent='도로 경로 계산 중…';
    try{
      const fetchLeg=opt.fetchLeg||(opt.proxyUrl?RouteAPI.proxyFetcher(opt.proxyUrl,opt.fetchImpl):null);
      const result=await RouteAPI.calculate(engine.course,fetchLeg,abortController.signal);
      if(destroyed||!engine.isCurrentRequest(token))return;
      route=result;engine.drawRouteSegments(result.segments);
      info.textContent='총거리 '+RouteAPI.formatDistance(result.distance)+' · 예상시간 '+RouteAPI.formatDuration(result.duration);
    }catch(e){
      if(destroyed||!engine.isCurrentRequest(token))return;
      route=null;info.textContent='도로 경로 계산 실패: '+(e&&e.message||'알 수 없는 오류');
      if(typeof opt.onRouteError==='function')opt.onRouteError(e);
    }
    // Async route calculation never changes the waypoint-only viewport.
  }
  function destroy(){
    if(destroyed)return;destroyed=true;
    if(abortController)abortController.abort();
    clearLabels();engine.destroy();root.remove();
  }
  // Only explicit user action expands the card; initially collapsed.
  const ready=search();
  return Object.freeze({root,engine,ready,search,fit,collapse,destroy,getResult:()=>route,getCourse:()=>MapAPI.normalize(course)});
 }
 return Object.freeze({create});
});
