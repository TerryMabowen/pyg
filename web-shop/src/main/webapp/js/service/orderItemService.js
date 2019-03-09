app.service('orderItemService',function ($http) {



    //更新
    this.selectOrderItemList=function (id) {
        return $http.post('../orders/selectOrderItemList.do?id='+id);
    }

})