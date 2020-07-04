import React from 'react';
import { Map, Tooltip, GeoJSON, TileLayer, DivOverlay } from "react-leaflet";
import { statesData } from "./data/data.js"
//import { tD } from "./data/index"
import { searchState, searchStateCode } from "./assets/fetchState"
// postCSS import of Leaflet's CSS
import 'leaflet/dist/leaflet.css';

import axios from 'axios';
import { Redirect } from 'react-router-dom';




export default class Mapping extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      tD: [],
      mapLoad: false
    }
  }

  style(feature) {
    let d = feature.properties.sentiment;
    let gc = () => {
      if(d){
        return d > 1.5 ? 'green' :
        d > 1 ? 'orange' : 
        d = 1 ? 'red' : 
        'grey'
      } else return 'grey'
      
    }
    return {
      fillColor: gc(),
      weight: 2,
      opacity: 1,
      color: 'white',
      dashArray: '3',
      fillOpacity: 0.7
    };
  }
  
  onEachFeature = (feature, layer) =>{
    // let goDetail = (feature) =>{
    //   window.location = "/aggrid?name=" + feature.properties.name;
    // }
    layer.on({ onmouseover: this.clicked }).bindPopup("State Name: " + feature.properties.name + "</br>" + "State Sentiment: " + feature.properties.sentiment.toFixed(2))
    layer.on({
      'dblclick': this.clicked
    });
    
  }

  clicked=(feature)=> {
    localStorage.setItem("stateCode",searchStateCode(feature.sourceTarget.feature.properties.name))
    //localStorage.setItem("stateCode",feature.sourceTarget.feature.properties.name)
    window.location = "/aggrid"
  }
  tooltipFn(e) {
    console.log(e);
  }



  componentWillMount() {
    var d = new Date(),
      month = '' + (d.getMonth() + 1),
      day = '' + (d.getDate() - 1),
      year = d.getFullYear();

    if (month.length < 2)
      month = '0' + month;
    if (day.length < 2)
      day = '0' + day;

    let today = [year, month, day].join('-');
    axios.get('http://localhost:8102/rest/users/fetch/date/2020-07-03').then(res => {
      this.setState({ tD: res.data });
      let sn = [];
      for (let i = 0; i < this.state.tD.length; i++) {
        if (this.state.tD[i].location) {
          if (searchState(this.state.tD[i].location)) {
            sn.push({statename: searchState(this.state.tD[i].location), sentimentScore: this.state.tD[i].sentimentScore});

          }
        }
      }
      for (let j = 0; j < statesData.features.length; j++) {
        let x = 0;
        statesData.features[j].properties["sentiment"] = [];
        for (let y = 0; y < sn.length; y++) {
          if (sn[y].statename == statesData.features[j].properties.name) {
            statesData.features[j].properties["count"] = x++;
            statesData.features[j].properties["sentiment"].push(sn[y].sentimentScore);
          }
        }
        var sum=0;
        if(statesData.features[j].properties["sentiment"]){
          for(var f=0;f<statesData.features[j].properties["sentiment"].length;f++){
            sum+=Number(statesData.features[j].properties["sentiment"][f]);
          }
          var avg = sum/statesData.features[j].properties["sentiment"].length;
          statesData.features[j].properties["sentiment"] = avg;
        }
      }
      this.setState({ mapLoad: true });
    });
  }

  render() {
    return this.state.mapLoad
      ? <div class="container p-3 my-3  text-white">
        <Map style={{ height: '400px', width: '100%' }} doubleClickZoom={false} center={[37.8, -96]} zoom={4} >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
          />
          <GeoJSON
            data={statesData}
            onEachFeature={this.onEachFeature}
            style={this.style}
          >
            {/* <Tooltip direction="auto" opacity={1}>{statesData}</Tooltip> */}
          </GeoJSON>

        </Map>

      </div>
      : null;
  }

}

