//控制层
app.controller('ordersController', function ($scope, $controller, $location, typeTemplateService, itemCatService, uploadService, ordersService) {

    $controller('baseController', {$scope: $scope});//继承



    //修改状态 确认发货
    $scope.updateStatus = function () {
        ordersService.updateStatus($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
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
        ordersService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

    // 显示支付类型
    $scope.status = ["","微信支付", "货到付款"];

    //显示状态
    $scope.statuss = ["","未付款", "已付款","未发货","已发货"];
    $scope.statusss = ["","app端", "pc端","M端","微信端","手机qq端"];



});	
