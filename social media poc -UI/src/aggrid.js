
import React, { Component } from 'react';
import { render } from 'react-dom';
import { AgGridReact } from '@ag-grid-community/react';
import { AllModules } from '@ag-grid-enterprise/all-modules';
import '@ag-grid-community/all-modules/dist/styles/ag-grid.css';
import '@ag-grid-community/all-modules/dist/styles/ag-theme-alpine.css';
//import Data from './aggrid.json'; 
//import { DateTimeRenderer } from './cellRenderers/DateTimeRenderer';
//import { formatDate } from '@ag-grid/common';



class Aggrid extends Component {


    constructor(props) {
        super(props);

        this.state = {
            modules: AllModules,
            columnDefs: [
                {
                    headerName: "Date", field: "date",
                    cellRenderer: (data) => {
                        return data.value ? (new Date(data.value)).toLocaleDateString() : '';
                    }
                },

                
                { headerName: " User Name", field: "name" },
                { headerName: "Location", field: "location" },
                {
                    headerName: "Tweet",
                    field: "text",
                    tooltipField: 'text',
                    width: 350,
                    cellStyle: { 'white-space': 'normal', 'min-height': '40' },
                    autoHeight: true
                },
                
                {
                    headerName: "User Sentiment",
                    field: "userSentiment",
                    cellStyle: function (params) {
                        if (params.value == "Negative Review") {
                            return { color: 'black', backgroundColor: 'pink' };
                        } else if (params.value == "Neutral Review") {
                            return { color: 'black', backgroundColor: '#fed8b1' };
                        } else if (params.value == "Positive Review") {
                            return { color: 'black', backgroundColor: 'lightgreen' };
                        } else {
                            return { color: 'black', backgroundColor: 'white' };
                        }
                    },
                },
                {
                    headerName: "Sentiment Score",
                    field: "sentimentScore",
                },
            ]
            ,
            rowData: null,
            defaultColDef: {
                sortable: true,
                resizable: true,
                filter: true,
            },
            getRowHeight: function (params) {
                return params.data.rowHeight;
            },
        }
    }
    componentWillMount() {
        let stateSelected = localStorage.getItem("stateCode")
        // fetch('http://localhost:8102/rest/users/fetch/location/USA').then(res => res.json()).then(rowData => this.setState({ rowData }))
        //         .catch(err => console.log(err));
        if (stateSelected) {
            fetch('http://localhost:8102/rest/users/fetch/location/' + stateSelected).then(res => res.json()).then(rowData => {
                for(let x=0;x<rowData.length;x++){
                    if(rowData[x].sentimentScore == 1)rowData[x]["userSentiment"] = "Negative Review"
                    if(rowData[x].sentimentScore == 2)rowData[x]["userSentiment"] = "Neutral Review"
                    if(rowData[x].sentimentScore == 3)rowData[x]["userSentiment"] = "Positive Review"
                }
                this.setState({ rowData })
            }).catch(err => console.log(err));
        } else {
            localStorage.setItem("stateCode", 'USA')
            stateSelected = localStorage.getItem("stateCode")
            fetch('http://localhost:8102/rest/users/fetch/location/USA').then(res => res.json()).then(rowData => {
                for(let x=0;x<rowData.length;x++){
                    if(rowData[x].sentimentScore == 1)rowData[x]["userSentiment"] = "Negative Review"
                    if(rowData[x].sentimentScore == 2)rowData[x]["userSentiment"] = "Neutral Review"
                    if(rowData[x].sentimentScore == 3)rowData[x]["userSentiment"] = "Positive Review"
                }
                this.setState({ rowData })
            }).catch(err => console.log(err));
        }
    }

    onGridReady = params => {
        this.gridApi = params.api;
        this.gridColumnApi = params.columnApi;
    };

    onBtExport = () => {
        this.gridApi.exportDataAsExcel({});
    };

    render() {
        return (

            <div style={{ width: '100%', height: '100%' }}>
                <div className="example-wrapper m-2">
                    <div className="example-header">
                        <label>
                            <button onClick={() => this.onBtExport()}>Export to Excel</button>
                        </label>
                    </div>

                    <div
                        id="myGrid"
                        style={{
                            height: '100%',
                            width: '100%',
                        }}
                        className="ag-theme-alpine" style={{ height: '400px', width: '100%' }}
                    >
                        <AgGridReact
                            modules={this.state.modules}
                            columnDefs={this.state.columnDefs}
                            defaultColDef={this.state.defaultColDef}
                            getRowHeight={this.state.getRowHeight}
                            rowData={this.state.rowData}
                            onGridReady={this.onGridReady}
                        />
                    </div>
                </div>
            </div>
        );
    }
}



render(<Aggrid></Aggrid>, document.querySelector('#root'));

export default Aggrid