/* Standalone principle-2 pilgrimage selection view. No legacy app references or writes.
 * Data in via snapshots, changes out via callbacks. This module owns only its DOM/map.
 */
(function(root,factory){const api=factory();if(typeof module==='object'&&module.exports)module.exports=api;if(root)root.CatholicPilgrimagePickV2=api;})(typeof window!=='undefined'?window:null,function(){
'use strict';
const MAX_WAYPOINTS=10;
const clone=p=>p?{...p}:null;
function snapshot(course){const c=course||{};return {start:clone(c.start),end:clone(c.end),waypoints:(Array.isArray(c.waypoints)?c.waypoints:[]).map(clone).filter(Boolean)};}
function eq(a,b){return !!a&&!!b&&((a.id&&b.id&&String(a.id)===String(b.id))||(Number(a.lat)===Number(b.lat)&&Number(a.lng)===Number(b.lng)));}
function addWaypoint(c,p){const next=snapshot(c);if(!p||next.waypoints.some(x=>eq(x,p)))return next;if(next.waypoints.length>=MAX_WAYPOINTS)throw Error('경유지는 최대 10곳까지 추가할 수 있습니다.');next.waypoints.push(clone(p));return next;}
function removeWaypoint(c,i){const next=snapshot(c);if(i>=0&&i<next.waypoints.length)next.waypoints.splice(i,1);return next;}
function moveWaypoint(c,from,to){const next=snapshot(c);if(from<0||to<0||from>=next.waypoints.length||to>=next.waypoints.length)return next;next.waypoints.splice(to,0,...next.waypoints.splice(from,1));return next;}
function el(tag,cls,text){const e=document.createElement(tag);if(cls)e.className=cls;if(text!==undefined)e.textContent=text;return e;}
function button(text,handler,cls){const b=el('button',cls||'pmv2-btn',text);b.type='button';b.addEventListener('click',handler);return b;}
function create(opts){const o=opts||{}, api=o.engineApi||(typeof window!=='undefined'&&window.CatholicPilgrimageMapV2);if(!api||!o.container)throw Error('독립 지도 엔진과 컨테이너가 필요합니다.');
 const root=el('section','pmv2-screen pmv2-pick'),mapEl=el('div','pmv2-map'),card=el('section','pmv2-card'),grip=button('━━',()=>toggle(!collapsed),'pmv2-grip'),main=el('div','pmv2-pick-main'),top=el('div','pmv2-pick-top'),dialog=el('div','pmv2-pick-dialog');dialog.hidden=true;grip.setAttribute('aria-label','하단 카드 접기 또는 펼치기');
 const title=el('strong','','지도에서 추가'),display=el('select','pmv2-pick-display');display.setAttribute('aria-label','성당 표시');[['shrines','성지만 표시'],['selected','성당 표시(선택 경유지)'],['hidden','일반 마커 숨김']].forEach(([v,t])=>{const op=el('option','',t);op.value=v;display.append(op);});
 top.append(title,display,button('×',()=>{if(typeof o.onBack==='function')o.onBack(snapshot(course));},'pmv2-pick-close'));
 const search=el('input','pmv2-pick-search');search.type='search';search.placeholder='성지·장소 검색';search.setAttribute('aria-label','장소 검색');const searchItems=el('div','pmv2-pick-search-items');
 const rows=el('div','pmv2-pick-rows'),actions=el('div','pmv2-actions');main.append(search,searchItems,rows,actions);card.append(grip,main);root.append(mapEl,top,dialog,card);o.container.append(root);
 let destroyed=false,collapsed=false,course=snapshot(o.course),mode='shrines',selectedRole='waypoint',searchToken=0,dragIndex=-1;
 let undoStack=[],redoStack=[],undoButton=null,redoButton=null;
 const shrines=(Array.isArray(o.shrines)?o.shrines:[]).map(api.normalize?x=>api.normalize({waypoints:[x]}).waypoints[0]:x=>x).filter(Boolean);let background=[],backgroundListeners=[];
 const engine=new api.Engine({container:mapEl,kakao:o.kakao,course,mode:'pick',cardCollapsed:false,cardHeight:card.getBoundingClientRect().height,topInset:top.getBoundingClientRect().height,currentLocation:o.currentLocation,showParishWaypoints:false,callbacks:{onMarkerClick:({point,role})=>{if(role==='waypoint'){const ix=course.waypoints.findIndex(p=>eq(p,point));if(ix>=0){if(typeof o.isWaypointLocked==='function'&&o.isWaypointLocked(point,ix))return showChoices(point,[['GPS 방문기록 · 유지',closeDialog]]);return showChoices(point,[['유지',closeDialog],['경유지 취소',()=>{change(removeWaypoint(course,ix));closeDialog();}]]);}}if(role==='start'||role==='end'){if(o.lockEndpoints)return showChoices(point,[['유지',closeDialog]]);return showChoices(point,[['유지',closeDialog],['지정 취소',()=>{const c=snapshot(course);c[role]=null;change(c);closeDialog();}]]);}showPointChoice(point);}}});

 function clearBackground(){if(engine.maps.event&&engine.maps.event.removeListener){backgroundListeners.forEach(([marker,handler])=>engine.maps.event.removeListener(marker,'click',handler));}backgroundListeners=[];background.forEach(m=>m.setMap(null));background=[];}
 function drawBackground(){
  // Only instantiate currently visible shrine markers; displaying 167+ Kakao
  // marker objects on every edit makes the map costly on compact WebViews.
  engine.setShowParishWaypoints(mode==='selected'||mode==='hidden');
  clearBackground();if(mode==='hidden')return;
  const selected=[course.start,...course.waypoints,course.end].filter(Boolean);
  let box=null;
  try{
    const bounds=engine.map.getBounds&&engine.map.getBounds();
    if(bounds&&bounds.getSouthWest&&bounds.getNorthEast){
      const sw=bounds.getSouthWest(),ne=bounds.getNorthEast();
      box={south:sw.getLat(),north:ne.getLat(),west:sw.getLng(),east:ne.getLng()};
    }
  }catch(_e){}
  for(const p of shrines){
    if(selected.some(x=>eq(x,p)))continue;
    if(box&&(p.lat<box.south||p.lat>box.north||p.lng<box.west||p.lng>box.east))continue;
    const m=new engine.maps.Marker({map:engine.map,position:new engine.maps.LatLng(p.lat,p.lng),title:p.name||''});
    if(engine.maps.event&&engine.maps.event.addListener){const handler=()=>showPointChoice(p);engine.maps.event.addListener(m,'click',handler);backgroundListeners.push([m,handler]);}
    background.push(m);
  }
 }
 function showChoices(p,items){dialog.replaceChildren();const heading=el('strong','',p.name||'선택한 장소');dialog.append(heading);for(const [label,fn] of items)dialog.append(button(label,fn));dialog.hidden=false;}
 function closeDialog(){dialog.hidden=true;dialog.replaceChildren();}
 function showPointChoice(p){
  const choices=[];
  if(!o.lockEndpoints){
   choices.push(['출발지 지정',()=>{const c=snapshot(course);c.start=clone(p);change(c);closeDialog();}]);
   choices.push(['도착지 지정',()=>{const c=snapshot(course);c.end=clone(p);change(c);closeDialog();}]);
  }
  choices.push(['경유지 추가',()=>{try{change(addWaypoint(course,p));closeDialog();}catch(e){showMessage(e.message);}}]);
  choices.push(['닫기',closeDialog]);
  showChoices(p,choices);
 }
 function showMessage(message){showChoices({name:message},[['확인',closeDialog]]);}
 function renderRows(){rows.replaceChildren();function row(label,p,role,index){const r=el('div','pmv2-pick-row');const drag=el('span','pmv2-pick-drag',role==='waypoint'?'⠿':'●');const name=button(label+': '+(p&&p.name||'선택 안 됨'),()=>{selectedRole=role;search.focus();},'pmv2-pick-row-name');
 const locked=role==='waypoint'&&typeof o.isWaypointLocked==='function'&&o.isWaypointLocked(p,index);
 if(locked||(o.lockEndpoints&&role!=='waypoint')){name.disabled=true;name.title='GPS 완료기록은 이 지도에서 변경할 수 없습니다.';}
 r.append(drag,name);if(role==='waypoint'){r.draggable=!locked;if(locked)drag.style.display='none';r.dataset.waypointIndex=String(index);r.addEventListener('dragstart',e=>{if(locked){e.preventDefault();return;}dragIndex=index;});r.addEventListener('dragover',e=>e.preventDefault());r.addEventListener('drop',e=>{e.preventDefault();if(dragIndex>=0)change(moveWaypoint(course,dragIndex,index));dragIndex=-1;});
 // Fold/Android: hold the handle, then drag onto another waypoint row.
 // Desktop HTML drag-and-drop is preserved. Touch reordering owns no timers.
 let pointerStart=null;
 drag.setAttribute('role','button');drag.setAttribute('aria-label','경유지 순서 변경');
 drag.addEventListener('pointerdown',e=>{if(locked||e.pointerType==='mouse')return;pointerStart={id:e.pointerId,x:e.clientX,y:e.clientY,t:Date.now()};drag.setPointerCapture?.(e.pointerId);});
 drag.addEventListener('pointerup',e=>{if(!pointerStart||pointerStart.id!==e.pointerId)return;const start=pointerStart;pointerStart=null;r.classList.remove('pmv2-dragging');
   if(Date.now()-start.t<250||Math.hypot(e.clientX-start.x,e.clientY-start.y)<14)return;
   const at=document.elementFromPoint(e.clientX,e.clientY);const dest=at&&at.closest('.pmv2-pick-row[data-waypoint-index]');
   if(dest){const target=Number(dest.dataset.waypointIndex);if(target!==index)change(moveWaypoint(course,index,target));}
 });
 drag.addEventListener('pointermove',e=>{if(pointerStart&&pointerStart.id===e.pointerId&&Date.now()-pointerStart.t>=250&&Math.hypot(e.clientX-pointerStart.x,e.clientY-pointerStart.y)>14)r.classList.add('pmv2-dragging');});
 drag.addEventListener('pointercancel',()=>{pointerStart=null;r.classList.remove('pmv2-dragging');});
 if(!locked)r.append(button('×',()=>change(removeWaypoint(course,index)),'pmv2-pick-remove'));}else if(p&&!o.lockEndpoints){r.append(button('×',()=>{const c=snapshot(course);c[role]=null;change(c);},'pmv2-pick-remove'));}rows.append(r);}
 row('출발',course.start,'start');course.waypoints.forEach((p,i)=>row('경유 '+(i+1),p,'waypoint',i));row('도착',course.end,'end');}
 function render(){engine.setCourse(course,{fit:false});drawBackground();renderRows();
  if(undoButton)undoButton.disabled=!undoStack.length;
  if(redoButton)redoButton.disabled=!redoStack.length;
 }
 function change(next,historyAction){const proposed=snapshot(next);
  const approved=typeof o.onChange==='function'?o.onChange(snapshot(proposed)):true;
  if(approved===false){
   showMessage('GPS 방문기록 및 기존 순례코스 규칙상 변경할 수 없는 장소입니다.');return false;
  }
  // The app may return the validated course with canonical shrine/parish identity.
  // Apply it immediately so newly selected parish markers obey the display mode.
  const canonical=approved&&typeof approved==='object'&&Array.isArray(approved.waypoints)
    ?snapshot(approved):proposed;
  if(JSON.stringify(canonical)===JSON.stringify(course))return true;
  if(historyAction==='undo'){
    undoStack.pop();redoStack.push(snapshot(course));
  }else if(historyAction==='redo'){
    redoStack.pop();undoStack.push(snapshot(course));
  }else{
    undoStack.push(snapshot(course));if(undoStack.length>25)undoStack.shift();redoStack=[];
  }
  course=canonical;render();return true;
 }
 function toggle(flag){collapsed=!!flag;card.classList.toggle('pmv2-collapsed',collapsed);engine.setCardState(collapsed,card.getBoundingClientRect().height);}
 grip.addEventListener('touchstart',e=>{grip._touchStart=e.touches[0].clientY;},{passive:true});grip.addEventListener('touchend',e=>{if(typeof grip._touchStart!=='number')return;const d=e.changedTouches[0].clientY-grip._touchStart;delete grip._touchStart;if(Math.abs(d)>24)toggle(d>0);},{passive:true});
 undoButton=button('↶ 취소',()=>{if(undoStack.length)change(undoStack[undoStack.length-1],'undo');});
 redoButton=button('↷ 다시',()=>{if(redoStack.length)change(redoStack[redoStack.length-1],'redo');});
 actions.append(undoButton,redoButton,button('+ 경유지',()=>{selectedRole='waypoint';search.focus();}));
 if(!o.lockEndpoints)actions.append(button('현위치 출발',()=>{if(o.currentLocation){const c=snapshot(course);c.start=clone(o.currentLocation);change(c);}else if(typeof o.onRequestLocation==='function')o.onRequestLocation(p=>{if(destroyed||!p)return;const c=snapshot(course);c.start=clone(p);change(c);});else showMessage('현위치를 확인할 수 없습니다.');}));
 actions.append(button('자주 가는 장소',()=>{if(typeof o.getFavorites!=='function')return showMessage('등록된 장소가 없습니다.');const fav=o.getFavorites()||[];showChoices({name:'자주 가는 장소'},fav.slice(0,5).map(p=>[p.name,()=>{showPointChoice(p);}]).concat([['닫기',closeDialog]]));}));
 const bottom=el('div','pmv2-actions');bottom.append(button('경로검색',()=>{if(typeof o.onRouteSearch==='function')o.onRouteSearch(snapshot(course));}),button('돌아가기',()=>{if(typeof o.onBack==='function')o.onBack(snapshot(course));}));main.append(bottom);
 display.addEventListener('change',()=>{mode=display.value;drawBackground();});
 let searchAbort=null;
 search.addEventListener('input',async()=>{const query=search.value.trim(),token=++searchToken;searchItems.replaceChildren();if(searchAbort)searchAbort.abort();if(!query)return;searchAbort=new AbortController();try{const found=typeof o.searchPlaces==='function'?await o.searchPlaces(query,searchAbort.signal):shrines.filter(p=>(p.name||'').includes(query)).slice(0,10);if(destroyed||token!==searchToken)return;for(const p of (found||[]).slice(0,20))searchItems.append(button(p.name||'장소',()=>{search.value='';searchItems.replaceChildren();if(!o.lockEndpoints&&(selectedRole==='start'||selectedRole==='end')){const c=snapshot(course);c[selectedRole]=clone(p);change(c);}else showPointChoice(p);},'pmv2-pick-suggestion'));}catch(e){if(!searchAbort.signal.aborted)showMessage('검색 오류: '+e.message);}});
 // Refresh viewport shrine markers when the user moves or zooms the map.
 // This is a single listener per picker and is removed on destroy.
 const mapIdle=()=>{if(!destroyed)drawBackground();};
 if(engine.maps.event&&engine.maps.event.addListener)engine.maps.event.addListener(engine.map,'idle',mapIdle);
 // Explicit selection mode displays selected cathedral waypoints via course markers;
 // no broad diocese cathedral layer is ever fetched.
 render();
 // The waypoint rows and action buttons now exist: fit once against the actual
 // expanded card, not the empty card measured during the map constructor.
 engine.setCardState(false,card.getBoundingClientRect().height);
 if(!course.waypoints.length&&!course.start&&!course.end&&!o.currentLocation
    &&typeof o.onRequestLocation==='function'){
   o.onRequestLocation(place=>{if(!destroyed&&place)engine.setCurrentLocation(place);});
 }
 function destroy(){if(destroyed)return;destroyed=true;searchToken++;if(searchAbort)searchAbort.abort();closeDialog();if(engine.maps.event&&engine.maps.event.removeListener)engine.maps.event.removeListener(engine.map,'idle',mapIdle);clearBackground();engine.destroy();root.remove();}
 return {root,engine,destroy,getCourse:()=>snapshot(course),setCourse:change,setDisplayMode:m=>{if(['shrines','selected','hidden'].includes(m)){mode=m;display.value=m;drawBackground();}},getDisplayMode:()=>mode};
}
return Object.freeze({create,MAX_WAYPOINTS,snapshot,eq,addWaypoint,removeWaypoint,moveWaypoint});
});
