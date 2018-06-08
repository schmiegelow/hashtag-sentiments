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