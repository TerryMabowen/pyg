//服务层
app.service('seckill_orderService',function($http){

	//查询实体
	this.findOne=function(id){
		return $http.get('../seckill_order/findOne.do?id='+id);
	}
	//取消订单
	this.dele=function(id){
		return $http.get('../seckill_order/delete.do?id='+id);
	}
    //确认已发货
    this.updateStat=function(ids){
        return $http.get('../seckill_order/updateStat.do?ids='+ids);
    }
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seckill_order/search.do?page='+page+"&rows="+rows, searchEntity);
	}    	
});
