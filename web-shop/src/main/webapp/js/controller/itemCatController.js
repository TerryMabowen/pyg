app.controller('itemCatController', function ($scope, shop_itemCatService, $controller) {
    //继承baseController
    $controller('baseController', {$scope: $scope});

    //设置一个当前的分类级别,用来作为多级分类的条件
    $scope.grade = 1;
    $scope.setGrade = function (value) {
        $scope.grade = value;
    }
    //设置方法,用来显示分类,根据不同的级别跟随显示
    $scope.selectType = function (p_entity) {
        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.entity_2 = p_entity;
        }
        //重新查询
        $scope.findByParentId(p_entity.id);
    }

    //设置上级id(初始化)
    $scope.parentId = 0;

    //保存
    $scope.save = function () {
        //方法名称
        var object;
        if ($scope.entity.id != null) {
            //更新
            object = shop_itemCatService.update($scope.entity);
        } else {
            //将上级id赋值给$scope.entity的parentId
            $scope.entity.parentId = $scope.parentId;

            //添加
            object = shop_itemCatService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success == true) {
                    //保存成功
                    //重新查询
                    $scope.findByParentId($scope.parentId);
                } else {
                    //保存失败
                    alert(response.massage);
                }
            }
        )
    }

    //根据主键查询一条(修改时回显)
    $scope.findOne = function (id) {
        shop_itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    //批量删除
    $scope.dele = function () {
        shop_itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success == true) {
                    //重新刷新页面
                    $scope.findByParentId($scope.parentId);
                } else {
                    //弹出提示
                    alert(response.message);
                }
            }
        )
    }

    //批量提交审核
    $scope.updateStat = function () {
        shop_itemCatService.updateStat($scope.selectIds).success(
            function (response) {
                if (response.success == true) {
                    //重新刷新页面
                    $scope.findByParentId($scope.parentId);
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

    //分页查询
    $scope.search = function (page, rows) {
        shop_itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.itemCatList = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    }


   /* //条件查询不带分页
    $scope.search2 = function () {
        shop_itemCatService.search2($scope.searchEntity).success(
            function (response) {
                $scope.itemCatList = response;

            }
        )
    }*/


    //根据上级ID查询商品分类列表
    $scope.findByParentId = function (parentId) {
        //记住上级id
        $scope.parentId = parentId;

        shop_itemCatService.findByParentId(parentId).success(
            function (response) {
                $scope.itemCatList = response;
            }
        )
    }
    // 显示状态
    $scope.stat1 = ["未申请", "已申请", "审核通过", "审核未通过"];
})