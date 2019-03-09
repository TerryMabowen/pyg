//服务层
app.service('ordersService',function($http){
	    	


	//搜索
	this.search=function(page,rows, searchEntity){
		return $http.post('../orders/search.do?page='+page+"&rows="+rows, searchEntity);
	}

    this.updateStatus=function (ids) {
        return $http.get('../orders/updateStatus.do?ids='+ids);

    }
});
