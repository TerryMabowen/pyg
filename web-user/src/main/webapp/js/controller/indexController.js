//首页控制器
app.controller('indexController',function($scope,loginService,orderService){
	$scope.showName=function(){
			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;
					}
			);
	}

    // 显示状态
    $scope.status = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];

	$scope.findOrdersByUsername=function () {
        orderService.findOrdersByUsername(status).success(
            function (response) {
                $scope.orders=response.orders;
                $scope.orderItems=response.orderItems;
                $scope.sellerName=response.sellerName;
            }
        )
    }

});