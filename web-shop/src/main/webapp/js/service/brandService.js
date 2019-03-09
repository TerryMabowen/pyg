app.service('brandService',function ($http) {



    //更新
    this.update=function (id) {
        return $http.post('../brand/update.do?id='+id);
    }

    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&rows='+rows,searchEntity);
    }

})