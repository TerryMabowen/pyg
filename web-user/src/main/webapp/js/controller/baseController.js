app.controller('baseController',function($scope){

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //定义一个空数组代表选中的集合
    $scope.selectIds=[];
    //根据复选框的勾选情况,根性selectIds数组
    $scope.updateSelection=function ($event,id) {
        //判断,是否勾选
        if($event.target.checked){
            //如果勾选,则将id加入到数组中
            $scope.selectIds.push(id);
        }else {
            //如果未勾选或取消勾选,根据id找到数组索引,再删除索引上的元素
            var ids = $scope.selectIds.indexOf(id);
            //第一个参数表示索引,第二个参数表示删除几个元素
            $scope.selectIds.splice(ids,1);
        }
    };

    // 定义方法：获取JSON字符串中的某个key对应值的集合
    $scope.jsonToString = function(jsonStr,key){
        // 将字符串转成JSON:
        var jsonObj = JSON.parse(jsonStr);
        var value = "";
        for(var i=0;i<jsonObj.length;i++){
            if(i>0){
                value += ",";
            }
            value += jsonObj[i][key];
        }
        return value;
    };
})