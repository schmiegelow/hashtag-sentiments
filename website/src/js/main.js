/**
 * @license
 * Copyright (c) 2014, 2018, Oracle and/or its affiliates.
 * The Universal Permissive License (UPL), Version 1.0
 */
'use strict';

/**
 * Example of Require.js boostrap javascript
 */

requirejs.config(
{
  baseUrl: 'js',

  // Path mappings for the logical module names
  // Update the main-release-paths.json for release mode when updating the mappings
  paths:
  //injector:mainReleasePaths
  {
    'knockout': 'libs/knockout/knockout-3.4.2.debug',
    'jquery': 'libs/jquery/jquery-3.3.1',
    'jqueryui-amd': 'libs/jquery/jqueryui-amd-1.12.1',
    'promise': 'libs/es6-promise/es6-promise',
    'hammerjs': 'libs/hammer/hammer-2.0.8',
    'ojdnd': 'libs/dnd-polyfill/dnd-polyfill-1.0.0',
    'ojs': 'libs/oj/v5.0.0/debug',
    'ojL10n': 'libs/oj/v5.0.0/ojL10n',
    'ojtranslations': 'libs/oj/v5.0.0/resources',
    'text': 'libs/require/text',
    'signals': 'libs/js-signals/signals',
    'customElements': 'libs/webcomponents/custom-elements.min',
    'proj4': 'libs/proj4js/dist/proj4-src',
    'css': 'libs/require-css/css',
  }
  //endinjector
  ,
  // Shim configurations for modules that do not expose AMD
  shim:
  {
    'jquery':
    {
      exports: ['jQuery', '$']
    }
  }
}
);

/**
 * A top-level require call executed by the Application.
 * Although 'ojcore' and 'knockout' would be loaded in any case (they are specified as dependencies
 * by the modules themselves), we are listing them explicitly to get the references to the 'oj' and 'ko'
 * objects in the callback
 */
require(['ojs/ojcore', 'knockout', 'appController', 'ojs/ojknockout', 'ojs/ojbutton', 'ojs/ojtoolbar', 'ojs/ojmenu'],
  function (oj, ko, app) { // this callback gets executed when all required modules are loaded

    $(function() {

      function init() {
        // Bind your ViewModel for the content of the whole page body.
        ko.applyBindings(app, document.getElementById('globalBody'));
      }

      // If running in a hybrid (e.g. Cordova) environment, we need to wait for the deviceready
      // event before executing any code that might interact with Cordova APIs or plugins.
      if ($(document.body).hasClass('oj-hybrid')) {
        document.addEventListener("deviceready", init);
      } else {
        init();
      }

    });

  }
);

require(['ojs/ojcore', 'knockout', 'jquery', 'ojs/ojknockout', 'promise', 'ojs/ojtable', 'ojs/ojarraydataprovider'],
function(oj, ko, $)
{
  function viewModel()
  {
    var self = this;

    var esArray =
         [{"score":-0.10000000149011612,"magnitude":0.4000000059604645,"tweet":"When you fill out an “I’m not a robot test”, a robot is asking you if you are his own kind. #ThursdayThoughts","user":"Wmm3 Productions","tweetId":1004768426580209666,"timestamp":1528390435000,"hashtag":"#ThursdayThoughts"},
         {"score":0.30000001192092896,"magnitude":1.100000023841858,"tweet":"Today's Thought ~ Tension inspires creativity. If there's not enough tension in the meeting, don't be afraid to int… https://t.co/EfappLYMNH","user":"Yochanan (John)","tweetId":1004768339799859200,"timestamp":1528390414000,"hashtag":"#ThursdayThoughts"},
         {"score":-0.4000000059604645,"magnitude":0.4000000059604645,"tweet":"RT @bill_auclair: Still trying to wrap my head around this bizarre new reality where Canada is now an \"enemy of the people\" while Russia is…","user":"kiltluvr","tweetId":1004768455654924288,"timestamp":1528390442000,"hashtag":"#ThursdayThoughts"},
         {"score":0.30000001192092896,"magnitude":3.200000047683716,"tweet":"RT @MyPowertalk: Instant stress relief:\n\n1.\nFocus your awareness\nfully in the NOW.\n\n2.\nRefuse to worry.\nBreathe in...\n\n3.\nImagine the best…","user":"Berni ॐ","tweetId":1004768351963500545,"timestamp":1528390417000,"hashtag":"#ThursdayThoughts"},
         {"score":0,"magnitude":0,"tweet":"RT @SilverAdie: Time Cover #Time #ThursdayThoughts #TheResistance #TrumpCrimeSyndicate #TimeMagazine #FBRParty #MuellerTime\n\nTrump is not a…","user":"Debra Duke","tweetId":1004768442887692288,"timestamp":1528390439000,"hashtag":"#ThursdayThoughts"},
         {"score":0.8999999761581421,"magnitude":0.8999999761581421,"tweet":"Adding a bit of colour to today’s greyness #Colourful #Flower #ThursdayThoughts #HereToCreate #CanonPhotography https://t.co/Z9SrFBc9nh","user":"Megan Calton","tweetId":1004768424776601600,"timestamp":1528390434000,"hashtag":"#ThursdayThoughts"},
         {"score":0,"magnitude":0.20000000298023224,"tweet":"RT @OnevoixPh: Because life is too short to be working all the time.\n\n#ThursdayThoughts #quotes #quoteoftheday #quotestoliveby \n\nvia @cktec…","user":"Royal LePage CK","tweetId":1004768452693975041,"timestamp":1528390441000,"hashtag":"#ThursdayThoughts"},
         {"score":-0.10000000149011612,"magnitude":0.800000011920929,"tweet":"RT @ Satishd51670551: #ThursdayThoughts bharam Chamhuhu no me, know Satyam devi! Taran Taran Abhay Pacha Daata, I am Kabir Avinashi !! #full_baby_baby ...","user":"Mahendra das","tweetId":1004768545643773952,"timestamp":1528390463000,"hashtag":"#ThursdayThoughts"},
         {"score":0.800000011920929,"magnitude":0.800000011920929,"tweet":"#ThursdayThoughts from High Performance Tennis Director Rob Budacsek: Mental power is essential, and sometimes even… https://t.co/PxydLinhY6","user":"College Park Athletic Club","tweetId":1004768541298581504,"timestamp":1528390462000,"hashtag":"#ThursdayThoughts"},
         {"score":0,"magnitude":0,"tweet":"RT @sweetatertot2: @RealCandaceO @realDonaldTrump @BillClinton Media covers up the fact that it was Bill Clinton's Crime Bill 94' that was…","user":"Dan","tweetId":1004768432884277248,"timestamp":1528390436000,"hashtag":"#ThursdayThoughts"}
         ];
    self.dataprovider = new oj.ArrayDataProvider(esArray, {idAttribute: 'timestamp', implicitSort: [{attribute: 'score', direction: 'ascending'}]});
  }
  var vm = new viewModel;

  $(document).ready
  (
    function()
    {
      ko.applyBindings(vm, document.getElementById('table'));
    }
  );
});
