//服务层
app.service('seckill_goodsService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seckill_goods/findAll.do');
	}

	//查询实体
	this.findOne=function(id){
		return $http.get('../seckill_goods/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seckill_goods/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seckill_goods/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seckill_goods/delete.do?ids='+ids);
	}
    //提交审核
    this.updateStat=function(ids){
        return $http.get('../seckill_goods/updateStat.do?ids='+ids);
    }
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seckill_goods/search.do?page='+page+"&rows="+rows, searchEntity);
	}    	
});
