import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '5aef38ee3c9661f118554278368c8c151628bade7740afc77d1ca9c8aa5babf0') {
    pending.push(import('./chunks/chunk-a8269f976859f6ae38c4dbf6396333fdc54097b34c6b27b91baa2e819a75a3e1.js'));
  }
  if (key === 'b53364d7be0569088534191439fc00d172d5f1fd6b4164f85469469ca8172196') {
    pending.push(import('./chunks/chunk-a8269f976859f6ae38c4dbf6396333fdc54097b34c6b27b91baa2e819a75a3e1.js'));
  }
  if (key === '9d8bc55a7b5ccbde73a3ab1d39c455b254be7d31ad13adbd99c83ab372a8d091') {
    pending.push(import('./chunks/chunk-282fb1b288bbc35c81660ed85d445f62127ac5b2a40bdae1a749c972f0705d63.js'));
  }
  if (key === 'e0e88391fc62b67a2c854882151d4c6cf7900192d36f9f0e02db1367af0e02b5') {
    pending.push(import('./chunks/chunk-a8269f976859f6ae38c4dbf6396333fdc54097b34c6b27b91baa2e819a75a3e1.js'));
  }
  if (key === '6dfe620561ba5553ae3c35c36b4d3f341cfbed87879588280f2a52dffe0e8fe8') {
    pending.push(import('./chunks/chunk-f6b4d56c39ca6b4af1360389004f7fe3a9c145cfa63ce42359a5a54650a520d6.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}