app.controller('seckillOrderController',function ($scope,seckillOrderService,$controller) {
    //继承baseController
    $controller('baseController',{$scope:$scope});
    //查询所有并展示
    $scope.findAll=function() {
        seckillOrderService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }

    //分页查询
    $scope.findPage=function(page,rows){
        seckillOrderService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }
    //条件查询带分页
    //定义一个查询条件对象
    $scope.searchEntity={};
    //条件查询
    $scope.search=function (page,rows) {
        seckillOrderService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }
    // 显示状态
    $scope.status = ["未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
})