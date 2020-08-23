import React from 'react';
import { Motion, spring, presets } from 'react-motion';
import { fetchApi } from '../utils/utils';
import LoadingSpinner from '../res/loading.gif';

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

class DogPicture extends React.Component {
  constructor(props) {
    super(props);
    this.MAX_IMAGE_HEIGHT_PX = 800;
    this.state = {
      currPic: null,
      currPicId: null,
      picHeightOverflow: false,
      picYOffset: 0, // Must always be nonnegative
      picXOffset: 0
    }
    this.setNewWidthOfPicture();
    window.addEventListener("resize", () => this.setNewWidthOfPicture());
  }

  setNewWidthOfPicture() {
    let windowWidth = window.innerWidth;
    if (windowWidth > 1000) {
      windowWidth = 1000;
    }
    this.wrapperWidth = windowWidth - 100; // TODO With the way the doc is set up, this is not entirely accurate
  }

  UNSAFE_componentWillReceiveProps(nextProps) {
    if (nextProps.picture && (!this.state.currPic || (nextProps.picture.id !== this.state.currPicId))) {
      const newPicture = nextProps.picture;
      const widthFactor = this.wrapperWidth / newPicture.normalizedWidth;
      const height = widthFactor * newPicture.normalizedHeight;
      const centerY = widthFactor * newPicture.normalizedCenterY;

      const heightFactor = this.MAX_IMAGE_HEIGHT_PX / newPicture.normalizedHeight;
      const width = heightFactor * newPicture.normalizedWidth;
      const centerX = heightFactor * newPicture.normalizedCenterX;

      const picHeightOverflow = height > this.MAX_IMAGE_HEIGHT_PX;
      let yOffset = 0;
      let xOffset = 0;
      if (picHeightOverflow && (centerY > (this.MAX_IMAGE_HEIGHT_PX / 2))) {
        if (centerY <= (height - (this.MAX_IMAGE_HEIGHT_PX / 2))) {
          yOffset = centerY - (this.MAX_IMAGE_HEIGHT_PX / 2);
        }
        else {
          yOffset = height - this.MAX_IMAGE_HEIGHT_PX;
        }
      }
      if (!picHeightOverflow && (centerX > (this.wrapperWidth / 2))) {
        if (centerX <= (width - (this.wrapperWidth / 2))) {
          xOffset = centerX - (this.wrapperWidth / 2);
        }
        else {
          xOffset = width - this.wrapperWidth;
        }
      }

      this.setState({
        currPic: null,
        currPicId: newPicture.id,
        picHeightOverflow: picHeightOverflow,
        picYOffset: yOffset,
        picXOffset: xOffset,
      });

      this.fetchDogPicture(newPicture.id);
    }
  }

  fetchDogPicture(id) {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dogpictures/${ id }`,
      resType: "blob",
      resCallback: (resBlob) => {
        const img = URL.createObjectURL(resBlob);
        this.setState({ currPic: img })
      }
    });
  }

  render() {
    return (
      <div style={{height: this.MAX_IMAGE_HEIGHT_PX}}>
        {this.state.currPic
          ? <div className="poll-img-wrapper-wrapper">
              <div className="poll-img-wrapper">
                <img className="poll-img" style={{
                  marginTop: -1 * this.state.picYOffset,
                  marginLeft: -1 * this.state.picXOffset,
                  width: this.state.picHeightOverflow ? "100%" : "",
                  height: this.state.picHeightOverflow ? "" : "100%"
                }} src={ this.state.currPic } alt="Freddy Pic" />
              </div>
            </div>
          : <img className="poll-img-loading" src={LoadingSpinner} alt="Loading..." />
        }
      </div>
    )
  }
}

export default class Poll extends React.Component {
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
      currId: null,
      currDogPicture: null,
      allNamesSeen: false,
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
    this.fetchRandomDogPicture();
  }

  addNameSeen(id) {
    let namesSeen = []
    if (this.state.namesSeen) {
      namesSeen = atob(this.state.namesSeen).split(",");
    }
    namesSeen.push(id);
    const namesSeenStr = btoa(namesSeen.toString());

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
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: "dognames/one",
      includeCreds: true,
      resCallback: {
        200: {
          resCallback: (resjson) => {
            this.setState({
              name: resjson.name,
              currId: resjson.id,
              loading: false
            });
            setTimeout(() => {
              clearInterval(this.rotationInterval);
              this.setState({rotation: 0});
            }, 100);
          }
        },
        204: {
          callback: (res) => this.setState({ allNamesSeen: true })
        }
      }
    });
  }

  fetchRandomDogPicture() {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dogpictures/info/random/${ this.state.currDogPicture ? this.state.currDogPicture.id + "/" : "" }`,
      resCallback: (resjson) => {
        this.setState({
          currDogPicture: resjson
        });
      }
    });
  }

  resetRotationInterval(startLeft) {
    this.setState({rotation: startLeft ? -20 : 20});
    this.rotationInterval = setInterval(() => this.setState({rotation: this.state.rotation === 20 ? -20 : 20}), 50);
  }

  voteOnName(voteIsYes) {
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dognames/vote/${ this.state.currId }/${voteIsYes}`,
      requestType: "POST",
      callback: (res) => {
        this.addNameSeen(this.state.currId);
        this.fetchName();
        this.fetchRandomDogPicture();
      }
    });
    this.resetRotationInterval(voteIsYes);
  }

  render() {
    return (
      <Motion
        defaultStyle={{rot: 0, opacity: 1}}
        style={{rot: spring(this.state.rotation, presets.wobbly), opacity: spring(this.state.rotation === 0 ? 1 : 0)}}
      >
        {style => (
          <div className="poll-container">
            {!this.state.allNamesSeen
              ? <div style={{opacity: style.opacity, marginBottom: "20px"}}>
                  <p className="poll-header">Does this name fit?</p>
                  <NameCard name={ this.state.name } rotation={ style.rot }/>
                  <DogPicture picture={ this.state.currDogPicture }/>
                  <PollOptions voteFunc={ (voteIsYes) => this.voteOnName(voteIsYes) } newPicFunc={ () => this.fetchRandomDogPicture() } rotation={ style.rot }/>
                </div>
              : <p className="poll-header">
                  All dog names seen!<br />
                  Maybe submit a few?
                </p>
            }
            
          </div>
        )}
      </Motion>
    );
  }
}