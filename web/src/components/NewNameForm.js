import React from 'react';
import { Motion, spring } from 'react-motion';
import Filter from 'bad-words';
import { fetchApi } from '../utils/utils';
import LoadingSpinner from '../res/loading.gif';

export default class NewNameForm extends React.Component {
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
    fetchApi({
      env: process.env.NODE_ENV,
      endpoint: `dognames`,
      requestType: "POST",
      body: JSON.stringify(name),
      includeCreds: true,
      resCallback: {
        200: {
          resCallback: (resJson) => {
            this.apiResult = {
              error: false,
              body: resJson
            };
            this.loading = false;
          },
          resErrorCallback: (error) => this.apiError(error)
        },
        0: {
          resType: "text",
          resCallback: (text) => this.apiError(text),
          resErrorCallback: (error) => this.apiError(error)
        }
      }
    });
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
  }

  handleChange(event) {
    this.setState({
      newName: event.target.value,
      apiResultText: ""
    });
    this.apiResult = null;
  }

  setError(errorText) {
    this.setState({
      apiResultText: errorText
    });
    this.apiResult = {
      error: true,
      body: ""
    };
  }

  handleSubmit(event) {
    let name = this.state.newName.trim();
    if (name !== "") {
      name = name.replace(/\w\S*/g, txt => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());
      const regexName = new RegExp("^[A-Za-z0-9 ]+$");
      if (!regexName.test(name)) {
        this.setError("Invalid name (name must only include letters, digits, and/or spaces)");
      }
      else if (name === "Freddy") {
        this.setError("No, you cannot suggest \"Freddy\". That's the whole point.");
      }
      else if (new Filter().isProfane(name)) {
        this.setError("Children use this website. Try again.");
      }
      else {
        this.sendName(name);
        setTimeout(() => this.checkLoading(), this.LOADING_WAIT_MILLI_SEC);
      }
    }
    event.preventDefault();
  }

  checkLoading() {
    if (!this.loading) {
      this.setState({
        newName: "",
        showInput: true,
        apiResultText: this.apiResult.error ? this.apiResult.body : `Successfully submitted name "${this.apiResult.body.name}"!`
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
          {!this.state.showInput && <img src={LoadingSpinner} className="new-name-form-loading-spinner" alt="Loading..." />}
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
                  {this.state.apiResultText}
                </p>
              )}
            </Motion>
          }
        </form>
      </div>
    );
  }
}