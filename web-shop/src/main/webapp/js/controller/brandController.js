app.controller('brandController',function ($scope,brandService,$controller) {
    //继承baseController
    $controller('baseController',{$scope:$scope});


    //申请  修改状态
    $scope.update=function (id) {
        brandService.update(id).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }else {
                    alert(response.message);
                }
            });

    }



    //条件查询带分页
    //定义一个查询条件对象
    $scope.searchEntity={};
    //条件查询
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

    // 显示状态
    $scope.stat1 = ["未申请", "已申请",  "审核通过", "驳回"];

})