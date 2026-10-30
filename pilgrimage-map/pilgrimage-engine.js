/* Catholic Companion — pilgrimage map V2. Standalone, not wired to legacy map.
 * Input-only snapshots; no storage writes or existing app globals.
 */
(function(root,factory){
  const api=factory();
  if(typeof module==='object' && module.exports)module.exports=api;
  if(root)root.CatholicPilgrimageMapV2=api;
})(typeof window!=='undefined'?window:null,function(){
  'use strict';
  const TYPES=Object.freeze({RESULT:'result',PICK:'pick',PLACE_VIEW:'place-view'});
  function point(p){
    if(!p)return null;
    const lat=Number(p.lat),lng=Number(p.lng);
    if(!Number.isFinite(lat)||!Number.isFinite(lng)||Math.abs(lat)>90||Math.abs(lng)>180||(lat===0&&lng===0))return null;
    return {id:String(p.id||p.placeId||''),name:String(p.name||''),addr:String(p.addr||p.address||''),lat,lng,kind:String(p.kind||p.type||p.category||'shrine'),sourceType:String(p.sourceType||''),routeOnly:!!p.routeOnly,done:!!p.done,doneMethod:String(p.doneMethod||''),dynamicCurrent:!!p.dynamicCurrent};
  }
  function normalize(input){
    const data=input||{};
    return {start:point(data.start),end:point(data.end),waypoints:(Array.isArray(data.waypoints)?data.waypoints:[]).map(point).filter(Boolean)};
  }
  function fitPoints(kind,course,current){
    const pts=course.waypoints;
    if(kind===TYPES.RESULT)return [course.start,...pts,course.end].filter(Boolean);
    if(pts.length)return pts.slice();
    return [course.start||course.end||point(current)].filter(Boolean);
  }
  function boundsPadding(container,cardHeight,collapsed,topInset){
    const w=container&&container.clientWidth||360,h=container&&container.clientHeight||640;
    const pad=Math.max(24,Math.round(Math.min(w,h)*0.07));
    const occupied=collapsed?42:Math.max(0,Number(cardHeight)||0);
    return {top:pad+16+Math.max(0,Number(topInset)||0),right:pad,bottom:Math.min(Math.max(pad+occupied,pad+42),Math.max(pad+42,h-96)),left:pad};
  }
  function acquireKakao(explicit){
    const kakao=explicit||(typeof window!=='undefined'&&window.kakao);
    if(!kakao||!kakao.maps||!kakao.maps.Map)throw new Error('Kakao Maps SDK unavailable');
    return kakao.maps;
  }
  function defaultMarkerImage(maps,role,label){
    if(!maps.MarkerImage||!maps.Size)return null;
    const color=role==='start'?'#2064c4':role==='end'?'#c83c3c':'#d34086';
    const title=role==='start'?'출':role==='end'?'도':label;
    const escaped=String(title).replace(/[&<>"']/g,'');
    const svg='<svg xmlns="http://www.w3.org/2000/svg" width="36" height="48" viewBox="0 0 36 48">'+
      '<path d="M18 2C9 2 2 9 2 18c0 13 16 28 16 28s16-15 16-28C34 9 27 2 18 2" fill="'+color+'" stroke="#fff" stroke-width="2"/>'+
      '<text x="18" y="23" text-anchor="middle" font-family="sans-serif" font-size="13" font-weight="bold" fill="white">'+escaped+'</text></svg>';
    const anchor=maps.Point?{offset:new maps.Point(18,46)}:{};
    return new maps.MarkerImage('data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(svg),new maps.Size(36,48),anchor);
  }
  class Engine{
    constructor(opts){
      opts=opts||{};
      if(!opts.container)throw new Error('container is required');
      this.container=opts.container;
      this.maps=acquireKakao(opts.kakao);
      this.course=normalize(opts.course);
      this.mode=Object.values(TYPES).includes(opts.mode)?opts.mode:TYPES.RESULT;
      this.current=point(opts.currentLocation);
      this.cardHeight=Number(opts.cardHeight)||0;
      this.topInset=Math.max(0,Number(opts.topInset)||0);
      this.cardCollapsed=opts.cardCollapsed!==undefined?!!opts.cardCollapsed:this.mode===TYPES.PLACE_VIEW;
      this.markers=[];this.lines=[];this.listeners=[];this.destroyed=false;
      this.requestId=0;this.userMoved=false;this.callbacks=opts.callbacks||{};
      this.showParishWaypoints=opts.showParishWaypoints!==false;
      this.domListeners=[];
      const initial=fitPoints(this.mode,this.course,this.current)[0]||{lat:36.3,lng:127.6};
      this.map=new this.maps.Map(this.container,{center:new this.maps.LatLng(initial.lat,initial.lng),level:7});
      this._drawCourseMarkers();
      this.fitInitial();
      // A wheel, touch or pointer operation belongs to the user; later card/route
      // updates must never reset the viewport they deliberately adjusted.
      const onInteraction=()=>{this.userMoved=true;};
      for(const eventName of ['wheel','pointerdown','touchstart']){
        this.container.addEventListener(eventName,onInteraction,{passive:true});
        this.domListeners.push([eventName,onInteraction]);
      }
      if(this.maps.event&&this.maps.event.addListener){
        const onMove=()=>{this.userMoved=true;};
        this.maps.event.addListener(this.map,'dragstart',onMove);
        this.listeners.push(['dragstart',onMove,this.map,'map']);
      }
    }
    _latlng(p){return new this.maps.LatLng(p.lat,p.lng);}
    _marker(p,role,label){
      const markerOpts={map:this.map,position:this._latlng(p),title:p.name||label};
      if(typeof this.callbacks.makeMarkerImage==='function'){
        const image=this.callbacks.makeMarkerImage({point:p,role,label,maps:this.maps});
        if(image)markerOpts.image=image;
      } else {
        const image=defaultMarkerImage(this.maps,role,label);
        if(image)markerOpts.image=image;
      }
      const mark=new this.maps.Marker(markerOpts);
      this.markers.push(mark);
      if(this.callbacks.onMarkerClick&&this.maps.event){
        const handle=()=>this.callbacks.onMarkerClick({point:p,role,label});
        this.maps.event.addListener(mark,'click',handle);
        this.listeners.push(['click',handle,mark,'marker']);
      }
      return mark;
    }
    _drawCourseMarkers(){
      if(this.course.start)this._marker(this.course.start,'start','출발');
      this.course.waypoints.forEach((p,i)=>{if(p.kind!=='parish'||this.showParishWaypoints)this._marker(p,'waypoint',String(i+1));});
      if(this.course.end)this._marker(this.course.end,'end','도착');
    }
    setShowParishWaypoints(visible){
      if(this.destroyed||this.showParishWaypoints===!!visible)return;
      this.showParishWaypoints=!!visible;
      this._clearListeners('marker');
      this.markers.forEach(m=>m.setMap(null));this.markers=[];
      this._drawCourseMarkers();
      // Visibility changes must never change the user's map position or zoom.
    }
    fitInitial(){
      if(this.destroyed)return;
      const pts=fitPoints(this.mode,this.course,this.current);
      if(!pts.length)return;
      if(typeof this.map.relayout==='function')this.map.relayout();
      if(pts.length===1){
        this.map.setCenter(this._latlng(pts[0]));
        this.map.setLevel(6); // comfortable selectable area, not excessive close-up
        // Shift the one point into the visible region above the expanded card.
        if(typeof this.map.panBy==='function'){
          const pad=boundsPadding(this.container,this.cardHeight,this.cardCollapsed,this.topInset);
          this.map.panBy(0,Math.round((pad.bottom-pad.top)/2));
        }
        return;
      }
      const b=new this.maps.LatLngBounds();pts.forEach(p=>b.extend(this._latlng(p)));
      const pad=boundsPadding(this.container,this.cardHeight,this.cardCollapsed,this.topInset);
      // Kakao Map.setBounds(bounds, paddingTop, paddingRight, paddingBottom, paddingLeft)
      this.map.setBounds(b,pad.top,pad.right,pad.bottom,pad.left);
    }
    setCardState(collapsed,height){
      if(this.destroyed)return;
      this.cardCollapsed=!!collapsed;
      if(height!==undefined)this.cardHeight=Number(height)||0;
      // Never override a viewport moved deliberately by the user.
      if(!this.userMoved)this.fitInitial();
    }
    setCurrentLocation(place){
      if(this.destroyed)return false;
      const candidate=point(place);
      if(!candidate)return false;
      this.current=candidate;
      if(!this.userMoved&&this.mode===TYPES.PICK&&!this.course.waypoints.length
        &&!this.course.start&&!this.course.end)this.fitInitial();
      return true;
    }
    setCourse(data,options){
      if(this.destroyed)return;
      this.cancelPending();this.clearGraphics();this._clearListeners('marker');this.course=normalize(data);
      this._drawCourseMarkers();if(!options||options.fit!==false){if(!this.userMoved)this.fitInitial();}
    }
    drawRouteSegments(segments){
      if(this.destroyed)return;
      this.lines.forEach(l=>l.setMap(null));this.lines=[];
      const all=Array.isArray(segments)?segments:[];
      const budget=Math.max(80,Math.floor(1200/Math.max(1,all.length)));
      all.forEach(segment=>{
        const raw=Array.isArray(segment)?segment:[];
        if(raw.length<2)return;
        // API distance/time is unchanged. Thin only visual geometry so a long
        // route cannot monopolize the Fold's WebView with thousands of vertices.
        const step=Math.max(1,Math.ceil((raw.length-1)/(budget-1)));
        const picked=[];
        for(let i=0;i<raw.length;i+=step){const p=point(raw[i]);if(p)picked.push(this._latlng(p));}
        const end=point(raw[raw.length-1]);
        if(end && ((raw.length-1)%step!==0))picked.push(this._latlng(end));
        if(picked.length<2)return;
        this.lines.push(new this.maps.Polyline({map:this.map,path:picked,strokeWeight:5,strokeColor:'#1C6CB3',strokeOpacity:0.9}));
      });
      // Route calculation must not overwrite the course-based zoom.
    }
    beginRequest(){return ++this.requestId;}
    isCurrentRequest(token){return !this.destroyed&&token===this.requestId;}
    cancelPending(){this.requestId+=1;}
    _clearListeners(group){
      if(this.maps.event&&this.maps.event.removeListener){
        this.listeners.filter(l=>l[3]===group).forEach(([ev,fn,target])=>this.maps.event.removeListener(target||this.map,ev,fn));
      }
      this.listeners=this.listeners.filter(l=>l[3]!==group);
    }
    clearGraphics(){
      this.lines.forEach(l=>l.setMap(null));this.markers.forEach(m=>m.setMap(null));
      this.lines=[];this.markers=[];
    }
    destroy(){
      if(this.destroyed)return;
      this.cancelPending();this.destroyed=true;this.clearGraphics();
      if(this.maps.event&&this.maps.event.removeListener){
        this.listeners.forEach(([ev,fn,target])=>this.maps.event.removeListener(target||this.map,ev,fn));
      }
      this.listeners=[];
      this.domListeners.forEach(([name,listener])=>this.container.removeEventListener(name,listener));
      this.domListeners=[];this.map=null;
    }
  }
  return Object.freeze({Engine,TYPES,normalize,fitPoints,boundsPadding});
});
