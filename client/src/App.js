import React, { Component } from 'react';
import { GoogleLogin } from 'react-google-login';
import InfiniteScroll from 'react-infinite-scroller';

import './App.css';

class App extends Component {

  constructor() {
    super();
    this.state = { isAuthenticated: false, user: null, token: '' };
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

    var url = api.baseUrl + '/users/8665091/favorites';
    if (this.state.nextHref) {
      url = this.state.nextHref;
    }

    qwest.get(url, {
      client_id: api.client_id,
      linked_partitioning: 1,
      page_size: 10
    }, {
      cache: true
    })
      .then(function (xhr, resp) {
        if (resp) {
          var tracks = self.state.tracks;
          resp.collection.map((track) => {
            if (track.artwork_url == null) {
              track.artwork_url = track.user.avatar_url;
            }

            tracks.push(track);
          });

          if (resp.next_href) {
            self.setState({
              tracks: tracks,
              nextHref: resp.next_href
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

    let content = <InfiniteScroll
      pageStart={0}
      loadMore={this.loadItems.bind(this)}
      hasMore={this.state.hasMoreItems}
      loader={loader}>

      <div className="tracks">
        {items}
      </div>
    </InfiniteScroll>

    return (
      <div className="App">
        {/* {sessionHandler} */}
        {content}
      </div>
    );
  }
}

export default App;
