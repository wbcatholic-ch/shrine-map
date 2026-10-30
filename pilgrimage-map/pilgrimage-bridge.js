/* Development-only pilgrimage map V2 bridge.
 * Disabled unless window.__OAI_PILGRIMAGE_V2_DEV__ === true.
 * Never alters saved pilgrimage data, legacy category maps, or route globals.
 */
(function(root){
 'use strict';
 let active=null;
 const api={
  // Fail closed: an explicit adapter with working actions is required even for a dev switch.
  enabled(){return root.__OAI_DEV_RUNTIME_ACTIVE__===true && root.__OAI_PILGRIMAGE_V2_DEV__===true && !!root.__OAI_PILGRIMAGE_V2_ADAPTER__;},
  close(){
   if(!active)return false;
   const session=active;active=null;
   try{session.instance.destroy();}
   catch(error){console.warn('[순례지도 V2] 화면 해제 오류',error);}
   finally{
    session.host.remove();
    root.__OAI_PILGRIMAGE_V2_ADAPTER__=null;
    // A trial is single-use. Never strand the rest of the pilgrimage UI in
    // developer routing after leaving a map or navigating away from Settings.
    root.__OAI_PILGRIMAGE_V2_DEV__=false;
    const toggle=document.getElementById('oai-dev-pilgrimage-v2');
    if(toggle)toggle.checked=false;
    const status=document.getElementById('oai-dev-pilgrimage-v2-status');
    if(status)status.textContent='기존 순례 지도 사용 중 · 신규 지도 OFF';
   }
   return true;
  },
  // Consumed only while an isolated V2 screen is active. A failed save must
  // leave the editor visible; do not fall through to the underlying app back.
  back(){
   if(!active)return false;
   const session=active;
   const current=typeof session.instance.getCourse==='function'?session.instance.getCourse():undefined;
   session.onBack(current);
   return true;
  },
  open(kind,course,options){
   if(!api.enabled())return false;
   const opt=options||{};
   const adapter=root.__OAI_PILGRIMAGE_V2_ADAPTER__;
   // The existing app supplies no real route/storage/back adapters yet.
   // Never launch a screen with inert buttons or a missing road-route provider.
   const actions=adapter&&adapter[kind];
   const required=kind==='pick'?['onBack','onChange','onRouteSearch','searchPlaces','getFavorites']:
     kind==='result'?['onBack','fetchLeg','onNavigate']:
     kind==='place-view'?['onBack','fetchLeg']:null;
   if(!required || !actions || required.some(key=>typeof actions[key]!=='function'))return false;
   const resolved=Object.assign({},opt,actions);
   const makers={result:root.CatholicPilgrimageResultV2,pick:root.CatholicPilgrimagePickV2,'place-view':root.CatholicPilgrimagePlaceViewV2};
   const maker=makers[kind];
   if(!maker||!root.CatholicPilgrimageMapV2||!root.kakao||!root.kakao.maps||!root.kakao.maps.Map)return false;
   // Build the next V2 map first. A failed result/search transition must not
   // destroy the still-working V2 picker beneath it.
   const previous=active;
   const host=document.createElement('div');host.className='pmv2-dev-host';host.setAttribute('data-pilgrimage-dev','true');document.body.append(host);
   const onBack=(...args)=>{
    if(!active||active.host!==host)return false;
    try{
     // Returning false means validation or persistence failed; keep V2 open.
     if(resolved.onBack(...args)===false)return false;
    }catch(error){
     if(typeof resolved.onError==='function')resolved.onError(error);
     return false;
    }
    api.close();
    return true;
   };
   try{
    const instance=maker.create({
     container:host,kakao:root.kakao,course:course||{},editable:resolved.editable===true,
     shrines:resolved.shrines||[],currentLocation:resolved.currentLocation||null,onRequestLocation:resolved.onRequestLocation,
     lockEndpoints:resolved.lockEndpoints===true,isWaypointLocked:resolved.isWaypointLocked,
     fetchLeg:resolved.fetchLeg,
     onRouteSearch:resolved.onRouteSearch,onChange:resolved.onChange,onReselect:resolved.onReselect,
     searchPlaces:resolved.searchPlaces,getFavorites:resolved.getFavorites,onNavigate:resolved.onNavigate,
     onBack
    });
    active={kind,instance,host,onBack};
    // Always offer a local escape hatch even if Android Back/history is stuck.
    // Closing a trial never persists its working copy.
    const emergency=document.createElement('button');
    emergency.type='button';emergency.className='pmv2-dev-exit';
    emergency.textContent='시험 종료';
    emergency.setAttribute('aria-label','신규 순례 지도 시험을 종료하고 기존 화면으로 돌아가기');
    emergency.addEventListener('click',()=>api.close());
    host.append(emergency);
    if(previous){
     try{previous.instance.destroy();}catch(error){console.warn('[순례지도 V2] 이전 화면 해제 오류',error);}
     finally{previous.host.remove();}
    }
    return true;
   }catch(error){
    host.remove();
    if(active&&active.host===host)active=previous;
    if(typeof resolved.onError==='function')resolved.onError(error);
    return false;
   }
  },
  getActiveKind(){return active&&active.kind||null;}
 };
 root.CatholicPilgrimageBridgeV2=Object.freeze(api);
})(typeof window!=='undefined'?window:{});
