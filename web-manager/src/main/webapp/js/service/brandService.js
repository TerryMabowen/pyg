app.service('brandService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
       return $http.get('../brand/findAll.do');
    }
    //分页查询
    this.findPage=function (page, rows) {
        return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
    }
    //添加
    this.add=function (entity) {
        return $http.post('../brand/add.do',entity);
    }
    //更新
    this.update=function (entity) {
        return $http.post('../brand/update.do',entity);
    }
    //根据主键查询一条(修改时回显)
    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get('../brand/delete.do?ids='+ids);
    }
    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&rows='+rows,searchEntity);
    }
    //获取品牌列表数据格式是封装的map对象
    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList.do');
    }
    this.updateStatus = function(ids,status){
        return $http.get('../brand/updateStatus.do?ids='+ids+"&status="+status);
    }
})