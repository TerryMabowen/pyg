app.controller('brandController',function ($scope,brandService,$controller) {
    //继承baseController
    $controller('baseController',{$scope:$scope});
    //查询所有并展示
   /* $scope.findAll=function() {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }*/

    //分页查询
    /*$scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }*/

    //保存
    $scope.save=function () {
        //方法名称
        var object;
        if($scope.entity.id != null){
          //更新
            object = brandService.update($scope.entity);
        }else{
          //添加
            object = brandService.add($scope.entity);
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
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    }

    //批量删除
    $scope.dele=function () {
       brandService.dele($scope.selectIds).success(
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
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }
})