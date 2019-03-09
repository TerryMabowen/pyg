//控制层
app.controller('seckill_goodsController', function ($scope, $controller, $location, seckill_goodsService, uploadService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }
    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        // alert(id);
        seckill_goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        serviceObject = seckill_goodsService.update($scope.entity); //修改
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert(response.message);
                    $scope.entity = {};
                    location.href = "seckill_goods_manager.html";
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        seckill_goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    //批量提交审核
    $scope.updateStat = function () {
        //获取选中的复选框
        seckill_goodsService.updateStat($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }


    //搜索
    $scope.search = function (page, rows) {
        alert("111");
        seckill_goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    // $scope.entity={goods:{},goodsDesc:{},itemList:[]}

    $scope.uploadFile = function () {
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                // 获得smallPic
                $scope.entity.seckillGoods.smallPic = response.message;
            } else {
                alert(response.message);
            }
        });
    }

    // 获得了image_entity的实体的数据{"color":"褐色","url":"http://192.168.209.132/group1/M00/00/00/wKjRhFn1bH2AZAatAACXQA462ec665.jpg"}
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_iamge_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }


    // 显示状态 0-已申请 1-审核通过 2-审核未通过 3-未申请
    $scope.status = ["已申请", "审核通过", "审核未通过", "未申请"];

});	
