app.controller('typeTemplateController', function ($scope, shop_brandService, shop_typeTemplateService,shop_specificationService, $controller) {
        $controller('baseController', {$scope: $scope});//前端的假继承

        //读取列表数据绑定到表单中
        $scope.findAll = function () {
            shop_typeTemplateService.findAll().success(
                function (response) {
                    $scope.list = response;
                }
            );
        }


        //查询实体
        $scope.findOne = function (id) {
            shop_typeTemplateService.findOne(id).success(
                function (response) {
                    $scope.entity = response;
                    // eval()   JSON.parse();
                    $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);

                    $scope.entity.specIds = JSON.parse($scope.entity.specIds);

                    $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);


                }
            );
        };

        //保存
        $scope.save = function () {
            //var methodName = 'add';//方法名称
            if ($scope.entity.id != null) {//如果有ID
                // methodName = 'update';//则执行修改方法
                shop_typeTemplateService.update($scope.entity).success(
                    function (response) {
                        if (response.success) {
                            //重新查询
                            $scope.reloadList();//重新加载
                        } else {
                            alert(response.message);
                        }
                    }
                );
            } else {
                shop_typeTemplateService.add($scope.entity).success(
                    function (response) {
                        if (response.success) {
                            //重新查询
                            $scope.reloadList();//重新加载
                        } else {
                            alert(response.message);
                        }
                    }
                );
            }

        };

        //批量删除
        $scope.dele = function () {
            //获取选中的复选框
            shop_typeTemplateService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                    }
                }
            );
        };

    //批量提交审核
    $scope.updateStat = function () {
        //获取选中的复选框
        shop_typeTemplateService.updateStat($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    };

        $scope.searchEntity = {};//定义搜索对象
        //条件查询
        $scope.search = function (page, rows) {
            shop_typeTemplateService.search(page, rows, $scope.searchEntity).success(
                function (response) {
                    $scope.paginationConf.totalItems = response.total;//总记录数
                    $scope.list = response.rows;//给列表变量赋值
                }
            );
        };




        $scope.brandList = {data: []}
        // $scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};//品牌列表
        // 查询关联的品牌信息:
        $scope.findBrandList = function () {
            shop_brandService.selectOptionList().success(function (response) {
                $scope.brandList = {data: response};
            });
        }

        $scope.specList = {data: []}
        // 查询关联的规格信息:
        $scope.findSpecList = function () {
            shop_specificationService.selectOptionList().success(function (response) {
                $scope.specList = {data: response};
            });
        }

        //给扩展属性添加行
        $scope.entity = {customAttributeItems: []};
        $scope.addTableRow = function () {
            $scope.entity.customAttributeItems.push({});
        }

        $scope.deleteTableRow = function (index) {
            $scope.entity.customAttributeItems.splice(index, 1);
        }

    // 显示状态
    $scope.stat1 = ["未申请", "已申请", "审核通过", "审核未通过"];
    }
);
