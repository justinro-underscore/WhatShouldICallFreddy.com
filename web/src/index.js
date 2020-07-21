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
      <p className="poll-option poll-option-yes">YES</p>
      <hr className="poll-option-divider"/>
      <p className="poll-option poll-option-new-pic">NEW PICTURE</p>
      <hr className="poll-option-divider"/>
      <p className="poll-option poll-option-no">NO</p>
    </div>
  );
}

class Poll extends React.Component {
  render() {
    return (
      <div className="poll-container">
        <p className="poll-header">Does this name fit?</p>
        <NameCard name="Trevor"/>
        <img className="poll-img" src={FreddyPic}></img>
        <PollOptions />
      </div>
    )
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
