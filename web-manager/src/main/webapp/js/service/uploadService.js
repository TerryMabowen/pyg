app.service("uploadService", function ($http) {

    this.uploadFile = function () {
        // 向后台传递数据:
        var formData = new FormData();
        // 向formData中添加数据:
        formData.append("file", file.files[0]);

        return $http({
            method: 'post',
            url: '../upload/uploadFile.do',
            data: formData,
            headers: {'Content-Type': undefined},// Content-Type : text/html  text/plain
            transformRequest: angular.identity
        });
    };

    /*//上传excel文档解析
    this.uploadExcel = function () {
        // 向后台传递数据:
        var formData = new FormData();
        // 向formData中添加数据:
        formData.append("file", file.files[0]);

        return $http({
            method: 'post',
            url: '../upload/uploadExcel.do',
            data: formData,
            headers: {'Content-Type': undefined},// Content-Type : text/html  text/plain
            transformRequest: angular.identity

        });
    }*/
});