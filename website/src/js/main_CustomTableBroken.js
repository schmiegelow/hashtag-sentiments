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

// JET Custom table

require(['ojs/ojcore', 'knockout', 'jquery', 'ojs/ojknockout', 'promise', 'ojs/ojtable', 'ojs/ojgauge', 'ojs/ojarraydataprovider', 'ojs/ojchart', 'ojs/ojinputtext'],
function(oj, ko, $)
{
  function viewModel()
  {
    var self = this;
    var deptArray = [{EmployeeId: 5, Photo: 'images/dvt/1.png',FirstName: 'Amy', LastName: 'Bartlet', Revenue: 100000, Rating: 1, TargetComplete: 20, TargetIncomplete: 80},
        {EmployeeId: 10, Photo: 'images/dvt/10.png', FirstName: 'Andy', LastName: 'Jones', Revenue: 200000, Rating: 2, TargetComplete: 40, TargetIncomplete: 60},
        {EmployeeId: 20, Photo: 'images/dvt/11.png', FirstName: 'Andrew', LastName: 'Bugsy', Revenue: 130000, Rating: 3, TargetComplete: 10, TargetIncomplete: 90},
        {EmployeeId: 30, Photo: 'images/dvt/2.png', FirstName: 'Annette', LastName: 'Barnes', Revenue: 110000, Rating: 4, TargetComplete: 30, TargetIncomplete: 70},
        {EmployeeId: 40, Photo: 'images/dvt/12.png', FirstName: 'Bob', LastName: 'Jones', Revenue: 230000, Rating: 5, TargetComplete: 70, TargetIncomplete: 30},
        {EmployeeId: 50, Photo: 'images/dvt/13.png', FirstName: 'Bart', LastName: 'Buckler', Revenue: 400000, Rating: 1.5, TargetComplete: 90, TargetIncomplete: 10},
        {EmployeeId: 60, Photo: 'images/dvt/14.png', FirstName: 'Bobby', LastName: 'Fisher', Revenue: 600000, Rating: 1.5, TargetComplete: 40, TargetIncomplete: 60}];
    self.dataprovider = new oj.ArrayDataProvider(deptArray, {idAttribute: 'EmployeeId'});

    self.revenue_total_func = function(context)
    {
      var dataprovider = context.datasource;

      if (!dataprovider)
      {
          return;
      }

      var total = 0;
      dataprovider.getTotalSize().then(function(totalRowCount) {
        var addRevenue = function(rowNum)
        {
          dataprovider.fetchByOffset({offset:rowNum}).then(function(value)
          {
            var row = value['results'][0];
            total = total + row['data']['Revenue'];
            if (rowNum < totalRowCount - 1)
            {
              addRevenue(rowNum + 1);
            }
            else
            {
              var parentElement = $(document.getElementById("table:revenue_total"));
              parentElement.attr('value', total);
            }
          });
        };
        addRevenue(0);
      });
      return total;
    };

    self.columnArray = [{"renderer": oj.KnockoutTemplateUtils.getRenderer("emp_photo", true),
                     "footerRenderer": oj.KnockoutTemplateUtils.getRenderer("revenue_total_label", true),
                     "sortable": "disabled"},
                    {"headerText": "Employee Name",
                     "sortable": "enabled",
                     "renderer": oj.KnockoutTemplateUtils.getRenderer("emp_name", true),
                     "sortProperty": "FirstName"},
                    {"headerText": "Sales Revenue",
                     "renderer": oj.KnockoutTemplateUtils.getRenderer("revenue", true),
                     "footerRenderer": oj.KnockoutTemplateUtils.getRenderer("revenue_total", true),
                     "sortProperty": "Revenue"},
                    {"headerText": "Rating",
                     "field": "Rating",
                     "renderer": oj.KnockoutTemplateUtils.getRenderer("rating", true)},
                    {"headerText": "Sales Target Achievement",
                     "field": "TargetAchievement",
                     "sortable": "disabled",
                     "renderer": oj.KnockoutTemplateUtils.getRenderer("target", true)}];
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
