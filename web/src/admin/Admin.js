import React from 'react';
import '../index.css';
import './admin.css';
import { fetchApi } from '../utils/utils';

function Header(props) {
  return (
    <div>
      <h1 className="header-title">WhatShouldICallFreddy.com Admin Page</h1>
      <p className="header-subtitle">Hey you shouldn't be here</p>
    </div>
  )
}

class DeleteDogName extends React.Component {
  deleteId(id) {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dognames/${ id }`,
      admin: true,
      requestType: "DELETE",
      callback: (res) => console.log(res.status)
    });
  }

  render() {
    return <button onClick={() => this.deleteId(16)}>Delete!</button>;
  }
}

class UpdateDogName extends React.Component {
  updateId(id, newName) {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dognames/${ id }`,
      requestType: "GET",
      resCallback: (res) => {
        fetchApi({
          env: process.env.NODE_ENV,
          endpoint: `dognames/${ id }`,
          admin: true,
          requestType: "PUT",
          body: {
            name: newName.name || res.name,
            yesVotes: newName.yesVotes || res.yesVotes,
            noVotes: newName.noVotes || res.noVotes
          },
          callback: (res) => console.log(res.status)
        });
      }
    });
  }

  render() {
    return <button onClick={() => this.updateId(15, {name: "Joe"})}>Update!</button>;
  }
}

class ControlPanel extends React.Component {
  render() {
    return (
      <div className="control-panel-wrapper">
        <h2 className="control-panel-header">Control Panel</h2>
        <DeleteDogName />
        <UpdateDogName />
      </div>
    );
  }
}

export default class Admin extends React.Component {
  render() {
    return (
      <div>
        <Header />
        <ControlPanel />
      </div>
    );
  }
}