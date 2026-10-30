/* Pilgrimage V2 data/return boundary. No app storage, DOM or legacy map access. */
(function(root,factory){
 const api=factory();
 if(typeof module==='object'&&module.exports)module.exports=api;
 if(root)root.CatholicPilgrimageContractV2=api;
})(typeof window!=='undefined'?window:null,function(){
 'use strict';
 const VALID_DESTINATIONS=new Set(['completed-detail','revisit-detail','planned-detail','active-detail','planning-editor','place-info']);
 const VALID_KINDS=new Set(['pick','result','place-view']);
 function point(value){
  if(!value||typeof value!=='object')return null;
  const lat=Number(value.lat??value.latitude??value.y),lng=Number(value.lng??value.longitude??value.x);
  if(!Number.isFinite(lat)||!Number.isFinite(lng)||Math.abs(lat)>90||Math.abs(lng)>180||(lat===0&&lng===0))return null;
  return {id:value.id??value.placeId??null,name:String(value.name??value.placeName??value.title??'장소'),lat,lng,type:String(value.type??value.category??'')};
 }
 function course(source){
  const data=source||{},ends=data.endpoints||{};
  const raw=Array.isArray(data.waypoints)?data.waypoints:Array.isArray(data.places)?data.places:[];
  return {start:point(data.start??ends.start),end:point(data.end??ends.end),waypoints:raw.map(point).filter(Boolean)};
 }
 function validate(spec){
  const value=spec||{};
  if(!VALID_KINDS.has(value.kind))throw Error('지원하지 않는 순례 지도 화면');
  if(!VALID_DESTINATIONS.has(value.returnTo))throw Error('복귀 대상이 지정되지 않았습니다.');
  if(value.kind==='place-view'&&value.returnTo!=='place-info')throw Error('지도에서 보기는 상세카드로 돌아가야 합니다.');
  if(value.kind==='pick'&&!['completed-detail','planned-detail','planning-editor'].includes(value.returnTo))throw Error('지도에서 추가의 복귀 대상이 올바르지 않습니다.');
  if(value.kind==='result'&&value.returnTo==='place-info')throw Error('상세카드는 읽기 전용 지도에서 보기로 연결합니다.');
  const normalized=course(value.course);
  if(normalized.waypoints.length>10)throw Error('경유지 최대 10곳을 초과했습니다.');
  return Object.freeze({kind:value.kind,returnTo:value.returnTo,editable:value.kind==='pick'||value.editable===true,course:normalized});
 }
 function createSession(spec,callbacks){
  const config=validate(spec),cb=callbacks||{};
  if(typeof cb.onBack!=='function')throw Error('실제 복귀 콜백이 필요합니다.');
  if(config.editable&&typeof cb.onChange!=='function')throw Error('편집 내용 전달 콜백이 필요합니다.');
  let draft=course(config.course),closed=false;
  return Object.freeze({
   config,
   getDraft:()=>course(draft),
   changed(next){if(closed)throw Error('종료된 순례 지도');draft=course(next);if(config.editable)cb.onChange(course(draft));},
   back(){if(closed)return false;closed=true;cb.onBack({returnTo:config.returnTo,course:course(draft)});return true;},
   isClosed:()=>closed
  });
 }
 return Object.freeze({point,course,validate,createSession});
});
