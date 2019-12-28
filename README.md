# Box SDK Examples
This repository contains a collection examples showing how to use Box SDK in scala.

## Setup
### JWT Examples
[Prerequisite](https://developer.box.com/docs/setting-up-a-jwt-app)

1. Create an App - get client_id & client_secret
2. Set App to use JWT and set scopes and permissions as necessary
3. Generate RSA Key Pair
4. Add Public Key to App (remember to get keyId) - get keyId
5. Admin - Grant Access to app in admin console
6. Admin - Get enterprise_id from admin console

### OAUTH2 Examples
[Prerequisite](https://developer.box.com/docs/setting-up-an-oauth-app)

1. Create an App - get client_id & client_secret
2. Set App to use OAuth2, redirect_uri and set scopes and CORS domains

More OAuth documentation - [link](https://developer.box.com/reference#oauth-2-overview/)

## Credits
Examples here were adapted form this [github repo](https://github.com/DNSKishore/BoxJavaJWTExamples).

## License
Open sourced under the [The Unlicense](LICENSE.md).