import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import $cssFromFile_0 from '@vaadin/vaadin-lumo-styles/lumo.css?inline';

injectGlobalWebcomponentCss($cssFromFile_0.toString());
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';
const loadOnDemand = (key) => {
  const pending = [];
  if (key === '6dfe620561ba5553ae3c35c36b4d3f341cfbed87879588280f2a52dffe0e8fe8') {
    pending.push(import('./chunks/chunk-bc1b6a313d7f2c145cef2b7f9a7bf338535a820800dc4f2b2babf3804da7205c.js'));
  }
  if (key === 'e0e88391fc62b67a2c854882151d4c6cf7900192d36f9f0e02db1367af0e02b5') {
    pending.push(import('./chunks/chunk-5ac587597a3d00a40cca74aab4ff40f57d956874f4ca2070177ef6652ecc3002.js'));
  }
  if (key === '161734883044a4cefc0c040233ce6618c53c79aaef96a3a3ec8cb302b77a942f') {
    pending.push(import('./chunks/chunk-d7b9557992a17974c3b23b4c2b2d921e4fd5f653cdb560defec7db11585c61d1.js'));
  }
  if (key === '5aef38ee3c9661f118554278368c8c151628bade7740afc77d1ca9c8aa5babf0') {
    pending.push(import('./chunks/chunk-e104a47912f75466461496137cd92c785e20d6459073aae2546024ae4a06a1c4.js'));
  }
  if (key === '38c612ef09e88d9b18c87b215a1c60293db3b15e0de37bd71173d68cd0ddfc97') {
    pending.push(import('./chunks/chunk-c3ea5bd8ec451ed8361604fac3e314b8460289845d97d2a07f800c8eb34f509f.js'));
  }
  if (key === '9fc4710b46ed8f328b460caa7c4cae866b3378488284544cf365790be45178e8') {
    pending.push(import('./chunks/chunk-e104a47912f75466461496137cd92c785e20d6459073aae2546024ae4a06a1c4.js'));
  }
  if (key === 'b53364d7be0569088534191439fc00d172d5f1fd6b4164f85469469ca8172196') {
    pending.push(import('./chunks/chunk-e104a47912f75466461496137cd92c785e20d6459073aae2546024ae4a06a1c4.js'));
  }
  if (key === '9d8bc55a7b5ccbde73a3ab1d39c455b254be7d31ad13adbd99c83ab372a8d091') {
    pending.push(import('./chunks/chunk-e1a4226b3478e5650301519f20ac69e0f1c26d5f94cd8cec27f4e65651caf175.js'));
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