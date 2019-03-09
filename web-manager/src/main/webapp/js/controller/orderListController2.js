app.controller('orderListController2',function ($scope,orderListService2,$controller,$location) {
    //继承baseController
    $controller('baseController',{$scope:$scope});
    $scope.sellerId={};
   //  //查询所有并展示
   // $scope.findAll=function() {
   //      orderListService2.findAll().success(
   //          function (response) {
   //              $scope.list = response;
   //          }
   //      )
   //  }
    //分页查询
   $scope.findPage=function(page,rows){
        orderListService2.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }
    //加载关键字
    $scope.findOrderItem=function(){
        //$scope.sellerId= $location.search()['sellerId'];
        orderListService2.findOrderItem(sellerId).success(
            function (response) {
                    $scope.list= response;
            }
        )
}
})