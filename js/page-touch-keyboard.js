(function(){
  'use strict';
  var cfg = window.OAI_PAGE_TOUCH_CONFIG || {};
  var flag = cfg.flag || '__oaiPageTouchKeyboard20260506';
  if(window[flag]) return;
  window[flag] = true;

  var actionDelay = Number(cfg.actionDelayMs || cfg.delayMs || 55);
  var feedbackMs = Number(cfg.feedbackMs || 170);
  var pressDelayMs = Number(cfg.pressDelayMs || cfg.delayMs || 85);
  var moveCancelPx = Number(cfg.moveCancelPx || 7);
  var delayedSelectors = cfg.delayedSelectors || '';
  var directSelectors = cfg.directSelectors || 'a,input,textarea,select,label';
  var instantPress = !!cfg.instantPress;
  var touchableClass = cfg.touchableClass || 'oai-touchable';
  var pressedClass = cfg.pressedClass || 'oai-touch-pressed';
  var pressingKey = cfg.pressingKey || '__oaiPressing';
  var canceledUntilKey = cfg.canceledUntilKey || '__oaiTouchCanceledUntil';
  var clickDelayKey = cfg.clickDelayKey || '__oaiClickDelay';
  var blurOnOutside = !!cfg.blurOnOutside;
  var enterBlurSelectors = cfg.enterBlurSelectors || 'input,textarea';

  var activeTouch = null;

  function closest(el, sel){
    try{return el && el.closest && sel ? el.closest(sel) : null;}catch(e){return null;}
  }
  function clearPress(el){
    if(!el) return;
    try{el.classList.remove(pressedClass);}catch(e){}
    el[pressingKey] = false;
  }
  function press(el){
    if(!el || el[pressingKey]) return;
    el[pressingKey] = true;
    try{el.classList.add(touchableClass, pressedClass);}catch(e){}
    setTimeout(function(){ clearPress(el); }, feedbackMs);
  }
  function cancelActive(){
    if(!activeTouch) return;
    activeTouch.canceled = true;
    if(activeTouch.timer){ clearTimeout(activeTouch.timer); activeTouch.timer = null; }
    clearPress(activeTouch.el);
    try{ activeTouch.el[canceledUntilKey] = Date.now() + 350; }catch(e){}
  }

  document.addEventListener('pointerdown', function(e){
    if(closest(e.target, directSelectors)) return;
    var el = closest(e.target, delayedSelectors);
    if(!el) return;
    if(instantPress){ press(el); return; }
    activeTouch = {el:el,id:e.pointerId,x:e.clientX,y:e.clientY,canceled:false,timer:null};
    activeTouch.timer = setTimeout(function(){
      if(activeTouch && activeTouch.el === el && !activeTouch.canceled) press(el);
    }, pressDelayMs);
  }, true);

  if(!instantPress){
    document.addEventListener('pointermove', function(e){
      if(!activeTouch || activeTouch.id !== e.pointerId) return;
      var dx = Math.abs(e.clientX - activeTouch.x);
      var dy = Math.abs(e.clientY - activeTouch.y);
      if(dx > moveCancelPx || dy > moveCancelPx) cancelActive();
    }, true);
    document.addEventListener('pointercancel', cancelActive, true);
    document.addEventListener('pointerup', function(e){
      if(!activeTouch || activeTouch.id !== e.pointerId) return;
      if(activeTouch.timer){ clearTimeout(activeTouch.timer); activeTouch.timer = null; }
      if(activeTouch.canceled) clearPress(activeTouch.el);
      activeTouch = null;
    }, true);
  }

  document.addEventListener('click', function(e){
    if(e.__oaiTouchReplay) return;
    if(closest(e.target, directSelectors)) return;
    var el = closest(e.target, delayedSelectors);
    if(!el) return;
    if(el[canceledUntilKey] && Date.now() < el[canceledUntilKey]){
      e.preventDefault();
      if(e.stopImmediatePropagation) e.stopImmediatePropagation();
      return;
    }
    if(el[clickDelayKey]) return;
    e.preventDefault();
    if(e.stopImmediatePropagation) e.stopImmediatePropagation();
    press(el);
    el[clickDelayKey] = true;
    setTimeout(function(){
      try{
        var ev = new MouseEvent('click', {bubbles:true,cancelable:true,view:window});
        ev.__oaiTouchReplay = true;
        el.dispatchEvent(ev);
      }catch(err){
        try{ el.click(); }catch(_e){}
      }
      setTimeout(function(){ el[clickDelayKey] = false; }, 0);
    }, actionDelay);
  }, true);

  function disableKeyboardSuggestions(root){
    root = root || document;
    var nodes = root.querySelectorAll ? root.querySelectorAll('input[type="text"], input[type="search"], input:not([type]), textarea') : [];
    nodes.forEach(function(el){
      if(el.type === 'password' || el.type === 'number' || el.type === 'tel' || el.type === 'email') return;
      el.setAttribute('autocomplete','off');
      el.setAttribute('autocorrect','off');
      el.setAttribute('autocapitalize','off');
      el.setAttribute('spellcheck','false');
      el.setAttribute('enterkeyhint','done');
    });
  }
  function isEditable(el){
    return !!(el && (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA' || el.tagName === 'SELECT' || el.isContentEditable));
  }
  function blurActiveEditable(){
    try{
      var a = document.activeElement;
      if(isEditable(a) && a.blur) a.blur();
    }catch(e){}
  }
  document.addEventListener('keydown', function(e){
    if(e.key !== 'Enter') return;
    var el = closest(e.target, enterBlurSelectors);
    if(!el) return;
    setTimeout(blurActiveEditable, 0);
  }, true);
  if(blurOnOutside){
    document.addEventListener('click', function(e){
      var a = document.activeElement;
      if(isEditable(a) && e.target !== a){
        blurActiveEditable();
      }
    }, true);
  }

  disableKeyboardSuggestions(document);
  document.addEventListener('DOMContentLoaded', function(){ disableKeyboardSuggestions(document); });
  try{
    var mo = new MutationObserver(function(muts){
      for(var i=0;i<muts.length;i++){
        for(var j=0;j<muts[i].addedNodes.length;j++){
          var n = muts[i].addedNodes[j];
          if(n && n.nodeType === 1) disableKeyboardSuggestions(n);
        }
      }
    });
    mo.observe(document.documentElement,{childList:true,subtree:true});
  }catch(e){}
})();
