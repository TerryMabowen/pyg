//服务层
app.service('seckillService',function($http){
	//读取列表数据绑定到表单中
	this.findSeckillOrderList=function(){
		return $http.get('../seckillOrder/findSeckillOrderList.do');
	}

    this.cancelOrder=function(id){
        return $http.get('../seckillOrder/cancelOrder.do?orderId='+id);
    }

});