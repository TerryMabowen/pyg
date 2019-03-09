 //控制层 
app.controller('userController' ,function($scope,$controller,uploadService ,loginService  ,userService){

    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    };

	//注册用户
	$scope.reg=function(){
		
		//比较两次输入的密码是否一致
		if($scope.password!=$scope.entity.password){
			alert("两次输入密码不一致，请重新输入");
			$scope.entity.password="";
			$scope.password="";
			return ;			
		}
		//新增
		userService.add($scope.entity,$scope.smscode).success(
			function(response){
				alert(response.message);
			}		
		);
	};
    
	//发送验证码
	$scope.sendCode=function(){
		if($scope.entity.phone==null || $scope.entity.phone==""){
			alert("请填写手机号码");
			return ;
		}
		
		userService.sendCode($scope.entity.phone  ).success(
			function(response){
				alert(response.message);
			}
		);		
	};

   // $scope.formData = {'formData.nickName':'','formData.sex':'','formData.birthday':'','formData.provinceId':'','formData.cityId':'','formData.townId':'','formData.job':'','formData.headPic':''};
    $scope.uploadFile = function () {
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                // 获得url
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message);
            }
        });
    };


    //$scope.status = ["","男","女"];
   // entity.job = {'cxy':'程序员','cp':'产品经理','ui':'UI设计师'};
    $scope.showAddress=function () {
		userService.showAddress().success(
			function (response) {
				$scope.addList = response;
            }
		)
    };

    $scope.showUserInfo=function () {
        userService.showUserInfo().success(
            function (response) {
                $scope.userInfo=response;

            }
        )
    };



    $scope.updateUser=function () {
        userService.updateUser($scope.entity).success(
            function (response) {
                if(response.success){
                    alert(response.message);
                    location.reload();
                }else {
                    alert(response.message);
                }
            }
        )
    };

    //保存
    //保存
    $scope.save=function () {
        //方法名称
        var object;
        if($scope.entity.id != null){
            //更新
            object = userService.update($scope.entity);
        }else{
            //添加
            object = userService.addAddress($scope.entity);
        }
        object.success(
            function (response) {
                if(response.success == true){
                    //保存成功
                    //重新查询
                    location.reload();
                }else {
                    //保存失败
                    alert(response.massage);
                }
            }
        )
    };

    $scope.backShowAddress=function (id) {
        userService.backShowAddress(id).success(
            function (response) {
                $scope.entity=response;
            }
        )
    };

    $scope.dele=function (id) {
        userService.dele(id).success(
            function (response) {
                if(response.success){
                    //保存成功
                    //重新查询
                    alert(response.message);
                    location.reload();
                }else {
                    //保存失败
                    alert(response.message);
                }
            }
        )
    };

    $scope.collect=function () {
        userService.collect().success(
            function (response) {
                $scope.list=response;
            }
        )
    }

});	
