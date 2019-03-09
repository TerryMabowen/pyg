app.controller('orderListController',function ($scope,orderListService,$controller) {
    //继承baseController
    $controller('baseController',{$scope:$scope});
    //查询所有并展示
   $scope.findAll=function() {
        orderListService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }
    //分页查询
   $scope.findPage=function(page,rows){
        orderListService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }
    // //搜索,跳转到portal系统查询列表页面(传递参数）
    // $scope.search=function(){
    //     location.href="http://localhost:8083/orderSearch.html#?sellerId="+$scope.sellerId;
    // }
    //分页查询
    $scope.getOrderList=function(sellerId){
        orderListService.getOrderList(sellerId).success(
                location.href = "http://localhost:8083/admin/orderlist2.html#?sellerId=" + $scope.sellerId
        )
    }
})