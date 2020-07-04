
import React, { Component } from 'react';

import { AgGridReact } from 'ag-grid-react';

import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine.css';
//import Data from './aggrid.json'; 





class Aggrid extends Component {


    constructor(props) {
        super(props);

        this.state = {
            columnDefs: [
                { headerName: "Date", field: "date" },
                {
                    headerName: "SentimentScore",
                    field: "sentimentScore",
                    cellStyle: function(params) {
                        if (params.value==1) {
                            return {color: 'black', backgroundColor: 'pink'};
                        } else if (params.value==2) {
                            return {color: 'black', backgroundColor: '#fed8b1'};
                        } else if (params.value==3) {
                            return {color: 'black', backgroundColor: 'lightgreen'};
                        } else {
                            return {color: 'black', backgroundColor: 'white'};
                        }
                    },
                    cellRenderer: function(params){
                        if (params.value==1) {
                            return "Negative Review";
                        } else if (params.value==2) {
                            return "Neutral Review";
                        } else if (params.value==3) {
                            return "Positive Review";
                        } else {
                            return {color: 'black', backgroundColor: 'white'};
                        }
                    }
                },
                { headerName: "Name", field: "name" },
                { headerName: "Location", field: "location" },
                { headerName: "Text", field: "text" },
                { headerName: "SNumber", field: "SNumber" },
                { headerName: "Hashtag", field: "hashtag" },
            ],
            rowData: null
        }
    }
    componentWillMount() {
        let stateSelected = localStorage.getItem("stateCode")
        fetch('http://localhost:8102/rest/users/fetch/location/USA').then(res => res.json()).then(rowData => this.setState({ rowData }))
                .catch(err => console.log(err));
        // if (stateSelected) {
        //     fetch('http://localhost:8102/rest/users/fetch/location/' + stateSelected).then(res => res.json()).then(rowData => this.setState({ rowData }))
        //         .catch(err => console.log(err));
        // } else {
        //     localStorage.setItem("stateCode",'USA')
        //     stateSelected = localStorage.getItem("stateCode")
        //     fetch('http://localhost:8102/rest/users/fetch/location/USA').then(res => res.json()).then(rowData => this.setState({ rowData }))
        //         .catch(err => console.log(err));
        // }

    }

    render() {

        return (



            <div className="ag-theme-alpine" style={{ height: '400px', width: '1200px' }}>

                <AgGridReact
                    columnDefs={this.state.columnDefs}
                    rowData={this.state.rowData}>

                </AgGridReact>

            </div>
        );
    }
}
export default Aggrid