import React from 'react';
import CanvasJSReact from '../assets/canvasjs.react';
import LoadingSpinner from '../res/loading.gif';

export default class NameResultsChart extends React.Component {
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
          : <img src={LoadingSpinner} alt="Loading..." />
         }
      </div>
    );
  }
}