app.service('userService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
       return $http.get('../user/findAll.do');
    }
    //分页查询
    this.findPage=function (page, rows) {
        return $http.get('../user/findPage.do?page='+page+'&rows='+rows);
    }
    //添加
    this.add=function (entity) {
        return $http.post('../user/add.do',entity);
    }
    //更新
    this.update=function (entity) {
        return $http.post('../user/update.do',entity);
    }
    //根据主键查询一条(修改时回显)
    this.findOne=function (id) {
        return $http.get('../user/findOne.do?id='+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get('../user/delete.do?ids='+ids);
    }
    //修改状态
    this.dele=function (ids) {
        return $http.get('../user/delete.do?ids='+ids);
    }
    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../user/search.do?page='+page+'&rows='+rows,searchEntity);
    }
    //获取品牌列表数据格式是封装的map对象
    this.selectOptionList = function () {
        return $http.get('../user/selectOptionList.do');
    }
    //上传excel文档解析
    this.uploadExcel = function () {
        // 向后台传递数据:
        var formData = new FormData();
        // 向formData中添加数据:
        formData.append("file", file.files[0]);

        return $http({
            method: 'post',
            url: '../upload/uploadExcel2Brand.do',
            data: formData,
            headers: {'Content-Type': undefined},// Content-Type : text/html  text/plain
            transformRequest: angular.identity
        });
    }

    this.updateStatus = function(ids,status){
        return $http.get('../user/updateStatus.do?ids='+ids+"&status="+status);
    }

})