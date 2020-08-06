import React from 'react';
import { Motion, spring, presets } from 'react-motion';
import CanvasJSReact from './assets/canvasjs.react';
import { withCookies, CookiesProvider } from 'react-cookie';
import './index.css';
import LoadingSpinner from './res/loading.gif';

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
      <p className="poll-option poll-option-new-pic" onClick={ () => props.newPicFunc() }>NEW PICTURE</p>
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
    this.cookies = this.props.cookies;
    const namesSeen = this.cookies.get("namesSeen");
    this.state = {
      name: "",
      currPicId: null,
      loading: true,
      rotation: 20,
      namesSeen: namesSeen
    };
    this.rotationInterval = setInterval(() => this.setState({rotation: -20}), 50);
  }

  /**
   * After component mounts, fetch the new name
   */
  componentDidMount() {
    this.fetchName();
    this.fetchDogPictureId();
  }

  addNameSeen(id) {
    let namesSeen = []
    if (this.state.namesSeen) {
      namesSeen = this.state.namesSeen.split(",");;
    }
    namesSeen.push(id);
    const namesSeenStr = namesSeen.toString();

    let expiration = new Date();
    const EXPIRATION_TIME_MINUTES = 60 * 24 * 14; // 2 weeks
    expiration.setTime(expiration.getTime() + (EXPIRATION_TIME_MINUTES * 60 * 1000));
    this.cookies.set("namesSeen", namesSeenStr, {
      path: "/",
      expires: expiration
    });
    this.setState({ namesSeen: namesSeenStr });
  }

  fetchName() {
    fetch(`http://localhost:8080/dognames/one`, {credentials: "include"})
      .then(
        (res) => {
          res.json().then(
            (resjson) => {
              this.addNameSeen(resjson.id);
              this.setState({
                name: resjson.name,
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

  fetchDogPictureId() {
    fetch(`http://localhost:8080/dogpictures/randomid/${ this.state.currPicId ? this.state.currPicId + "/" : "" }`)
      .then(
        (res) => {
          res.text().then(
            (resjson) => {
              this.setState({
                currPicId: JSON.parse(resjson)
              });
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
    fetch(`http://localhost:8080/dognames/vote/${this.state.nextNameIndex - 1}/${voteIsYes}`, {method: 'POST', credentials: "include"})
      .then(
        (res) => {
          this.fetchName();
          this.fetchDogPictureId();
        },
        (error) => this.apiError(error)
      );
    this.resetRotationInterval(voteIsYes);
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
              {this.state.currPicId ? <img className="poll-img" src={ `http://localhost:8080/dogpictures/${ this.state.currPicId }` } alt="Freddy Pic" /> : <img src={LoadingSpinner} />}
              <PollOptions voteFunc={ (voteIsYes) => this.voteOnName(voteIsYes) } newPicFunc={ () => this.fetchDogPictureId() } rotation={ style.rot }/>
            </div>
          </div>
        )}
      </Motion>
    );
  }
}

class NewNameForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      newName: "",
      showInput: true,
      apiResultText: ""
    };
    this.loading = false;
    this.apiResult = null;
    this.LOADING_WAIT_MILLI_SEC = 1000;
  }

  sendName(name) {
    this.setState({showInput: false});
    this.loading = true;
    fetch("http://localhost:8080/dognames", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(name),
      credentials: "include"
    })
      .then(
        (res) => {
          if (res.status === 200) {
            res.json().then(
              (resjson) => {
                this.apiResult = {
                  error: false,
                  body: resjson
                };
                this.loading = false;
              },
              (error) => this.apiError(error)
            );
          }
          else {
            res.text().then(
              (text) => this.apiError(text),
              (error) => this.apiError(error)
            );
          }
        },
        (error) => this.apiError(error)
      );
  }

  /**
   * Handles what should happen when there is an error from the API
   * @param {json} error Describes info from the error
   */
  apiError(error) {
    this.apiResult = {
      error: true,
      body: String(error)
    };
    this.loading = false;
    // console.log(error); // TODO Figure out how to show this
  }

  handleChange(event) {
    this.setState({
      newName: event.target.value,
      apiResultText: ""
    });
    this.apiResult = null;
  }

  handleSubmit(event) {
    let name = this.state.newName.trim();
    if (name !== "") {
      const regexName = new RegExp("^[A-Za-z0-9 ]+$");
      if (regexName.test(name)) {
        name = name.replace(/\w\S*/g, txt => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());
        this.sendName(name);
        setTimeout(() => this.checkLoading(), this.LOADING_WAIT_MILLI_SEC);
      }
      else {
        this.setState({
          apiResultText: `Invalid name (name must only include letters, digits, and/or spaces)`
        });
        this.apiResult = {
          error: true,
          body: ""
        };
      }
    }
    event.preventDefault();
  }

  checkLoading() {
    if (!this.loading) {
      this.setState({
        newName: "",
        showInput: true,
        apiResultText: this.apiResult.error ? this.apiResult.body : `Successfully submitted name "${this.apiResult.body.name}"`
      });
    }
    else {
      setTimeout(() => this.checkLoading(), this.LOADING_WAIT_MILLI_SEC);
    }
  }

  render() {
    return (
      <div className="new-name-form">
        <form onSubmit={ (event) => this.handleSubmit(event) }>
          <label htmlFor="newName"><h2 className="new-name-form-header">Suggest a name!</h2></label>
          {!this.state.showInput && <img src={LoadingSpinner} className="new-name-form-loading-spinner" />}
          <div className="new-name-form-input-container" style={{width: this.state.showInput ? "100%" : "0"}}>
            <input type="text" className="new-name-form-input" id="newName" value={ this.state.newName }
              placeholder="Enter a name..." onChange={ (event) => this.handleChange(event) } />
            <input type="submit" className="new-name-form-submit" value="Submit" />
          </div>
          {!(this.state.showInput && this.state.apiResultText !== "") ? "" :
            <Motion
              defaultStyle={{y: -40}}
              style={{y: spring(0)}}
            >
              {style => (
                <p style={{transform: `translateY(${style.y}px)`}} className={"new-name-form-api-res" + (this.apiResult.error ? " new-name-form-api-res-error" : "")}>
                  {this.state.apiResultText}!
                </p>
              )}
            </Motion>
          }
        </form>
      </div>
    );
  }
}

class ColumnChart extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currWinner: "",
      options: null,
      refreshHover: false
    }
  }

  componentDidMount() {
    this.fetchNamesData();
  }

  fetchNamesData() {
    this.setState({
      options: null,
      refreshHover: false
    });
    fetch(`http://localhost:8080/dognames/`)
      .then(
        (res) => {
          res.json().then(
            (resjson) => this.formatOptions(resjson),
            (error) => this.apiError(error)
          );
        },
        (error) => this.apiError(error)
      );
  }

  formatOptions(jsonData) {
    const NUM_DOG_NAMES_SHOWN = 10;
    let dogNameList = jsonData.slice(0, NUM_DOG_NAMES_SHOWN);
    dogNameList.sort((a, b) => b.yesVotes - a.yesVotes);
    let yesVotesDataPoints = dogNameList.map(dogName => {
      return {label: dogName.name, y: dogName.yesVotes }
    });
    let noVotesDataPoints = dogNameList.map(dogName => {
      return {label: dogName.name, y: dogName.noVotes }
    });

    const options = {
      animationEnabled: true,
      axisY: {
				title: "Number of Votes",
        labelFontSize: 20
			},
			axisX: {
        title: "Potential New Name",
        labelFontSize: 20,
				labelAngle: 0
			},
      data: [
        {
          // Change type to "doughnut", "line", "splineArea", etc.
          type: "column",
          showInLegend: true,
          name: "Yes Votes",
          color: "#00ff00",
          dataPoints: yesVotesDataPoints
        },
        {
          // Change type to "doughnut", "line", "splineArea", etc.
          type: "column",
          showInLegend: true,
          name: "No Votes",
          color: "#ff0000",
          dataPoints: noVotesDataPoints
        },
      ]
    };

    this.setState({
      currWinner: yesVotesDataPoints[0].label,
      options: options
    });
  }

  /**
   * Handles what should happen when there is an error from the API
   * @param {json} error Describes info from the error
   */
  apiError(error) {
    console.log(error); // TODO Figure out how to show this
  }

  refreshHover(hovering) {
    this.setState({
      refreshHover: hovering
    });
  }

  render() {
    return (
      <div className="results-chart-container">
        { this.state.options
          ? <div>
              <button className="results-chart-refresh-btn" onClick={ () => this.fetchNamesData() } onMouseEnter={ () => this.refreshHover(true) } onMouseLeave={ () => this.refreshHover(false) }>
                <i className={"fa fa-refresh" + (this.state.refreshHover ? " fa-spin" : "")}/>
              </button>
              <p className="results-chart-header">What Should I Call Freddy? Results</p>
              <p className="results-chart-subheader">Current Winner: <b>{ this.state.currWinner }</b></p>
              <CanvasJSReact.CanvasJSChart options={ this.state.options } />
            </div>
          : <img src={LoadingSpinner} />
         }
      </div>
    );
  }
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
      return <img src={LoadingSpinner} />;
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
          <ColumnChart />
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