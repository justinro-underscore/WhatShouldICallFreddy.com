import React from 'react';
import { Motion, spring } from 'react-motion';
import { withCookies, CookiesProvider } from 'react-cookie';
import './index.css';
import LoadingSpinner from './res/loading.gif';
import Poll from './components/Poll';
import NewNameForm from './components/NewNameForm';
import NameResultsChart from './components/NameResultsChart';

function Header(props) {
  return (
    <div>
      <h1 className="header-title">WhatShouldICallFreddy.com</h1>
      <p className="header-subtitle">Help me come up with a new name for my dog!</p>
    </div>
  )
}

class Body extends React.Component {
  constructor(props) {
    super(props);
    this.cookies = this.props.cookies;
    this.state = {
      loading: true,
      error: null
    }
  }

  componentDidMount() {
    try {
      fetch("http://localhost:8080/heartbeat")
        .then(
          (res) => this.setState({ loading: false }),
          (error) => this.apiError(error)
        );
    }
    catch (e) {
      this.apiError(e);
    }
  }

  /**
   * Handles what should happen when there is an error from the API
   * @param {json} error Describes info from the error
   */
  apiError(error) {
    console.log(error);
    this.setState({
      loading: false,
      error: String(error)
    });
  }

  render() {
    if (this.state.loading) {
      return <img src={LoadingSpinner} alt="Loading..." />;
    }
    else if (this.state.error) {
      return (
        <div>
          <h2>Uh oh!</h2>
          <p>Looks like there's been an error. Please bear with us</p>
          <p>{ this.state.error }</p>
        </div>
      )
    }
    else {
      return (
        <div>
          <Poll cookies={ this.cookies }/>
          <NewNameForm />
          <NameResultsChart />
        </div>
      );
    }
  }
}

class App extends React.Component {
  render() {
    return (
      <CookiesProvider>
        <Motion
          defaultStyle={{y: 20, opacity: 0}}
          style={{y: spring(0, {stiffness: 150}), opacity: spring(1, {stiffness: 80})}}
        >
          {style => (
            <div style={{transform: `translateY(${style.y}px)`, opacity: style.opacity}}>
              <Header />
              <Body cookies={ this.props.cookies }/>
            </div>
          )}
        </Motion>
      </CookiesProvider>
    );
  }
}

export default withCookies(App);