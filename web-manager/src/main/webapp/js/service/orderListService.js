app.service('orderListService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
       return $http.get('../orderList/findAll.do');
    }
    //分页查询
  this.findPage=function (page, rows) {
        return $http.get('../orderList/findPage.do?page='+page+'&rows='+rows);
    }
    // //订单
    this.getOrderList=function (sellerId) {
        return $http.get('../orderList/getOrderList.do?sellerId='+sellerId);
    }
})