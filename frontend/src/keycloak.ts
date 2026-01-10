import Keycloak, { type KeycloakInitOptions } from 'keycloak-js';

export const keycloak = new Keycloak({
    url: import.meta.env.VITE_KEYCLOAK_SERVER_URL || 'http://localhost:8080/',
    realm: import.meta.env.VITE_KEYCLOAK_REALM || 'lefort-realm',
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'frontend-react',
});
 
export const initOptions: KeycloakInitOptions = {
    onLoad: 'login-required',      // type KeycloakOnLoad
    pkceMethod: 'S256',            // OK
    checkLoginIframe: false,       // option recommandée avec Vite
    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
    flow: 'standard',              // type KeycloakFlow
};