app.service('seckillOrderService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
        return $http.get('../seckillOrder/findAll.do');
    }
    //分页查询
    this.findPage=function (page, rows) {
        return $http.get('../seckillOrder/findPage.do?page='+page+'&rows='+rows);
    }
    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../seckillOrder/search.do?page='+page+'&rows='+rows,searchEntity);
    }
})