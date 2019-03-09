//首页控制器
app.controller('seckillController', function ($scope, seckillService) {
    $scope.findSeckillOrderList = function () {
        seckillService.findSeckillOrderList().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    $scope.cancelOrder = function (id) {
        seckillService.cancelOrder(id).success(
            function (response) {
                alert(response.message);
                window.location.reload();
            }
        );
    }

});