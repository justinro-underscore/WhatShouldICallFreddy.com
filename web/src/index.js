import React from 'react';
import ReactDOM from 'react-dom';
import {Motion, spring} from 'react-motion';
import './index.css';
import FreddyPic from './res/Freddy2.jpg';

function Header(props) {
  return (
    <div>
      <h1 className="header-title">WhatShouldICallFreddy.com</h1>
      <p className="header-subtitle">Help me come up with a new name for my dog!</p>
    </div>
  )
}

function NameCard(props) {
  return (
    <div className="name-card-back">
      <p className="name-card-name">{props.name}</p>
    </div>
  );
}

function PollOptions(props) {
  return (
    <div className="poll-options-container">
      <p className="poll-option poll-option-yes" onClick={ () => props.voteFunc(true) }>YES</p>
      <hr className="poll-option-divider"/>
      <p className="poll-option poll-option-new-pic">NEW PICTURE</p>
      <hr className="poll-option-divider"/>
      <p className="poll-option poll-option-no" onClick={ () => props.voteFunc(false) }>NO</p>
    </div>
  );
}

class Poll extends React.Component {
  /**
   * Set up react component
   * @param {props} props 
   */
  constructor(props) {
    super(props);
    this.state = {
      name: "",
      nextNameIndex: 1,
      loading: true
    };
  }

  /**
   * After component mounts, fetch the new name
   */
  componentDidMount() {
    this.fetchName();
  }

  fetchName() {
    fetch(`http://localhost:8080/dognames/${ this.state.nextNameIndex }`)
      .then(
        (res) => {
          res.json().then(
            (resjson) => {
              this.setState({
                name: resjson.name,
                nextNameIndex: this.state.nextNameIndex + 1,
                loading: false
              });
            },
            (error) => this.apiError(error)
          );
        },
        (error) => this.apiError(error)
      );
  }

  voteOnName(voteIsYes) {
    console.log(`Vote: ${ voteIsYes }`);
    this.fetchName();
  }

  /**
   * Handles what should happen when there is an error from the API
   * @param {json} error Describes info from the error
   */
  apiError(error) {
    console.log(error); // TODO Figure out how to show this
  }


  render() {
    if (!this.state.loading) {
      return (
        <div className="poll-container">
          <p className="poll-header">Does this name fit?</p>
          <NameCard name={ this.state.name }/>
          <img className="poll-img" src={FreddyPic}></img>
          <PollOptions voteFunc={ (voteIsYes) => this.voteOnName(voteIsYes) }/>
        </div>
      );
    }
    else {
      return <div>Loading...</div>
    }
  }
}

function App(props) {
  return (
    <Motion
      defaultStyle={{y: 20, opacity: 0}}
      style={{y: spring(0, {stiffness: 150}), opacity: spring(1, {stiffness: 80})}}
    >
      {style => (
        <div style={{transform: `translateY(${style.y}px)`, opacity: style.opacity}}>
          <Header />
          <Poll />
        </div>
      )}
    </Motion>

    // <div>
    //   <Header />
    //   <Poll />
    // </div>
  )
}

// ========================================

ReactDOM.render(
  <App />,
  document.getElementById('root')
);
