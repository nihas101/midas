import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'eb54e70712294a19c8b992820ec636da636d9842ee3c7bc5c52870ca295743c7') {
    pending.push(import('./chunks/chunk-e104a47912f75466461496137cd92c785e20d6459073aae2546024ae4a06a1c4.js'));
  }
  if (key === 'c13210b3013a2735dd6380c598f939d962b183b26ff944639dbddad2e8b15f5a') {
    pending.push(import('./chunks/chunk-e104a47912f75466461496137cd92c785e20d6459073aae2546024ae4a06a1c4.js'));
  }
  if (key === '3a4d361fbb219cef6332b4d9882ab0f7a8d7f8f00d662d1685db9ba28893ecd6') {
    pending.push(import('./chunks/chunk-e104a47912f75466461496137cd92c785e20d6459073aae2546024ae4a06a1c4.js'));
  }
  if (key === '4f122048b4e2b1aa57aed8b6dc5bca7ba9a1281d2628d2a2f995bd50d24ae418') {
    pending.push(import('./chunks/chunk-e1a4226b3478e5650301519f20ac69e0f1c26d5f94cd8cec27f4e65651caf175.js'));
  }
  if (key === '1d34c2d5fbc03bd95779a0a19924bb02d69d8b2f7283e21a1710be3e701bc069') {
    pending.push(import('./chunks/chunk-3b34ac9889c32e2e1ebeeaa6206f9c23a3c09d912d9eeeec76da88b11e21c65f.js'));
  }
  if (key === '0c0ae74f6249c0370d9c04925460b5fc29a22bf49f2ae4b0e325d316aa7f34d2') {
    pending.push(import('./chunks/chunk-abe3abbea5b1df6b717433914a667cf52d50f16557822da3d9a54fb15131855b.js'));
  }
  if (key === 'f87f6f62e3791029a12cdd3e21b3c8c28f5151ff33bdcb7a8b3e0d6ddcb1e453') {
    pending.push(import('./chunks/chunk-bc1b6a313d7f2c145cef2b7f9a7bf338535a820800dc4f2b2babf3804da7205c.js'));
  }
  if (key === 'ddeb0480098b829084cb93321f3880caa4416d6e9ecd683a53fe3af9107bfd19') {
    pending.push(import('./chunks/chunk-5ac587597a3d00a40cca74aab4ff40f57d956874f4ca2070177ef6652ecc3002.js'));
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