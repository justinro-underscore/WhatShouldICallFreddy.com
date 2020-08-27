import React from 'react';
import '../index.css';

function Header(props) {
  return (
    <div>
      <h1 className="header-title">WhatShouldICallFreddy.com Admin Page</h1>
      <p className="header-subtitle">Hey you shouldn't be here</p>
    </div>
  )
}

export default class Admin extends React.Component {
  render() {
    return <Header />;
  }
}