import React, { Component } from 'react';
import { GoogleLogin } from 'react-google-login';
import InfiniteScroll from 'react-infinite-scroller';
import qwest from 'qwest';

import './App.css';

// https://medium.com/@alexanderleon/implement-social-authentication-with-react-restful-api-9b44f4714fa

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
    console.log("Hello " + JSON.stringify(e))
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
      cache: true
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
    let sessionHandler = !!this.state.isAuthenticated ?
      (
        <div>
          <p>Authenticated</p>
          <div>
            {this.state.user.email}
          </div>
          <div>
            <button onClick={this.logout} className="button">Log out</button>
          </div>
        </div>
      ) :
      (
        <div>
          <GoogleLogin
            clientId="462925264156-ltj51nhq4l155f87utmtcmkbidb14l9r.apps.googleusercontent.com"
            buttonText="Login"
            onSuccess={this.googleResponse}
            onFailure={this.googleResponse}
          />
        </div>
      );

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

    let content = <InfiniteScroll
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

    return (
      <div className="App">
        <h1>Kubernetes Orchestrator</h1>
        {sessionHandler}
        <div style={{ margin: '0 auto' }}>
          {content}
        </div>
      </div>
    );
  }
}

export default App;
