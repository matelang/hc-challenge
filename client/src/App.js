import React, { Component } from 'react';
import { GoogleLogin } from 'react-google-login';
import InfiniteScroll from 'react-infinite-scroller';
import qwest from 'qwest';
import jwt_decode from 'jwt-decode';

import './App.css';

class App extends Component {

  constructor() {
    super();

    this.state = {
      isAuthenticated: false,
      user: null,
      token: '',
      deployments: [],
      hasMoreItems: true,
      nextHref: null
    };
  }

  logout = () => {
    this.setState({ isAuthenticated: false, token: '', user: null })
  };

  googleResponse = (e) => {
    this.setState({ isAuthenticated: true, token: e['tokenId'], user: jwt_decode(e['tokenId']).name })
  };

  onFailure = (error) => {
    alert(error);
  }

  loadItems(page) {
    var self = this;

    let api = "http://localhost:8080";
    var url = api + '/v1/deployments?namespace=default';
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
          <td>{d.spec.podTemplateSpec.podSpec.containers[0].image}</td>
        </tr>
      );
    });

    let deploymentsTable = <InfiniteScroll
      pageStart={0}
      loadMore={this.loadItems.bind(this)}
      hasMore={this.state.hasMoreItems}
      loader={loader}>

      <table border="2">
        <tbody>
          {items}
        </tbody>
      </table>
    </InfiniteScroll>

    let content = !!this.state.isAuthenticated ?
      (
        <div>
          <p>Authenticated</p>
          <div>
            {this.state.user}
          </div>
          <div>
            <button onClick={this.logout} className="button">Log out</button>
          </div>
          {deploymentsTable}
        </div>
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
