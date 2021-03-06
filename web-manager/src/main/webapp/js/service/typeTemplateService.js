app.service('typeTemplateService',function ($http) {
    //查询所有并展示
    this.findAll=function () {
       return $http.get('../typeTemplate/findAll.do');
    }
    //分页查询
    this.findPage=function (page, rows) {
        return $http.get('../typeTemplate/findPage.do?page='+page+'&rows='+rows);
    }
    //添加
    this.add=function (entity) {
        return $http.post('../typeTemplate/add.do',entity);
    }
    //更新
    this.update=function (entity) {
        return $http.post('../typeTemplate/update.do',entity);
    }
    //根据主键查询一条(修改时回显)
    this.findOne=function (id) {
        return $http.get('../typeTemplate/findOne.do?id='+id);
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get('../typeTemplate/delete.do?ids='+ids);
    }
    //条件查询带分页
    this.search=function (page, rows, searchEntity) {
        return $http.post('../typeTemplate/search.do?page='+page+'&rows='+rows,searchEntity);
    }
    //模板审批
    this.updateStatus = function(ids,status){
        return $http.get('../typeTemplate/updateStatus.do?ids='+ids+"&status="+status);
    }

    //上传excel文档解析
    this.uploadExcel = function () {
        // 向后台传递数据:
        var formData = new FormData();
        // 向formData中添加数据:
        formData.append("file", file.files[0]);

        return $http({
            method: 'post',
            url: '../upload/uploadExcel2Template.do',
            data: formData,
            headers: {'Content-Type': undefined},// Content-Type : text/html  text/plain
            transformRequest: angular.identity
        });
    }
})