import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '0c0ae74f6249c0370d9c04925460b5fc29a22bf49f2ae4b0e325d316aa7f34d2') {
    pending.push(import('./chunks/chunk-ff73fde31524ab0841a27aa129870c996ad08d966a301f2c0753a9ed39818821.js'));
  }
  if (key === 'f87f6f62e3791029a12cdd3e21b3c8c28f5151ff33bdcb7a8b3e0d6ddcb1e453') {
    pending.push(import('./chunks/chunk-31b25bb48173e30355fcde526a4274855102249b3d1c9430bf7e87c1ac665bd6.js'));
  }
  if (key === 'ddeb0480098b829084cb93321f3880caa4416d6e9ecd683a53fe3af9107bfd19') {
    pending.push(import('./chunks/chunk-b1191aae19a42444379293cb95a658d2fe7c2a4f998437dee035e84c60ea0e05.js'));
  }
  if (key === '3a4d361fbb219cef6332b4d9882ab0f7a8d7f8f00d662d1685db9ba28893ecd6') {
    pending.push(import('./chunks/chunk-ea3f4819a42e4709c22cba9446a09e7b76438c2608949ed89753eb0564681972.js'));
  }
  if (key === 'eb54e70712294a19c8b992820ec636da636d9842ee3c7bc5c52870ca295743c7') {
    pending.push(import('./chunks/chunk-ea3f4819a42e4709c22cba9446a09e7b76438c2608949ed89753eb0564681972.js'));
  }
  if (key === '1d34c2d5fbc03bd95779a0a19924bb02d69d8b2f7283e21a1710be3e701bc069') {
    pending.push(import('./chunks/chunk-88af31e1d7c4931e2a0f8090c06fa08ca1752e32e8bf233f0717c516f70b1cfa.js'));
  }
  if (key === 'c13210b3013a2735dd6380c598f939d962b183b26ff944639dbddad2e8b15f5a') {
    pending.push(import('./chunks/chunk-ea3f4819a42e4709c22cba9446a09e7b76438c2608949ed89753eb0564681972.js'));
  }
  if (key === '4f122048b4e2b1aa57aed8b6dc5bca7ba9a1281d2628d2a2f995bd50d24ae418') {
    pending.push(import('./chunks/chunk-2fcfa0a6726c18550450b14d547d704be66ead95576f56641d18f6436f99c793.js'));
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