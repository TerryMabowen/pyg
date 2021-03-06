app.controller('specificationController', function ($scope, shop_specificationService, $controller) {
    //继承baseController
    $controller('baseController', {$scope: $scope});
    //查询所有并展示
    /*$scope.findAll=function() {
        shop_specificationService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }*/

    //分页查询
    /*$scope.findPage=function(page,rows){
        shop_specificationService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }*/

    //保存
    $scope.save = function () {
        //方法名称
        var object;
        if ($scope.entity.specification.id != null) {
            //更新
            object = shop_specificationService.update($scope.entity);
        } else {
            //添加
            object = shop_specificationService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success == true) {
                    //保存成功
                    //重新查询
                    $scope.reloadList();
                } else {
                    //保存失败
                    alert(response.massage);
                }
            }
        )
    }

    //根据主键查询一条(修改时回显)
    $scope.findOne = function (id) {
        shop_specificationService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    //批量删除
    $scope.dele = function () {
        shop_specificationService.dele($scope.selectIds).success(
            function (response) {
                if (response.success == true) {
                    //重新刷新页面
                    $scope.reloadList();
                } else {
                    //弹出提示
                    alert(response.message);
                }
            }
        )
    }
    //批量提交申请
    $scope.updateStat= function () {
        shop_specificationService.updateStat($scope.selectIds).success(
            function (response) {
                if (response.success == true) {
                    //重新刷新页面
                    $scope.reloadList();
                } else {
                    //弹出提示
                    alert(response.message);
                }
            }
        )
    }

    //条件查询带分页
    //定义一个查询条件对象
    $scope.searchEntity = {};
    //条件查询
    $scope.search = function (page, rows) {
        shop_specificationService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    }

    //对集合进行初始化
    $scope.init = function () {
        $scope.entity = {specificationOptionList: []};
    }

    //新增选项行
    $scope.addTableRow = function () {
        $scope.entity.specificationOptionList.push({});
    }
    //删除选项行
    $scope.deleteTableRow = function (index) {
        $scope.entity.specificationOptionList.splice(index, 1);
    }
    // 显示状态
    $scope.stat1 = ["未申请", "已申请", "审核通过", "审核未通过"];
})