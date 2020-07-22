import React from 'react';
import ReactDOM from 'react-dom';
import {Motion, spring, presets} from 'react-motion';
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
    <div className="name-card-back" style={{transform: `rotate(${ props.rotation }deg)`}}>
      <p className="name-card-name">{ props.name === "" ? '____' : props.name }</p>
    </div>
  );
}

function PollOptions(props) {
  return (
    <div className="poll-options-container" style={{transform: `rotate(${ props.rotation }deg)`}}>
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
      loading: true,
      rotation: 20
    };
    this.resetRotationInterval(true);
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
              setTimeout(() => {
                clearInterval(this.rotationInterval);
                this.setState({rotation: 0});
              }, 100);
            },
            (error) => this.apiError(error)
          );
        },
        (error) => this.apiError(error)
      );
  }

  resetRotationInterval(startLeft) {
    this.setState({rotation: startLeft ? -20 : 20});
    this.rotationInterval = setInterval(() => this.setState({rotation: this.state.rotation === 20 ? -20 : 20}), 50);
  }

  voteOnName(voteIsYes) {
    console.log(`Vote: ${ voteIsYes }`);
    this.resetRotationInterval(voteIsYes);
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
    return (
      <Motion
        defaultStyle={{rot: 0, opacity: 1}}
        style={{rot: spring(this.state.rotation, presets.wobbly), opacity: spring(this.state.rotation === 0 ? 1 : 0)}}
      >
        {style => (
          <div className="poll-container">
            <div style={{opacity: style.opacity}}>
              <p className="poll-header">Does this name fit?</p>
              <NameCard name={ this.state.name } rotation={ style.rot }/>
              <img className="poll-img" src={FreddyPic}></img>
              <PollOptions voteFunc={ (voteIsYes) => this.voteOnName(voteIsYes) } rotation={ style.rot }/>
            </div>
          </div>
        )}
      </Motion>
    );
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
  )
}

// ========================================

ReactDOM.render(
  <App />,
  document.getElementById('root')
);
