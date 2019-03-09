app.service('itemCatService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
       return $http.get('../itemCat/findAll.do');
    }
    //分页查询
  /*  this.findPage=function (page, rows) {
        return $http.get('../itemCat/findPage.do?page='+page+'&rows='+rows);
    }*/
    //添加
    this.add=function (entity) {
        return $http.post('../itemCat/add.do',entity);
    }
    //更新
    this.update=function (entity) {
        return $http.post('../itemCat/update.do',entity);
    }
    //根据主键查询一条(修改时回显)
    this.findOne=function (id) {
        return $http.get('../itemCat/findOne.do?id='+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get('../itemCat/delete.do?ids='+ids);
    }
    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../itemCat/search.do?page='+page+'&rows='+rows,searchEntity);
    }
    //根据上级ID查询商品分类列表
    this.findByParentId=function (parentId) {
        return $http.get('../itemCat/findByParentId.do?parentId='+parentId);
    }
    //修改状态
    this.updateStatus = function(ids,status){
        return $http.get('../itemCat/updateStatus.do?ids='+ids+"&status="+status);
    }
})