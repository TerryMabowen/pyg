app.controller('typeTemplateController',function ($scope,typeTemplateService,$controller,brandService,specificationService) {
    //继承baseController
    $controller('baseController',{$scope:$scope});
    //查询所有并展示
   /* $scope.findAll=function() {
        typeTemplateService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }*/

    //分页查询
    /*$scope.findPage=function(page,rows){
        typeTemplateService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }*/

    //上传excel表格解析
    $scope.uploadExcel = function () {
        // 调用uploadService的方法完成文件的上传
        typeTemplateService.uploadExcel().success(function (response) {
            if (response.success == true) {
                alert(response.message)
                $scope.reloadList();//重新加载
            } else {
                alert(response.message);
            }
        });
    }

    //保存
    $scope.save=function () {
        //方法名称
        var object;
        if($scope.entity.id != null){
          //更新
            object = typeTemplateService.update($scope.entity);
        }else{
          //添加
            object = typeTemplateService.add($scope.entity);
        }
        object.success(
            function (response) {
                if(response.success == true){
                    //保存成功
                    //重新查询
                    $scope.reloadList();
                }else {
                    //保存失败
                    alert(response.massage);
                }
            }
        )
    }

    //根据主键查询一条(修改时回显)
    $scope.findOne=function (id) {
        typeTemplateService.findOne(id).success(
            function (response) {
                $scope.entity=response;
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                $scope.entity.specIds = JSON.parse($scope.entity.specIds);
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
            }
        )
    }

    //批量删除
    $scope.dele=function () {
       typeTemplateService.dele($scope.selectIds).success(
            function (response) {
                if(response.success == true){
                    //重新刷新页面
                    $scope.reloadList();
                }else {
                    //弹出提示
                    alert(response.message);
                }
            }
        )
    }

    //条件查询带分页
    //定义一个查询条件对象
    $scope.searchEntity={};
    //条件查询
    $scope.search=function (page,rows) {
        typeTemplateService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

    //查询所有品牌列表
    $scope.brandList={data:[]};//可以避免页面的一些初始值错误
    $scope.findBrandList = function () {
        brandService.selectOptionList().success(
            function(response){
                $scope.brandList = {data:response};
            }
        )
    }

    //查询所有规格列表
    $scope.specList={data:[]};//可以避免页面的一些初始值错误
    $scope.findSpecList = function () {
        specificationService.selectOptionList().success(
            function(response){
                $scope.specList = {data:response};
            }
        )
    }
    //
    //$scope.entity.customAttributeItems.push
    //对集合进行初始化
    $scope.init = function(){
        $scope.entity = {customAttributeItems:[]};
    };


    //$scope.entity={customAttributeItems:[]};
    //给扩展属性添加行
    $scope.addTableRow = function(){
        $scope.entity.customAttributeItems.push({});
    };
    //删除行
    $scope.deleteTableRow = function(index){
        $scope.entity.customAttributeItems.splice(index,1);
    };

    // 显示状态
    $scope.status = ["未申请","未审核","审核通过","已驳回"];

    // 审核的方法:
    $scope.updateStatus = function(status){
        typeTemplateService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.success){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];//清空id集合
            }else{
                alert(response.message);
            }
        });
    }
})