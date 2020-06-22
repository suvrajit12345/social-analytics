import React from 'react';
import { Map, Tooltip, GeoJSON, TileLayer, DivOverlay } from "react-leaflet";
import { statesData } from "./data/data.js"
import { tD } from "./data/index"
import { searchState } from "./assets/fetchState"
// postCSS import of Leaflet's CSS
import 'leaflet/dist/leaflet.css';

function getColor(d) {
  return d > 15 ? 'red' :
    d > 5 ? 'orange' : 'green'
  // return d > 1000 ? '#800026' :
  //   d > 500 ? '#BD0026' :
  //     d > 200 ? '#E31A1C' :
  //       d > 100 ? '#FC4E2A' :
  //         d > 50 ? '#FD8D3C' :
  //           d > 20 ? '#FEB24C' :
  //             d > 10 ? '#FED976' :
  //               '#FFEDA0';
}
function style(feature) {
  return {
    fillColor: getColor(feature.properties.count),
    weight: 2,
    opacity: 1,
    color: 'white',
    dashArray: '3',
    fillOpacity: 0.7
  };
}

function onEachFeature(feature, layer) {
  layer.on({
    onMouseOver: clickToFeature.bind(this)
  }).bindPopup("State Name: "+feature.properties.name+"</br>"+"Number Of Post: "+feature.properties.count);
  layer.on({
    dblclick: clickToFeatureDbl.bind(this)
  })
}
function clickToFeature(e) {
  var layer = e.target;
  console.log("I doubleclicked on " + layer.feature.properties.name + ' Id '+ layer.feature.id);

}
function clickToFeatureDbl(f){
  console.log('hii');
  var layer = f.target;
  window.location = "/aggrid?name="+layer.feature.properties.name;
  
}
function tooltipFn(e){
  console.log(e);
}
function leafLetMap() {
  let sn = [];
  for (let i = 0; i < tD.hits.hits.length; i++) {
    if (tD.hits.hits[i]._source.location) {
      if (searchState(tD.hits.hits[i]._source.location)) {
        sn.push(searchState(tD.hits.hits[i]._source.location));
      }
    }
  }
  for (let j = 0; j < statesData.features.length; j++) {
    let x = 0;
    for (let y = 0; y < sn.length; y++) {
      if (sn[y] == statesData.features[j].properties.name) {
        statesData.features[j].properties["count"] = x++;
      }
    }
  }
  return (
    <div class="container p-3 my-3  text-white">
      <Map style={{height: '400px',width:'100%'}} center={[37.8, -96]} zoom={4}>
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        />
        <GeoJSON
          data={statesData}
          onEachFeature={onEachFeature.bind(this)}
          style={style.bind(this)}
        >
        {/* <Tooltip direction="auto" opacity={1}>{statesData}</Tooltip> */}
        </GeoJSON>
      </Map>
    </div>
  )
}

export default class Mapping extends React.Component {
    render(){
        return (
            leafLetMap()
          );
    }
 
}

 