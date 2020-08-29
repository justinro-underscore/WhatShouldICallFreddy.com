import React from 'react';
import '../index.css';
import './admin.css';
import { fetchApi } from '../utils/utils';

function Header() {
  return (
    <div>
      <h1 className="header-title">WhatShouldICallFreddy.com Admin Page</h1>
      <p className="header-subtitle">Hey you shouldn't be here</p>
    </div>
  )
}

class DeleteDogName extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      id: null
    };
  }

  componentWillReceiveProps(newProps) {
    if (this.state.id === null) {
      this.setState({id: newProps.dogNames[0].id});
    }
  }

  handleChange(event) {
    this.setState({id: event.target.value});
  }

  deleteId(event) {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dognames/${ this.state.id }`,
      admin: this.props.adminPassword,
      requestType: "DELETE",
      resType: "text",
      resCallback: {
        200: {
          callback: (res) => window.location.reload()
        },
        401: {
          callback: (res) => document.getElementById("admin-password").focus()
        }
      }
    });
    event.preventDefault();
  }

  render() {
    if (this.state.id) {
      return (
        <div className="form-container">
          <div className="form-row">
            <label className="form-input">ID
              <select name="id" value={this.state.id} onChange={(event) => this.handleChange(event)}>
                {this.props.dogNames && this.props.dogNames.map((dogName) => <option value={dogName.id} key={dogName.id}>{dogName.id + ": " + dogName.name}</option>)}
              </select>
            </label>
            <button onClick={(event) => this.deleteId(event)}>Delete!</button>
          </div>
        </div>
      );
    }
    return <p>Loading...</p>;
  }
}

class UpdateDogName extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      id: null,
      name: "",
      yesVotes: "",
      noVotes: ""
    }
  }

  componentWillReceiveProps(newProps) {
    if (this.state.id === null) {
      this.setState({id: newProps.dogNames[0].id});
    }
  }

  handleChange(event) {
    const {name, value} = event.target;
    switch (name) {
      case "id":
        if (/^[0-9]*$/.test(value)) {
          this.setState({id: value});
        }
        break;
      case "name":
        if (/^[0-9a-zA-Z ]*$/.test(value)) {
          this.setState({name: value});
        }
        break;
      case "yesVotes":
        if (/^[0-9]*$/.test(value)) {
          this.setState({yesVotes: value});
        }
        break;
      case "noVotes":
        if (/^[0-9]*$/.test(value)) {
          this.setState({noVotes: value});
        }
        break;
      default:
        console.error(`Name not supported! ${name}`);
    }
  }

  updateId(event) {
    const newName = {
      name: this.state.name !== "" ? this.state.name : null,
      yesVotes: this.state.yesVotes !== "" ? Number(this.state.yesVotes) : null,
      noVotes: this.state.noVotes !== "" ? Number(this.state.noVotes) : null
    };
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dognames/${ this.state.id }`,
      admin: this.props.adminPassword,
      requestType: "PUT",
      body: newName,
      resCallback: {
        200: {
          callback: (res) => window.location.reload()
        },
        401: {
          callback: (res) => document.getElementById("admin-password").focus()
        }
      }
    });
    event.preventDefault();
  }

  render() {
    if (this.state.id) {
      return (
        <div className="form-container">
          <div className="form-row">
            <label className="form-input">ID
              <select name="id" value={this.state.id} onChange={(event) => this.handleChange(event)}>
                {this.props.dogNames && this.props.dogNames.map((dogName) => <option value={dogName.id} key={dogName.id}>{dogName.id + ": " + dogName.name}</option>)}
              </select>
            </label>
            <label className="form-input" style={{flexGrow: "6"}}>Name
              <input type="input" name="name" value={this.state.name} onChange={(event) => this.handleChange(event)} />
            </label>
          </div>
          <div className="form-row">
            <label className="form-input">Yes Votes
              <input type="input" name="yesVotes" className="form-input-input" value={this.state.yesVotes} onChange={(event) => this.handleChange(event)} />
            </label>
            <label className="form-input">No Votes
              <input type="input" name="noVotes" className="form-input-input" value={this.state.noVotes} onChange={(event) => this.handleChange(event)} />
            </label>
            <button onClick={(event) => this.updateId(event)}>Update!</button>
          </div>
        </div>
      );
    }
    return <p>Loading...</p>;
  }
}

class ControlPanel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      adminPassword: "",
      dogNames: []
    };
  }

  componentDidMount() {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: "dognames",
      requestType: "GET",
      resCallback: (res) => {
        this.setState({dogNames: res});
      }
    });
  }

  handleChange(event) {
    this.setState({ adminPassword: event.target.value });
    this.adminPassword = event.target.value;
  }

  render() {
    return (
      <div className="control-panel-wrapper">
        <h2 className="control-panel-header">Control Panel</h2>
        <p className="control-panel-desc">Yeah I know it's ugly, but you shouldn't be here anyways</p>
        <form>
          <label>
            Password:
            <input type="text" value={ this.state.adminPassword } id="admin-password" className="password-input" onChange={ (event) => this.handleChange(event) } />
          </label>
          <DeleteDogName adminPassword={ this.state.adminPassword } dogNames={ this.state.dogNames } />
          <UpdateDogName adminPassword={ this.state.adminPassword } dogNames={ this.state.dogNames } />
        </form>
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