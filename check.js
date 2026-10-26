const fs=require('fs'),vm=require('vm'),assert=require('assert');
const src=fs.readFileSync(__dirname+'/app.js','utf8');
function extract(name){let st=src.indexOf('function '+name+'(');if(st<0)throw Error(name);let p=src.indexOf('{',st),level=0,quoted=null,escaped=false;for(let i=p;i<src.length;i++){let ch=src[i];if(quoted){if(escaped)escaped=false;else if(ch==='\\')escaped=true;else if(ch===quoted)quoted=null;continue}if('"\'`'.includes(ch)){quoted=ch;continue}if(ch==='{')level++;if(ch==='}'&&!--level)return src.slice(st,i+1)}throw Error('end '+name)}
const ctx={console, OAI_SHARED_VISIT_JOURNAL_GROUPS:[{shrineName:'여사울 성지',parishName:'여사울성지성당',parishDiocese:'대전'}],_normPilgrimageName:x=>String(x||'').replace(/\s+/g,'').trim(),_dailyIso:x=>new Date(x).toISOString().slice(0,10),_configuredMyParish:()=>({name:'월배성당',diocese:'대구'}),_validGpsPair:()=>false};vm.createContext(ctx);
for(let fn of ['_dailySamePlace','_dailyLinkedGpsPair','_dailyIsMyParish','_dailyIsSharedParish','_dailyCourseRegistrationDecisions','_dailyAutoEvents','_dailyAutoEligible','_uniqueCompletionStampPlaces'])vm.runInContext(extract(fn),ctx);
let d=Date.parse('2026-10-24T04:00:00Z');let p=(name,kind,placeId,diocese)=>({name,kind,placeId,diocese,date:'2026-10-24',at:d});let shrine=p('여사울 성지','shrine','s1','대전'), parish=p('여사울성지성당','parish','p1','대전'), gen=p('합덕성당','parish','p2','대전'),home=p('월배성당','parish','p3','대구');let check=0;
function test(label,fn){try{fn();check++;console.log('PASS',label)}catch(e){console.log('FAIL',label,e.message);process.exitCode=1}}
test('성지와 성당은 독립 스탬프',()=>assert.equal(ctx._dailySamePlace(shrine,parish),false));
test('같은 성지·같은 ID 중복',()=>assert.equal(ctx._dailySamePlace(shrine,{...shrine}),true));
test('확정 공통화는 코스 하나',()=>assert.equal(ctx._dailyAutoEvents([shrine,parish]).length,1));
test('성지 동행 일반 성당은 코스 등록',()=>assert.equal(ctx._dailyAutoEvents([shrine,gen]).length,2));
test('성당만 방문 코스 생성 금지',()=>assert.equal(ctx._dailyAutoEvents([gen]).length,0));
test('내 본당 코스 제외',()=>assert.equal(ctx._dailyAutoEvents([shrine,home]).length,1));
test('완료 대상 같은 장소 중복 없음',()=>assert.equal(ctx._uniqueCompletionStampPlaces([shrine,{...shrine}]).length,1));
console.log('TOTAL',check,'/ 7');
