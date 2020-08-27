import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import WhatShouldICallFreddy from './whatshouldicallfreddy/WhatShouldICallFreddy';
import Admin from './admin/Admin';
import './index.css';

export default class App extends React.Component {
  render() {
    return (
      <Router>
        <Switch>
          <Route exact path="/">
            <WhatShouldICallFreddy />
          </Route>
          <Route path="/admin">
            <Admin />
          </Route>
        </Switch>
      </Router>
    );
  }
}