import React, { Component } from 'react';
import { GoogleLogin } from 'react-google-login';
import InfiniteScroll from 'react-infinite-scroller';
import qwest from 'qwest';
import jwt_decode from 'jwt-decode';

import './App.css';

let api = "http://localhost:8080";
let namespace = 'default';

class App extends Component {

  constructor() {
    super();

    this.state = {
      isAuthenticated: localStorage.getItem('isAuthenticated') != null ? localStorage.getItem('isAuthenticated') : false,
      user: localStorage.getItem('user') != null ? localStorage.getItem('user') : null,
      token: localStorage.getItem('token') != null ? localStorage.getItem('token') : '',
      deployments: [],
      hasMoreItems: true,
      nextHref: null,
      deplyName: '',
      deplyImage: '',
      deplyReplicas: 0,
      deplyPorts: ''
    };
  }

  logout = () => {
    this.setState({ isAuthenticated: false, token: '', user: null })
    localStorage.setItem("token", null)
    localStorage.setItem("isAuthenticated", false)
    localStorage.setItem("user", null)
  };

  changeName = (e) => {
    this.setState({ deplyName: e.target.value })
  }

  changeImage = (e) => {
    this.setState({ deplyImage: e.target.value })
  }

  changeReplicas = (e) => {
    this.setState({ deplyReplicas: e.target.value })
  }

  changePorts = (e) => {
    this.setState({ deplyPorts: e.target.value })
  }

  handleCreateDeploy = () => {
    var url = api + '/v1/deployments';

    var data = {
      namespace: "default",
      name: this.state.deplyName,
      replicas: this.state.deplyReplicas,
      containers: [
        {
          name: this.state.deplyName,
          image: this.state.deplyImage,
          ports: [
            {
              containerPort: this.state.deplyPorts
            }
          ]
        }	
      ]
    };

    qwest.post(url,data,{
      dataType: 'json',
      headers: {
        'Authorization': 'Bearer ' + this.state.token,
        'Content-Type': 'application/json'
      } 
    })
  }

  googleResponse = (e) => {
    this.setState({ isAuthenticated: true, token: e['tokenId'], user: jwt_decode(e['tokenId']).name })
    localStorage.setItem("token", e['tokenId'])
    localStorage.setItem("isAuthenticated", true)
    localStorage.setItem("user", jwt_decode(e['tokenId']).name)
  };

  onFailure = (error) => {
    alert(error);
  }

  loadItems(page) {
    var self = this;

    var url = api + '/v1/deployments?namespace=' + namespace;
    if (this.state.nextHref) {
      url = this.state.nextHref;
    }

    qwest.get(url, {
      linked_partitioning: 1,
      page_size: 3
    }, {
      cache: true,
      headers: {
        'Authorization': 'Bearer ' + this.state.token
      }
    })
      .then(function (xhr, resp) {
        resp = JSON.parse(resp)

        if (resp) {
          var deployments = self.state.deployments;
          resp["_embedded"]['deploymentList'].map((d) => {
            deployments.push(d);
          });

          if (resp["_links"]["next"]) {
            self.setState({
              deployments: deployments,
              nextHref: resp["_links"]["next"]["href"]
            });
          } else {
            self.setState({
              hasMoreItems: false
            });
          }
        }
      });
  }

  render() {
    const loader = <div className="loader">Loading ...</div>;

    var items = [];
    this.state.deployments.map((d, i) => {
      items.push(
        <tr>
          <td>{i}</td>
          <td>{d.name}</td>
          <td>{d.spec.podTemplateSpec.podSpec.containers
            .map((c) => { return c.image })
            .reduce((img, cat) => { return cat + '\n' + img }, '')}
          </td>
          <td>
            Replicas: {d.status.replicas}<br />
            Av: {d.status.availableReplicas}<br />
            Rdy: {d.status.readyReplicas}<br />
            UnAv: {d.status.unavailableReplicas}<br />
            Up: {d.status.updatedReplicas}<br />
          </td>
        </tr>
      );
    });

    let deploymentsTable = <InfiniteScroll
      pageStart={0}
      loadMore={this.loadItems.bind(this)}
      hasMore={this.state.hasMoreItems}
      loader={loader}>

      <table class="deploymentsTable">
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Image name</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {items}
        </tbody>
      </table>
    </InfiniteScroll>

    let deploymentCreatorForm = <div class="deploymentCreator">
      <form>
        <label>
          Name: <input type="text" value={this.state.deplyName} onChange={this.changeName} />
        </label>

        <label>
          Replicas: <input type="text" value={this.state.deplyReplicas} onChange={this.changeReplicas} />
        </label>

        <label>
          Container Image: <input type="text" value={this.state.deplyImage} onChange={this.changeImage} />
        </label>

        <label>
          Container Ports: <input type="text" value={this.state.deplyPorts} onChange={this.changePorts} />
        </label>

        <input class="button" type="button" value="Create" onClick={this.handleCreateDeploy} />
      </form>
    </div>

    let content = !!this.state.isAuthenticated ?
      (
        <span>
          <div class="userPanel">
            <span>Hello, {this.state.user}!</span>
            <button onClick={this.logout} className="button">Log out</button>
          </div>
          {deploymentCreatorForm}
          {deploymentsTable}
        </span>
      ) :
      (
        <div>
          <h3>Please Log In</h3>
          <GoogleLogin
            clientId="462925264156-ltj51nhq4l155f87utmtcmkbidb14l9r.apps.googleusercontent.com"
            buttonText="Login"
            onSuccess={this.googleResponse}
            onFailure={this.googleResponse}
          />
        </div>
      );

    return (
      <div className="App">
        <h1>Kubernetes Orchestrator</h1>
        {content}
      </div>
    );
  }
}

export default App;
