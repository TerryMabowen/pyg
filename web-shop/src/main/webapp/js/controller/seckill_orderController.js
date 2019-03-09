//控制层
app.controller('seckill_orderController', function ($scope, $controller, $location, seckill_orderService, uploadService) {

    $controller('baseController', {$scope: $scope});//继承

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        // alert(id);
        seckill_orderService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        serviceObject = seckill_orderService.update($scope.entity); //修改
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);
                    $scope.entity = {};
                    location.href = "seckill_order_manager.html";
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //取消订单
    $scope.dele = function () {
        var id = $location.dele()['id'];
        alert("确认要取消" + id + "订单？");
        seckill_orderService.dele(id).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);

                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //确认发货
    $scope.updateStat = function () {
        //获取选中的复选框
        seckill_orderService.updateStat($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};
    //搜索
    $scope.search = function (page, rows) {
        seckill_orderService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    // 显示状态 0-已申请 1-已付款 2-已发货 3-已收件 4-取消订单
    $scope.status = ["已下单", "已付款", "已发货", "已收件", "取消订单"];

});	
