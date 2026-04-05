import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import $cssFromFile_0 from '@vaadin/vaadin-lumo-styles/lumo.css?inline';

injectGlobalWebcomponentCss($cssFromFile_0.toString());
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
const loadOnDemand = (key) => {
  const pending = [];
  if (key === '6dfe620561ba5553ae3c35c36b4d3f341cfbed87879588280f2a52dffe0e8fe8') {
    pending.push(import('./chunks/chunk-070de8dfe0e970812364606d6221c979ba99840c399c39005a1176f44aff7b55.js'));
  }
  if (key === '161734883044a4cefc0c040233ce6618c53c79aaef96a3a3ec8cb302b77a942f') {
    pending.push(import('./chunks/chunk-017b6d6837247a644c47728ae869ed6f94017fbfe74189bc5b68ccbe17c9199f.js'));
  }
  if (key === '38c612ef09e88d9b18c87b215a1c60293db3b15e0de37bd71173d68cd0ddfc97') {
    pending.push(import('./chunks/chunk-a3378955c1ab99b477e49a4e7c1eaa7af483dbd15ed3909c5297b6646e613b19.js'));
  }
  if (key === '5aef38ee3c9661f118554278368c8c151628bade7740afc77d1ca9c8aa5babf0') {
    pending.push(import('./chunks/chunk-455961fa7cba584e5c9a3d65783a11b76f28ce3debc3cd96f4e8bef1175d2c44.js'));
  }
  if (key === '9d8bc55a7b5ccbde73a3ab1d39c455b254be7d31ad13adbd99c83ab372a8d091') {
    pending.push(import('./chunks/chunk-2c2e7f9ef2b5176aa72c95bf09c912ce7686e735e574cf7fd3ed57268adaa4e1.js'));
  }
  if (key === 'b53364d7be0569088534191439fc00d172d5f1fd6b4164f85469469ca8172196') {
    pending.push(import('./chunks/chunk-455961fa7cba584e5c9a3d65783a11b76f28ce3debc3cd96f4e8bef1175d2c44.js'));
  }
  if (key === '9fc4710b46ed8f328b460caa7c4cae866b3378488284544cf365790be45178e8') {
    pending.push(import('./chunks/chunk-455961fa7cba584e5c9a3d65783a11b76f28ce3debc3cd96f4e8bef1175d2c44.js'));
  }
  if (key === 'e0e88391fc62b67a2c854882151d4c6cf7900192d36f9f0e02db1367af0e02b5') {
    pending.push(import('./chunks/chunk-455961fa7cba584e5c9a3d65783a11b76f28ce3debc3cd96f4e8bef1175d2c44.js'));
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