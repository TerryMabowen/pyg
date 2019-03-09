app.controller('itemCatController',function ($scope,itemCatService,$controller) {
    //继承baseController
    $controller('baseController',{$scope:$scope});

    //设置一个当前的分类级别,用来作为多级分类的条件
    $scope.grade = 1;
    $scope.setGrade = function (value) {
        $scope.grade = value;
    }
    //设置方法,用来显示分类,根据不同的级别跟随显示
    $scope.selectType=function (p_entity) {
        if($scope.grade == 1){
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if($scope.grade == 2){
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        }
        if($scope.grade == 3){
            $scope.entity_2 = p_entity;
        }
        //重新查询
        $scope.findByParentId(p_entity.id);
    }

    //设置上级id(初始化)
    $scope.parentId = 0;
    
    //查询所有并展示
   /* $scope.findAll=function() {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }*/

    //分页查询
    /*$scope.findPage=function(page,rows){
        itemCatService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }*/


    //上传excel表格解析
    $scope.uploadExcel = function () {
        // 调用uploadService的方法完成文件的上传
        itemCatService.uploadExcel().success(function (response) {
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
            object = itemCatService.update($scope.entity);
        }else{
            //将上级id赋值给$scope.entity的parentId
            $scope.entity.parentId=$scope.parentId;

          //添加
            object = itemCatService.add($scope.entity);
        }
        object.success(
            function (response) {
                if(response.success == true){
                    //保存成功
                    //重新查询
                    $scope.findByParentId($scope.parentId);
                }else {
                    //保存失败
                    alert(response.massage);
                }
            }
        )
    }

    //根据主键查询一条(修改时回显)
    $scope.findOne=function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    }

    //批量删除
    $scope.dele=function () {
       itemCatService.dele($scope.selectIds).success(
            function (response) {
                if(response.success == true){
                    //重新刷新页面
                    $scope.findByParentId($scope.parentId);
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
        itemCatService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

    //根据上级ID查询商品分类列表
    $scope.findByParentId=function (parentId) {
        //记住上级id
        $scope.parentId=parentId;

        itemCatService.findByParentId(parentId).success(
            function (response) {
                $scope.itemCatList = response;
            }
        )
    }

    // 显示状态
    $scope.status = ["未申请","未审核","审核通过","已驳回"];

    // 审核的方法:
    $scope.updateStatus = function(status){
        itemCatService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.success){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];//清空id集合
            }else{
                alert(response.message);
            }
        });
    }
})