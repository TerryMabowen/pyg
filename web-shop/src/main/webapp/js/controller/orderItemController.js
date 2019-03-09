//控制层
app.controller('orderItemController', function ($scope, $controller, $location, orderItemService) {

    $controller('baseController', {$scope: $scope});//继承


    //查询实体
    $scope.selectOrderItemList = function () {
        var id = $location.search()['id'];
        // alert(id);
        orderItemService.selectOrderItemList(id).success(
                function (response) {
                    $scope.list = response;
                }
            )
    }
});	
