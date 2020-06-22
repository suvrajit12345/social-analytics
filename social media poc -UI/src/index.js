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
    <div class="container-fluid ">
      <nav className="navbar d-flex justify-content-start" >
        <a href="#" className="navbarbrand " style={{width:"90px",height:"40px"}}>
          <img src={theImage} class="img-fluid"></img>
          </a>
          
          <div className="navbarnav ml-auto mr-auto abc4">
            <b>Social Media POC</b></div>
            </nav>
            </div>
            <hr/> 
            <Router></Router> 
              
    </div>
  )
}
ReactDOM.render(<BrowserRouter><Verizon></Verizon></BrowserRouter>,document.getElementById("root"));


