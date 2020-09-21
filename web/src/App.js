import React from 'react';
import { withCookies, CookiesProvider } from 'react-cookie';
import WhatShouldICallFreddy from './whatshouldicallfreddy/WhatShouldICallFreddy';
import Admin from './admin/Admin';
import './index.css';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.admin = this.props.cookies.get("admin");
  }

  // Heroku doesn't like it when we use the Router, so instead we just have a cookie instead. Probably a bit more secure also (as long as I don't hardcode the cookie name like I just did)
  render() {
    return (
      <CookiesProvider>
        {!(this.admin && this.admin !== "0") ? <WhatShouldICallFreddy cookies={this.props.cookies} /> : <Admin />}
      </CookiesProvider>
    );
  }
}

export default withCookies(App);
