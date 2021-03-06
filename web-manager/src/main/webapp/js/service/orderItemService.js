app.service('orderItemService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
        return $http.get('../orderItem/findAll.do');
    }
    //分页查询
    this.findPage=function (page, rows) {
        return $http.get('../orderItem/findPage.do?page='+page+'&rows='+rows);
    }
    //添加
    this.add=function (entity) {
        return $http.post('../orderItem/add.do',entity);
    }
    //更新
    this.update=function (entity) {
        return $http.post('../orderItem/update.do',entity);
    }
    //根据主键查询一条(修改时回显)
    this.findOne=function (id) {
        return $http.get('../orderItem/findOne.do?id='+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get('../orderItem/delete.do?ids='+ids);
    }
    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../orderItem/search.do?page='+page+'&rows='+rows,searchEntity);
    }
    //获取品牌列表数据格式是封装的map对象
    this.selectOptionList = function () {
        return $http.get('../orderItem/selectOptionList.do');
    }
})