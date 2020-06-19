import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import theImage from './verizon.jfif';
import Router from './router';
import {BrowserRouter} from 'react-router-dom';
//import App from './App';


function Verizon()
{
  return (
    <div>
    
      <nav className="navbar abc1">
        <a href="#" className="navbarbrand abc2">
          <img src={theImage} width="140" height="55"></img>
          </a>
          <p className="abc3">beta v1.0</p>
          <div className="navbarnav abc4">
            <b>Social Media POC</b></div>
            </nav>
            <hr/> 
            <Router></Router>   
    </div>
  )
}
ReactDOM.render(<BrowserRouter><Verizon></Verizon></BrowserRouter>,document.getElementById("root"));


