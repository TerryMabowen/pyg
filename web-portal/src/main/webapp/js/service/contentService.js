app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}

	//实战
    this.selectByParentId = function(parentId){
        return $http.get("itemCat/selectByParentId.do?parentId="+parentId);
    }

    //实战
    this.queryShow = function(floor){
        return $http.get("content/queryShow.do?floor="+floor);
    }
});