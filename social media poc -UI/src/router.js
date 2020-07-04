import React from 'react';
import {Link,Route,Switch} from 'react-router-dom';
 
import Aggrid from './aggrid';
import Mapping from './main';


  
function Router()
{
  return (
            <div>
            <nav className="navbar navbar-expand-sm bg-primary navbar-dark d-flex abc5">
            <ul className="navbar-nav">
            <li className="nav-item active"><Link  class="nav-link" to="/"  >Home</Link></li>
            <li className="nav-item active"><Link  class="nav-link" to="/aggrid" >Details</Link></li>
            
           
            </ul>
            </nav>
            <Switch>
            <Route exact path="/" component={Mapping}></Route>
            <Route exact path="/aggrid" component={Aggrid}></Route>
           
            
            </Switch>
            </div>
    
  )
}
  export default Router