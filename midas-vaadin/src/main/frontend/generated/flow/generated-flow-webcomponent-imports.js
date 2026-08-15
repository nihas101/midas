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
  if (key === '3a4d361fbb219cef6332b4d9882ab0f7a8d7f8f00d662d1685db9ba28893ecd6') {
    pending.push(import('./chunks/chunk-3ecdcaa39f50099e185ae88482eae72a241572fd1b48c0eb5e030cfd8a8b5e49.js'));
  }
  if (key === 'c13210b3013a2735dd6380c598f939d962b183b26ff944639dbddad2e8b15f5a') {
    pending.push(import('./chunks/chunk-3ecdcaa39f50099e185ae88482eae72a241572fd1b48c0eb5e030cfd8a8b5e49.js'));
  }
  if (key === 'eb54e70712294a19c8b992820ec636da636d9842ee3c7bc5c52870ca295743c7') {
    pending.push(import('./chunks/chunk-3ecdcaa39f50099e185ae88482eae72a241572fd1b48c0eb5e030cfd8a8b5e49.js'));
  }
  if (key === '0c0ae74f6249c0370d9c04925460b5fc29a22bf49f2ae4b0e325d316aa7f34d2') {
    pending.push(import('./chunks/chunk-abe3abbea5b1df6b717433914a667cf52d50f16557822da3d9a54fb15131855b.js'));
  }
  if (key === '1d34c2d5fbc03bd95779a0a19924bb02d69d8b2f7283e21a1710be3e701bc069') {
    pending.push(import('./chunks/chunk-3b34ac9889c32e2e1ebeeaa6206f9c23a3c09d912d9eeeec76da88b11e21c65f.js'));
  }
  if (key === '4f122048b4e2b1aa57aed8b6dc5bca7ba9a1281d2628d2a2f995bd50d24ae418') {
    pending.push(import('./chunks/chunk-9f6ee70f90e573ae9013a1f9ed8c696cfd56bca88c66a4fa6dcfd11071c3cd9b.js'));
  }
  if (key === 'f87f6f62e3791029a12cdd3e21b3c8c28f5151ff33bdcb7a8b3e0d6ddcb1e453') {
    pending.push(import('./chunks/chunk-1cbcd1701567f7eab4522e32a5c3a1cb7a521001924068df25fdec47b1b92d20.js'));
  }
  if (key === 'ddeb0480098b829084cb93321f3880caa4416d6e9ecd683a53fe3af9107bfd19') {
    pending.push(import('./chunks/chunk-63c64ad9c907b51eec5eab3399db8470c4e694479bcdf88bfee1aef01ca375e7.js'));
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